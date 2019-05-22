#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Listen to incoming data sent by a client.

Usage: listener.py

"""

__version__ = "$Revision: 499 $"

import sys
import getopt

from twisted.internet import reactor
from twisted.internet.protocol import Protocol, Factory
from twisted.internet.endpoints import TCP4ServerEndpoint

def usage():
    sys.stderr.write(__doc__)
    sys.exit(1)

class HandleRequest(Protocol):
    """ Process incoming request from clients. """

    def connectionMade(self):
        """ Handle data. """
        print "I'm ready to handle requests !"
        
    def dataReceived(self, data):
        """ Handle data. """
        print "Got", data
        s = data.lower()
        if s in ('k', 'kill'):
            reactor.stop()
        

def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, 'h', ['help'])
        
    except getopt.GetoptError:          
        usage()                         
        
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()

    factory = Factory()
    factory.protocol = HandleRequest
    
    endpoint = TCP4ServerEndpoint(reactor, 1951)
    endpoint.listen(factory)
    
    print "I'm listening !"
    reactor.run()
    
    print 'Bye !'
    

if __name__ == '__main__':
    main(sys.argv[1:])
