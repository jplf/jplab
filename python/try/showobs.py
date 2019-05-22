#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Display a map centered on a given observatory.

Usage: showobs.py
login/password must be defined in the environment : USER/MY_PW
The script then enters a loop.
Observatory coordinates need to be more precise.page

"""

__version__ = "$Revision: 627 $"

import sys
import os
import getopt
import pprint
import MySQLdb
import jplib

handle = None
user   = None
    
def usage():
    """ Print the accepted options. """
    sys.stderr.write(__doc__)
    sys.exit(1)

def fetch_obs():
    """
    Get the content of the observatories table.
    Return the observatories in a dictionnary.
    
    """

    global user
    user     = os.getenv('USER')
    password = os.getenv('MY_PW')
    
    if user is None or password is None:
        print 'Missing user/pw env variables.'
        usage()
        

    # Get the list of known observatories.
    try:
        my_db = MySQLdb.connect(user=user, passwd=password,
                            unix_socket='/tmp/mysql.sock')
    except:
        print sys.exc_info()[1]
        sys.exit(1)
        
    global handle
    handle = my_db.cursor()
    handle.execute('use ' + config.db)

    handle.execute('select * from observatories')
    observatories = handle.fetchall()
    
    my_obs = dict([(observatories[i][1], observatories[i])
                   for i in range(len(observatories))])

    return my_obs

def read_page():
    """
    Read the template page.
    Return the template as a string.
    
    """

    template = open('template.html', 'r')
    page = template.readlines()
    
    return ''.join(page)
    
def main(argv):
    """ Execute the program. """
    try:                                
        opts, args = getopt.getopt(argv, 'h', ['help'])
        
    except getopt.GetoptError:          
        usage()
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()

    my_obs = fetch_obs()
    page = read_page()
    jplib.warn("Hi " + user)
    
    options = dict(h='help', l='list', o='obs', q='quit')
    
    # Then, start the loop.
    while True:
        
        line = raw_input('-> ').lower()

        if line == 'q':
            break
        
        elif line == 'h':
            print 'Available options :'
            pprint.pprint(options)
            
        elif line == 'l':
            pprint.pprint(my_obs)
            
        elif line == 'o':
            name = raw_input('name = ')
            try:
                # pprint.pprint(my_obs[name])
                pos  = '{0:.3f}, {1:.3f}'.format(my_obs[name][3],
                                                 my_obs[name][4])
                html = page.replace('@lat_lng', pos, 1)
                html = html.replace('@name', name, 1)
                
                temp = open('/tmp/map.html', 'w')
                temp.write(html)
                temp.close()
                
                os.system('firefox file:///tmp/map.html')
               
            except KeyError:
                print 'Unkown name', name, '!'
            
        else:
            print 'Unkown option !'
            pprint.pprint(options)

if __name__ == '__main__':
    main(sys.argv[1:])
