#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Interact with a server of counts.

Usage: count_client.py [-w ws_url] [-o operation]
Operations : p (print), + (increment)

"""

__version__ = "$Revision: 507 $"

import sys
import getopt
from twisted.internet import reactor
from autobahn.websocket import WebSocketClientFactory, WebSocketClientProtocol, connectWS

def usage():
    """ Print the accepted options. """
    sys.stderr.write(__doc__)
    sys.exit(1)

class CountClientProtocol(WebSocketClientProtocol):

    def __init__(self):
        self.op = 'p'
        
    def setOp(self, op):
        self.op = op
        
    def onOpen(self):
        self.sendMessage('p')
 
    def onMessage(self, msg, binary):
        print 'Current count : ' + str(msg)
        line = raw_input('> ')
        print 'read :', line[0]
        self.sendMessage( line[0])
 
def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, 'hw:o:',
                        ['help', 'ws=', 'operation='])
        
    except getopt.GetoptError:          
        usage()

    ws = 'ws://localhost:9000'
    op = 'p'
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-o', '--operation'):
            op = val
        elif opt in ('-w', '--ws'):
            ws = val

    factory = WebSocketClientFactory(ws, debug = True)
    factory.protocol = CountClientProtocol
    connectWS(factory)
    reactor.run()

if __name__ == "__main__":
    main(sys.argv[1:])
