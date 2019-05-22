#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Just say 'hi'.

Usage: hijp.py

"""

__version__ = "$Revision$"

import sys
import getopt

def usage():
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

    print "Salut Jaypee Le Fèvre !"

if __name__ == "__main__":
    main(sys.argv[1:])
