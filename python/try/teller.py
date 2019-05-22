#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Tell something to a listener.

Usage: teller.py [-t hostname]
The server must be running.

"""

__version__ = "$Revision: 500 $"

import sys
import getopt
import socket

def usage():
    sys.stderr.write(__doc__)
    sys.exit(1)

def main(argv):

    hostname = 'localhost'
    
    try:                                
        opts, args = getopt.getopt(argv, 'ht:', ['help', "to"])
        
    except getopt.GetoptError:          
        usage()                         
        
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-t', '--to'):
            hostname = val

    try:
        the_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        the_socket.connect((hostname, 1951))

    except socket.error, (value, message):
        print 'Can''t connect to the server :', message
        sys.exit(1)
        
    print 'Enter "n" to terminate the client, "k" to kill the server.'

    while True:
        line = raw_input('> ')

        print 'read :', line
        line = line.lower()

        if line in ('n', 'no', 'non'):
            break
        the_socket.send(line)

    the_socket.close()
    print 'Hasta luego !'
    
if __name__ == "__main__":
    main(sys.argv[1:])
