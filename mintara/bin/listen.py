#!/usr/bin/python3
"""
 Starts the Svom system alert distribution prototype.
 Checks the Svom Nats server.

 Usage info: listen.py
 export NATS_URL

"""

#_______________________________________________________________________________

# Henri Louvin - henri.louvin@cea.fr
# Adaptation by Jean-Paul Le Fèvre for Mintara
#_______________________________________________________________________________

# System
import os
import sys

# Logs
import logging

# Asyncio
import asyncio

# NATS
from natsio import NatsIo

LOG = logging.getLogger('example')
logging.basicConfig(stream=sys.stdout, level=logging.INFO,\
        format='%(asctime)s %(levelname)s [%(name)s] %(message)s')

def sub_and_log(client, queue):
    """
    Define response function as asynchronous coroutine
    """
    @asyncio.coroutine
    def print_to_log(msg):
        """
        Basic function printing message content to log
        """
        LOG.info('Message received on queue \'%s\': %s', queue, msg.data)

    @asyncio.coroutine
    def reply_handler(msg):
        """
        Handler illustrating the reply request usage
        A response *has to be sent* when a message with reply is received
        """
        if 'reply' in dir(msg):
            LOG.info('Message received on queue \'%s\': %s', queue, msg.data)
            LOG.info('Sending response now.')
            client.publish(msg.reply, b'that parrot is definitely deceased.')
        else:
            LOG.info('Message without reply-to on  \'%s\': %s', queue, msg.data)

    # Subscribe to queue and define reaction function
    client.subscribe(queue, print_to_log)
    client.subscribe(queue, reply_handler)


def main():
    """
    Listens to activity queue

    """
    nats_url = 'nats://svom:mot2passe@localhost:4222'

    try:
        nats_url = os.environ['NATS_URL']
    except KeyError:
        LOG.warning('Empty environment variable NATS_URL')

    # Connect to NATS server

    nats_client = NatsIo(nats_url, streaming_id='sub-example')

    # Subscribe to queue
    activity_queue = 'activity.infrastructure'
    LOG.info('Listening messages from %s on queue %s', nats_url, activity_queue)

    sub_and_log(nats_client, activity_queue)

#_______________________________________________________________________________
