#! /usr/bin/python3
"""
Checks the Svom Nats server.

Usage info: talk.py
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

# NATS
from natsio import NatsIo

LOG = logging.getLogger('example')
logging.basicConfig(stream=sys.stdout, level=logging.INFO,\
        format='%(asctime)s %(levelname)s [%(name)s] %(message)s')

def main():
    """
    Talks to activity queue

    """
    nats_url = 'nats://svom:mot2passe@localhost:4222'

    try:
        nats_url = os.environ['nats_url']
    except KeyError:
        LOG.warning('Empty environment variable nats_url')

    # Connect to the NATS server
    client = NatsIo(nats_url, streaming_id='pub-example')

    # Subscribe to queue
    queue = 'activity.infrastructure'
    import time

    for _ in range(2):# pylint: disable=unused-variable
        time.sleep(1)
        LOG.info('Sending message to %s on queue %s', nats_url, queue)
        # publish example without reply request (preferred)
        client.publish(queue, 'i am groot')
        client.publish("decode", 'i am groot')

    client.stop()

#_______________________________________________________________________________
