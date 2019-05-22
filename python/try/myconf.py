#!/usr/bin/env python
# -*- coding: utf-8 -*-

#___________________________________________________________________________

"""
Examine the content of the config file.

Usage: myconf.py
The config file is supposed to be ~/.mypy.cfg

"""

__version__ = "$Revision: 628 $"

import sys
import os
import getopt
import jplib
import ConfigParser

main_v = 1

    
def usage():
    """ Print the accepted options """
    sys.stderr.write(__doc__)
    sys.exit(1)

def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, "h", ["help"])
        
    except getopt.GetoptError:          
        usage()                         
        
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()

    rsrc = ConfigParser.SafeConfigParser()
    rsrc.read(os.path.expanduser('~/.mypy.cfg'))

    print
    prop = rsrc.defaults();
    print 'Number of def values :', len(prop)
    for k, v in prop.iteritems():
        print k, ' : ', v

    print
    sections = rsrc.sections();
    print 'Number of sections :', len(sections)
    for s in sections:
        print s;
        for k,v in rsrc.items(s):
            print '  ', k, ' : ', v
        
if __name__ == "__main__":
    main(sys.argv[1:])

#___________________________________________________________________________
