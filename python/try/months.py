#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Compute the number of months of 30 days since a certain date.

Usage: months.py date1 date2 date3 ... 

"""

__version__ = "$Revision: 650 $"

import sys
import getopt
from datetime import datetime, timedelta

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

    fmt = '%d/%m/%y'
    d0 = datetime.strptime('01/08/06', fmt)
    print d0.strftime('%d %B %Y')

    # A unit a 30 days.
    unit = 30.0

    for s in args:
        d1 = datetime.strptime(s, fmt)
        dt = d1 - d0
        dd = float(dt.days)
        
        print '{0:.1f}'.format(dd / unit)

if __name__ == "__main__":
    main(sys.argv[1:])
