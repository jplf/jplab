#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Make a count available to clients.

Usage: count_server.py
It is meant to learn how to use twisted and websockets.
The server, once started, run forever. A ctrl-c will kill it.

"""

__version__ = "$Revision: 507 $"

import sys
import getopt
from twisted.python import log
from twisted.internet import reactor
from autobahn.websocket import WebSocketServerFactory, WebSocketServerProtocol, listenWS

count   = 0
factory = None

def usage():
    """ Print the accepted options """
    sys.stderr.write(__doc__)
    sys.exit(1)

def doCount():
    """ Increment the counter """
    
    global count
    count += 1
    if (count > 99):
        count = 0

def countForEver():
    """ Keep on incrementing the counter """
    doCount()
    reactor.callLater(2, countForEver)
 
class CountServerProtocol(WebSocketServerProtocol):
    """ Manage the dialog with the clients """
    
    def __init__(self):
        self.client = None
           
    def onConnect(self, connectionRequest):
        host = connectionRequest.host
        self.client = host.split(':')[0]
        print 'New incoming connection from ' + self.client
        n = self.factory.getConnectionCount()
        print 'Number of connections : ' + str(n)
  
    def onMessage(self, msg, binary):
        global count

        if msg == '+':
            doCount()
            print 'Count set to ' + str(count)
            self.sendMessage('Count : ' + str(count))

        elif msg == 'q':
            print 'Server stopped by ' + self.client
            reactor.stop()

        else:
            self.sendMessage('Count : ' + str(count))

    def connectionMade(self):
        WebSocketServerProtocol.connectionMade(self)
        print 'Connection established'
        
    def connectionLost(self, reason):
        print 'Connection lost by ' + self.client
        self.client = None
        WebSocketServerProtocol.connectionLost(self, reason)

      
def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, 'h', ['help'])
        
    except getopt.GetoptError:          
        usage()                         
        
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()

    print 'The count server is gonna start ...'
    
    factory = WebSocketServerFactory('ws://localhost:9000')
    factory.protocol = CountServerProtocol
    listenWS(factory)
    
    reactor.callWhenRunning(countForEver)
    reactor.run()

if __name__ == "__main__":
    main(sys.argv[1:])
