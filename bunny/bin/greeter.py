#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
A simple message producer.

Usage: greeter.py some_id

It launches a loop. Press 'q' or 'x' to exit.
It is the python version of java org.svom.bunny.worker.Greeter.

"""

# First run 'pip install pika' if needed.
# See: https://www.rabbitmq.com/tutorials/tutorial-three-python.html

import sys
import pika
import getopt
import time
from datetime import date

debug = False

def usage():
    """
    Prints the accepted options
    """
    sys.stdout.write(__doc__)
    sys.exit(1)

def process(greeter_id):
    """
    Initializes the RabbitMQ stuff, asks for messages, sends them.

    """
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host='hurukan'))
    channel = connection.channel()

    channel.exchange_declare(exchange='bunny', type='fanout',
                             durable=False, auto_delete=True)
    
    print 'Enter "x" or "q" to terminate. Or "close" to kill the consumers.'

    count = 0
    
    while True:
        line = raw_input('> ')

        if line in ('x', 'q'):
            break
        elif '=' in line:
            print 'Forbidden character "=" in input !'
            continue
        
        now = time.strftime('%H:%M:%S')
        count += 1
        message = now + ' message ' + str(count) + ' from ' + greeter_id
        message += ' = ' + line
        
        print 'Sending', message
        channel.basic_publish(exchange='bunny', routing_key='', body=message)
        
    connection.close()
    print 'Greeter', greeter_id, 'is done !'
    
def main(argv):

    try:                                
        opts, args = getopt.getopt(argv, 'h', ['help'])

    except getopt.GetoptError:          
          usage()                         

    greeter_id = None
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()

    if len(args) > 0:
        greeter_id = args[0]
    else:
        greeter_id = 'unknown'
        
    process(greeter_id)

if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
