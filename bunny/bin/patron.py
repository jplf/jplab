#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
A simple message consumer.

Usage: patron.py

It receives messages sent by a greeter of a worker.
It is the python version of java org.svom.bunny.worker.Patron.

"""

# First run 'pip install pika' if needed.
# See: https://www.rabbitmq.com/tutorials/tutorial-three-python.html

import sys
import pika
import getopt
import time
from datetime import date

def usage():
    """
    Prints the accepted options
    """
    sys.stdout.write(__doc__)
    sys.exit(1)

def process():
    """
    Initializes the RabbitMQ stuff, wait for messages, prints them.

    """
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='hurukan'))
    channel = connection.channel()
    channel.exchange_declare(exchange='bunny', type='fanout',
                             durable=False, auto_delete=True)
    
    result = channel.queue_declare(durable=False, exclusive=False,
                                   auto_delete=True)
    queue_name = result.method.queue

    channel.queue_bind(exchange='bunny', queue=queue_name)

    def callback(canal, method, properties, body):
        
        print("Message: %s" % body)
        try:
            i = body.index('=')
            content = body[(i+1):].strip()
        except:
            content = body
            
        print("Content: %s" % content)
        if content == 'close':
            channel.close()
            connection.close()
            print 'Patron is dead !'

    print 'Patron is listening ...'
    channel.basic_consume(callback, queue=queue_name, no_ack=True)
    channel.start_consuming()
    
def main(argv):

    try:                                
        opts, args = getopt.getopt(argv, 'h', ['help'])

    except getopt.GetoptError:          
          usage()                         
   
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
       
    process()

if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
