#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Manage my mp3 collection.

Usage: mymp3.py [-h][-d dir] [file]

"""

__version__ = "$Revision: 496 $"

import sys
import os
import getopt

def usage():
    sys.stderr.write(__doc__)
    sys.exit(1)

def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, "hvqd:",
                                   ["help", "verbose", "quiet", "dir="])
        
    except getopt.GetoptError:          
        usage()
        
    mp3dir   = None
    filename = None
    verbose  = False
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-v', '--verbose'):
            verbose = True
        elif opt in ('-q', '--quiet'):
            verbose = False
        elif opt in ('-d', '--dir'):
            mp3dir = val
            
    if len(args) > 0:
        filename = args[0]
        
    print "Directory : ", mp3dir,  " file : ", filename

    try:
        collection = os.listdir(mp3dir)
    except:
        print "Can't list directory : ", mp3dir
        sys.exit(1)

    if verbose:
        print collection
        
    print "\nNumber of disc found is {0}".format(len(collection))
    
    item_number = 0
        
    for disc in collection:
        try:
            files = os.listdir(mp3dir + "/" + disc)
        except:
            print "Can't list directory : ", disc
            sys.exit(1)
            
        nbr = len(files)
        item_number += nbr
        
        if verbose:
            print "{0:3d} files found in disc {1}".format(nbr, disc)
           
    print "\nTotal number of files is {0}".format(item_number)
    
if __name__ == "__main__":
    import sys
    main(sys.argv[1:])
