#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Reload exiting statistics obtained with a previous version of the application.

Usage: reload.py [-d] filename

The flag -d sets the debug mode in case of necessity.

Author  : Jean-Paul Le Fèvre <jean-paul.lefevre at cea dot fr>
Date    : 14 February 2012
Licence : http://www.cecill.info/licences/Licence_CeCILL_V2-en.html

"""

__version__ = "$Revision: 687 $"

import os
import sys
import getopt
import shutil
import sqlite3
import ConfigParser
import re
import csv

conex  = None
cursor = None

def usage():
    """ Print the accepted options. """
    sys.stdout.write(__doc__)
    sys.exit(1)

   
def main(argv):
    """
    Execute the program.
    It parses the command line.
    
    """
    appli = 'cac40'
    debug = False

    # The resources for this program are found in the config file.
    rsrc = ConfigParser.SafeConfigParser()
    rsrc.read(os.path.expanduser('cac40.conf'))

    # Parse the command line.
    try:                                
        opts, args = getopt.getopt(argv, 'hd', ['help', 'debug'])
        
    except getopt.GetoptError:          
        usage()
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-d', '--debug'):
            debug = True

    if len(args) > 0:
        filename = args[0]
    else:
        usage()


    try:
        # The path to the database is found in the configuration.
        dbKey = 'cac40_db'
        liteDb = rsrc.get(appli, dbKey)
             
        global conex
        conex  = sqlite3.connect(liteDb)
        # Try to handle correctly different types of string.
        # Non ansi characters may cause problem.
        conex.text_factory = sqlite3.OptimizedUnicode

    except:
        print sys.exc_info()[1]
        sys.exit(1)

    # Fetch the list of pages to survey
    cursor = conex.cursor()

    handle = open(filename, 'r')
    batch  = csv.reader(handle, delimiter='|')

    statement  = 'insert into scores (page_id, count_1, count_2, count_3, date) '
    statement +=  'values (?, ?, ?, ?, ?)'

    # Keep track of the number of pages succesfully parsed
    numberOfPage = 0

    selection  = 'select t1.id from company_pages as t1, companies as t2 '
    selection += 'where t1.company_id=t2.id and t2.main_name=? and t1.source=?'
    
    for row in batch:

        companyId   = row[0]
        companyName = row[1]
        source      = row[3] # The source either facebook or twitter

        if source == 'fb':
            cursor.execute(selection, (companyName, source))
            records = cursor.fetchone()

            if records is None:
                print 'Not found :', companyName
                continue
            pid = records[0]
            
            values = (pid, row[4], row[5], 0, row[7])
           
        elif source == 'tw':
            cursor.execute(selection, (companyName, source))
            records = cursor.fetchone()

            if records is None:
                print 'Not found :', companyName
                continue
            pid = records[0]
            
            values = (pid, row[4], row[5], row[6], row[7])

        else:
            print 'Unknown source', source, '.'

        # Insert a new record into the database.
        cursor.execute(statement, values)
        conex.commit()
        numberOfPage += 1

    print str(numberOfPage), 'pages examined'
    
if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
