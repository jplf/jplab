#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Read, parse and store in the database the content of a csv file.

Usage: reader.py [-hd] file.csv

It can be used to populate the database in order to test the application.
The database must have been created before, e.g. with create_cac.sql
The content of the db may be checked with the script cacdb.py or simply
with the program sqlite3.

"""

__version__ = "$Revision: 664 $"

import sys
import os
import getopt
import sqlite3
import ConfigParser
import csv
import re

debug  = False
conex  = None
cursor = None

def usage():
    """ Print the accepted options """
    sys.stdout.write(__doc__)
    sys.exit(1)

def quote(string):
    """ Put a string into quotes """
    return '\"' + string + '\"'

def insertCompany(data):
    """
    Add a new company in the main table.
    Data is a row read from the input file.
    Return the id of the inserted row.

    Nom;URL;URL court;Groupe;Entité;Secteur;Indice;Nationalité;Type
     0   1     2        3      4      5       6       7         8

    """

    # Do not use "Entité".
    if len(data[3]) < 1:
        group = 'null'
    else:
        group = quote(data[3])

    if len(data[5]) < 1:
        sector = 'null'
    else:
        sector = quote(data[5].lower())
    
    if len(data[6]) < 1:
        index = 'null'
    else:
        index = quote(data[6].lower())
    
    if len(data[7]) < 1:
        country = 'null'
    else:
        country = quote(data[7].lower())

    mainName = quote(data[0])
    
    cmd ='insert into companies '
    cmd += '(main_name, group_name, sector, stock_index, country_code) '
    cmd += 'values(' + mainName + ', ' + group + ', ' + sector
    cmd +=  ', ' + index + ', ' + country + ')'

    if debug:
        print "insert :", cmd

    cursor.execute(cmd)
    conex.commit()
    
    cursor.execute('select id from companies where main_name=?', (data[0],))
    result = cursor.fetchone()
    if debug:
        lastrowid = cursor.lastrowid
        print mainName, result[0], lastrowid

    if result is None:
        return None

    return str(result[0])


def updateDb(data):
    """
    Insert or update data into the database.
    Data is a row read from the input file.

    Nom;URL;URL court;Groupe;Entité;Secteur;Indice;Nationalité;Type
     0   1     2        3      4      5       6       7         8

    """

    # Check the number of fields in the input row.
    if len(data) < 1:
        return
    elif len(data) != 9:
        print >> stderr, 'bad number of items in', data
        return

    # Check if the company is already stored in db or not.
    cursor.execute('select id from companies where main_name=?', (data[0],))
    result = cursor.fetchone()

    if result is not None:
        companyId = str(result[0])
        if debug:
            print >>sys.stderr, data[0], 'is already stored', '(', result[0], ') !'
    else :
        # It is a new company, store it.
        companyId = insertCompany(data)

    if companyId is None:
        print >>sys.stderr, 'Can\'t insert', data,  '!'
        return

    # Either a facebook page or a twitter one.
    # For twitter a possible substring must be discarded
    fbPattern = 'facebook.com/(.+)'
    twPattern = 'twitter.com/(#!/)?(.+)'

    if len(data[1]) < 1:
        return
    else:
        url = data[1]

    # If the url matches the regular expression the kind of page
    # is guessed and the path is what follows the domain name

    # First try facebook
    m = re.search(fbPattern, url)
    if m is not None:
        source = 'fb'
        path   = m.group(1)

    # Then try twitter
    m = re.search(twPattern, url)
    if m is not None:
        source = 'tw'
        path   = m.group(m.lastindex)


    # And finally the type of site.
    if len(data[8]) < 1:
        kind = 'null'
    else:
        kind = quote(data[8].lower())
        
    # Check if the page is already stored in db or not.
    statement  = 'select id from company_pages where '
    statement += 'company_id=? and source=? and path=?'

    cursor.execute(statement, (companyId, source, path))
    result = cursor.fetchone()

    if result is not None:
        if debug:
            print >>sys.stderr, data[0], 'has already page', result[0], ' !'
        return
    
    # Then update the company page table.
    statement = 'insert into company_pages(company_id, source, path, type)'

    values = ' values(' + companyId + ', ' + quote(source)
    values += ', ' + quote(path) + ', ' + kind + ')'

    cmd = statement + values
    if debug:
        print cmd

    cursor.execute(cmd)
    conex.commit()

def main(argv):

    try:                                
        opts, args = getopt.getopt(argv, 'hd', ['help', 'debug'])

    except getopt.GetoptError:          
          usage()                         

    global debug

    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-d', '--debug'):
            debug = True

    if len(args) > 0:
        filename = args[0]
    else:
        usage()

    # Process each line read in the input file.
    handle = open(filename, 'r')
    batch  = csv.reader(handle, dialect='excel', delimiter=';')

    # The resources for this program are found in the config file.
    rsrc = ConfigParser.SafeConfigParser()
    rsrc.read(os.path.expanduser('cac40.conf'))
    dbKey = 'cac40_db' # Corresponding value is the path to the db file.

    try:
        # The path to database is found in the configuration.
        liteDb = rsrc.get('cac40', dbKey)

        global conex
        conex  = sqlite3.connect(liteDb)
        # Try to handle correctly different types of string.
        conex.text_factory = sqlite3.OptimizedUnicode

        if debug:
            print 'Connection to', liteDb, 'successful !'

    except:
        print sys.exc_info()[1]
        sys.exit(1)

    global cursor
    cursor = conex.cursor()
     
    for i, row in enumerate(batch):
        try:
            updateDb(row)

        except:
            print >> sys.stderr, 'Error line', i+1
            print >> sys.stderr,  row
            print >> sys.stderr,  sys.exc_info()[1]
            sys.exit(1)
           
          
    conex.close()
        
if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
