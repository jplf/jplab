#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Play with a MySQL database.

Usage: mydb.py [-u user] -p password [-d db] [-t table] [-o operation]
Operations : list_db, list_tables, dump_table

"""

__version__ = "$Revision: 498 $"

import sys
import getopt
import MySQLdb
import config

handle = None
    
def usage():
    """ Print the accepted options. """
    sys.stderr.write(__doc__)
    sys.exit(1)

def list_db():
    """ Print the list of databases. """
    handle.execute('show databases')
    db_list = handle.fetchall()

    for db in db_list:
        print db[0]

def list_tables():
    """ Print the list of tables. """
    handle.execute('show tables')
    tbl_list = handle.fetchall()

    for tbl in tbl_list:
        print tbl[0]

def dump_table(table):
    """ Print the content of a table. """
    if table is None:
        raise NameError('No table specified')
    
    handle.execute('select * from ' + table)
    records = handle.fetchall()

    for r in records:
        print r
    
def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, "hu:p:d:o:t:",
                        ["help", "user=", "password=",
                         "db=", "operation=", "table="])
        
    except getopt.GetoptError:          
        usage()

    user     = config.user
    password = None
    op  = 'list_db'
    db  = config.db
    tbl = None
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-u', '--user'):
            user = val
        elif opt in ('-p', '--password'):
            password = val
        elif opt in ('-d', '--db'):
            db = val
        elif opt in ('-o', '--operation'):
            op = val
        elif opt in ('-t', '--table'):
            tbl = val

    if user is None or password is None:
        usage()
        
    config.warn("Hi " + user)

    try:
        my_db = MySQLdb.connect(user=user, passwd=password,
                            unix_socket='/tmp/mysql.sock')

    except:
        print sys.exc_info()[1]
        sys.exit(1)
        
    global handle
    handle = my_db.cursor()
    
    if op == 'list_db':
        list_db()
        sys.exit(0)
        
    handle.execute('use ' + db)
    
    if op == 'list_tables':
        list_tables()
        
    elif op == 'dump_table':
        dump_table(tbl)
        
    else:
        usage()

if __name__ == "__main__":
    import sys
    main(sys.argv[1:])
