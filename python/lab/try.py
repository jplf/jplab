#!/usr/bin/env python
# -*- coding: utf-8 -*-
#______________________________________________________________________________
"""
Tests the definition of the object Machine.

Usage: try.py

"""

import sys
import getopt
from machines import Machine

def usage():
    """ Tells how to use the program """
    sys.stderr.write(__doc__)
    sys.exit(1)

def main(argv):
    """
    The main program
    """

    try:
        #pylint: disable=unused-variable
        opts, args = getopt.getopt(argv, "h", ["help"])

    except getopt.GetoptError:
        usage()

    #pylint: disable=unused-variable
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()

    print('Testing the machines')
    my_host = Machine()
    my_host.set_name('hurukan')
    my_host.set_ip_nbr('132.166.28.183')
    
    print(my_host.get_id())

if __name__ == "__main__":
    main(sys.argv[1:])
#______________________________________________________________________________
