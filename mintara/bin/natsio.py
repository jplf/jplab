##_______________________________________________________________________________
# NATS client using asyncio-nat

# Created on 18 jun. 2017
# @author: Patrick Maeght
# From https://github.com/
# nats-io/asyncio-nats/blob/master/examples/coroutine_threadsafe.py

# NATS client using asyncio asyncio-nats-streaming
# Updated on 18 jun. 2018
# Henri Louvin - henri.louvin@cea.fr
#_______________________________________________________________________________

import threading
import time
import asyncio

import sys
import logging

LOG = logging.getLogger('nats_client')

# NATS
from nats.aio.client import Client as NATS
from nats.aio.errors import ErrConnectionClosed, ErrTimeout

# Check availability of asyncio-nats-streaming
has_streaming = True
try:
    from stan.aio.client import Client as STAN
except ImportError:
    has_streaming = False

class NATSClient(threading.Thread):
    """
    Threaded asyncio NATS client
    """
    def __init__(self, nc, loop):
        """Init asyncio thread"""
        threading.Thread.__init__(self)
        self.nc = nc
        self.loop = loop

    # called by thread.start()
    def run(self):
        """Start asyncio loop"""
        LOG.debug("Starting async loop")
        self.loop.run_forever()

class NatsIo(object):
    """
    asyncio NATS client

    """
    def __init__(self, host='127.0.0.1', port='4222',
                 streaming_id=None, cluster='svom-cluster'):
        self.nc = NATS()
        self.server = host
        self.is_connected = False
        self.streaming_id = streaming_id
        self.cluster = cluster
        self.sc = None
        self.loop = None
        try:
            self.loop = asyncio.get_event_loop()
        except RuntimeError:
            LOG.info('Failed to retrieve asyncio event loop. Creating a new one...')
            self.loop = asyncio.new_event_loop()


        self.loop.run_until_complete(self._connect(servers=[self.server]))
        if streaming_id is not None:
            if has_streaming is True:
                self.sc = STAN()
                self.loop.run_until_complete(self._stan_connect())
            else:
                LOG.error("Can't run client without asyncio-nats-streaming")
                sys.exit(1)

        self.nats_server = NATSClient(self.nc, self.loop)
        self.nats_server.start()

    def is_streaming(self):
        return True if self.streaming_id is not None else False

    def _connect(self, servers):
        """connect"""
        while not self.is_connected:
            try:
                LOG.info('Attempting connection to nats server '+servers[0])
                yield from self.nc.connect(servers=servers, io_loop=self.loop)
                yield from self.nc.flush()
            except Exception:
                LOG.exception('Connection failed.')
                LOG.info('Retrying connection in 5s.')
                yield from asyncio.sleep(5)
            else:
                self.is_connected = True
                LOG.info('Connected.')

        if self.streaming_id is not None:
            self.is_connected = False

    @asyncio.coroutine
    def _stan_connect(self):
        """connect"""
        while not self.is_connected:
            try:
                LOG.info('Attempting connection to server cluster \''
                            + self.cluster+
                            '\' with clientID \''+self.streaming_id+'\'')
                yield from self.sc.connect(self.cluster, self.streaming_id,
                                           nats=self.nc)
            except Exception:
                LOG.exception('Connection failed.')
                LOG.info('Retrying streaming server connection in 5s.')
                yield from asyncio.sleep(5)
            else:
                self.is_connected = True
                LOG.info('Connected to streaming server.')

    def subscribe(self, subject, handler=None):
        """launch subscription as async task"""
        if not self.is_connected:
            LOG.info("Awaiting to be connected to subscribe")
            self.loop.call_later(5, self.subscribe, subject, handler)
        if handler is None:
            handler = self._default_handler
        if self.streaming_id is None:
            asyncio.run_coroutine_threadsafe(self._subscribe(subject, handler), loop=self.loop)
        else:
            asyncio.run_coroutine_threadsafe(self._stan_subscribe(subject, handler), loop=self.loop)

    @asyncio.coroutine
    def _subscribe(self, subject, handler):
        """new subscription or handler change"""
        LOG.info('Subscribing to subject \'' + subject + '\'')
        try:
            yield from self.nc.subscribe(subject, cb=handler)
        except Exception:
            LOG.exception('Subscription failed.')
        else:
            LOG.info('Subscribed to subject  \'' + subject + '\'')

    @asyncio.coroutine
    def _stan_subscribe(self, subject, handler):
        """new subscription or handler change"""
        LOG.info('Subscribing to subject \'' + subject + '\'')
        try:
            # No need to custom durable name since 'clientID+durable name' is checked
            yield from self.sc.subscribe(subject, durable_name=handler.__name__, cb=handler)
        except Exception as e:
            LOG.exception('Subscription to \'%s\' failed' % subject)
        else:
            LOG.info('Handler \'%s\' subscribed to subject \'%s\'' % (handler.__name__, subject))

    def publish(self, subject, data, with_reply=False):
        """launch publish as async task"""
        if isinstance(data, str):
            data = data.encode()
        if with_reply is False:
            if self.streaming_id is None:
                asyncio.run_coroutine_threadsafe(self._publish(subject, data),
                                                 loop=self.loop)
            else:
                asyncio.run_coroutine_threadsafe(self._stan_publish(subject,\
                                                 data), loop=self.loop)
        else:
            if self.streaming_id is None:
                future = asyncio.run_coroutine_threadsafe(\
                    self._publish(subject,\
                    data, with_reply=True), loop=self.loop)
                try:
                    result = future.result(1)
                    if result is None:
                        LOG.error('Message received no response')
                        return None
                except Exception:
                    LOG.exception('Timed request publishing exception')
                else:
                    return result
            else:
                LOG.error('NATS Streaming publish cannot handle replies')

    @asyncio.coroutine
    def _publish(self, subject, data, with_reply=False):
        """Nats transport data : bytes or str"""
        try:
            if with_reply is False:
                yield from self.nc.publish(subject, data)
            else:
                yield from self.nc.timed_request(subject, data)
        except Exception as e:
            LOG.exception('Publication failed.')

    @asyncio.coroutine
    def _stan_publish(self, subject, data):
        """Nats transport data : bytes or str"""
        try:
            yield from self.sc.publish(subject,\
                                     data, ack_handler=self._ack_handler)
        except Exception as e:
            LOG.exception('Publication failed, is \'' + \
                           subject + '\' a valid channel?')

    @asyncio.coroutine
    def _default_handler(self, msg):
        """Application handler data : str"""
        LOG.debug("--- Received: " + msg.subject +  msg.data.decode())
        if self.streaming_id is not None:
            yield from self.sc.ack(msg)

    @asyncio.coroutine
    def ack(self, msg):
        yield from self.sc.ack(msg)

    @asyncio.coroutine
    def _ack_handler(self, ack):
        LOG.debug('Delivery ack received: {}'.format(ack.guid))

    def stop(self):
        LOG.debug('Trying to stop properly NatsIo: ')
        asyncio.run_coroutine_threadsafe(self.nc.flush(), loop=self.loop)
        asyncio.run_coroutine_threadsafe(self.nc.close(), loop=self.loop)
        try:
            time.sleep(1)
            self.loop.call_soon_threadsafe(self.loop.stop)
        except Exception as e:
            LOG.debug(e)
        try:
            time.sleep(1)
            if self.loop.is_running():
                LOG.debug('loop.is_running: ' + str(self.loop.is_running()))
                self.loop.call_soon_threadsafe(self.loop.close())
        except Exception as e:
            LOG.debug(e)
        self.nats_server.join()
#_______________________________________________________________________________
