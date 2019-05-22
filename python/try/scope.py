#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Probe the language features.

Usage: scope.py

"""

__version__ = "$Revision: 640 $"

import os
import sys
import getopt
import logging
import ConfigParser
import jplib

main_v = 1

def probe(s):
    """ Figure out how python behaves. """
    print s, ' ', main_v
    
def usage():
    """ Print the accepted options """
    sys.stderr.write(__doc__)
    sys.exit(1)

def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, 'hd', ['help', 'debug'])
        
    except getopt.GetoptError:          
        usage()                         

    appli = 'scope'
    debug = False

    rsrc = ConfigParser.SafeConfigParser()
    rsrc.read(os.path.expanduser('~/.mypy.cfg'))

    logger = logging.getLogger(appli)
    logger.setLevel(logging.NOTSET)
    level = rsrc.get(appli, 'log_level').lower()

    if level == 'quiet':
        logger.setLevel(logging.NOTSET)
    elif level == 'verbose':
        logger.setLevel(logging.INFO) 
    elif level == 'debug':
        logger.setLevel(logging.DEBUG)
    else:
        logger.setLevel(logging.WARN)
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-d', '--debug'):
            debug = True
    
    if debug:
        logger.setLevel(logging.DEBUG)

    if rsrc.has_option(appli, 'log_file'):
        filename = rsrc.get(appli, 'log_file')
        handler = logging.FileHandler(filename)
    else:
        handler = logging.StreamHandler(sys.stderr)

    if rsrc.has_option(appli, 'log_format'):
        format = rsrc.get(appli, 'log_format', True)
        handler.setFormatter(logging.Formatter(format))

    logger.addHandler(handler)
    
    probe('Salut Jaypee Le Fèvre !')
    
    logger.debug('This is a debug message')
    logger.warning('This is an warning message')
    logger.info('This is an information message')

if __name__ == '__main__':
    main(sys.argv[1:])
