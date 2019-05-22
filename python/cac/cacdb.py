#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Check the content of a SQLite database.

Usage: cacdb.py [-o operation] [-t table]
Operations : list_tables, dump_table

"""

__version__ = "$Revision: 662 $"

import sys
import os
import getopt
import sqlite3
import ConfigParser

cursor = None
    
def usage():
    """ Print the accepted options. """
    sys.stderr.write(__doc__)
    sys.exit(1)

def listTables():
    """ Print the list of tables in the lite database. """

    cursor.execute('select name from sqlite_master where type = "table"')
    tblList = cursor.fetchall()

    for tbl in tblList:
        print tbl[0]

def dumpTable(table):
    """ Print the content of a table. """
    if table is None:
        raise NameError('No table specified')
    
    cursor.execute('select * from ' + table)
    records = cursor.fetchall()

    for r in records:
        print r
    
def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, "ho:t:",
                        ["help", "operation=", "table="])
        
    except getopt.GetoptError:          
        usage()

    # The resources for this program are found in the config file.
    rsrc = ConfigParser.SafeConfigParser()
    rsrc.read(os.path.expanduser('cac40.conf'))

    op    = 'list_tables'
    tbl   = None
    dbKey = 'cac40_db'

    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-o', '--operation'):
            op = val
        elif opt in ('-t', '--table'):
            tbl = val

    try:
        # The path to database is found in the configuration.
        liteDb = rsrc.get('cac40', dbKey)
        conex  = sqlite3.connect(liteDb)
        conex.text_factory = sqlite3.OptimizedUnicode
        print 'Connection to', liteDb, 'successful !'

    except:
        print sys.exc_info()[1]
        sys.exit(1)
        
    global cursor
    cursor = conex.cursor()
    
    if op == 'list_tables':
        listTables()
        
    elif op == 'dump_table':
        dumpTable(tbl)
        
    else:
        usage()

if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
