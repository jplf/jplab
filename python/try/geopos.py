#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Convert  geographic coordinates to decimal degrees.

Usage: geopos latitude longitude
Example: geopos 44N38,5 7W29,7

"""

__version__ = "$Revision: 495 $"

import getopt
import re

def usage():
    sys.stderr.write(__doc__)
    sys.exit(1)

def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, "hv", ["help", "verbose"])
        
    except getopt.GetoptError:          
        usage()
        
    verbose  = False
    
    for opt, val in opts:
        if opt in ('-v', '--verbose'):
            verbose = True
        elif opt in ('-h', '--help'):
            usage()

    if len(args) < 2:
        usage()

    if verbose:
        print "Position ", args[0], " ", args[1]

    # Replace commas by dot
    lat = args[0].replace(',', '.')
    lng = args[1].replace(',', '.')

    #  Latitude 44N38,5 7W29,7
    info = re.match("(\d+)N(\d*\.\d*)?", lat)
    if verbose:
        print info.group(1), " deg north ", info.group(2)
        
    lat = float(info.group(1))
    if info.group(2) is not None:
        lat += float(info.group(2)) / 60.
    
    #  Longitude 7W29,7
    info = re.match("(\d+)W(\d*\.\d*)?", lng)
    if verbose:
        print info.group(1), " deg west ", info.group(2)
        
    lng = float(info.group(1))
    if info.group(2) is not None:
        lng += float(info.group(2)) / 60.

    # Print position in decimal degrees 
    print "{0:.4f}, {1:.4f}".format(lat, -lng)
   

if __name__ == "__main__":
    import sys
    main(sys.argv[1:])
