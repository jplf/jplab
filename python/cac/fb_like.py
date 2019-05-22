#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Find out how many people like and talk about a list of companies on facebook.

Usage: fb_like.py [-d]
The flag -d sets the debug mode in case of necessity.

The script prints : date, company id, like count, talk count.
It can be executed by a cron(1).
These csv values can be stored in a file for later processing.
The database is not used.

Author  : Jean-Paul Le Fèvre <jean-paul.lefevre at cea dot fr>
Date    : 26 January 2012
Licence : http://www.cecill.info/licences/Licence_CeCILL_V2-en.html

"""

__version__ = "$Revision: 748 $"

import sys
import getopt
import urllib2
import re
from datetime import datetime

# The Facebook web site
fb_url = 'http://www.facebook.com/'
debug  = False

# The list of companies to survey. It is a dictionnary of
# couples (company id, fb page)

groups    = {'Accor'                 : 'Accor',
             'Air liquide'           : 'AirLiquide',
             'Alcatel-Lucent'        : 'ALUEnterprise',
             'Alstom'                : 'ALSTOM',
             'Arcelor-Mittal'        : 'arcelornmittal',
             'Axa'                   : 'AXAEquitable',
             'BNP Paribas'           : 'bnpparibas.net',
             'Bouygues Telecom'      : 'bouyguestelecom',
             'Bouygues Construction' : 'Bouygues.Construction',
             'BP'                    : 'BPAmerica',
             'Cap Gemini Consulting' : 'capgemini.consulting',
             'Cap Gemini France'     : 'capgeminifrance',
             'Chevron'               : 'Chevron',
             'Credit Agricole'       : 'CreditAgricole',
             'Danone'                : 'danoneetvous',
             'EADS'                  : 'EADSfan',
             'EDF'                   : 'edf',
             'GDF Suez'              : 'gdfsuez',
             'Lafarge'               : 'Lafarge',
             'Legrand'               : 'Legrand',
             'L\'Oréal'              : 'lorealparis',
             'LVMH'                  : 'lvmh',
             'Orange'                : 'orange',
             'Petrobras'             : 'fanpagepetrobras',
             'Peugeot'               : 'Peugeot',
             'Publicis'              : 'publicisgroupe',
             'Renault'               : 'renault',
             'Saint-Gobain'          : 'saintgobaingroup',
             'Schneider Electric'    : 'SchneiderElectricFrance',
             'Société générale'      : 'societegeneralebank',
             'Total'                 : 'Total',
             'Vinci'                 : 'Vinci.Group'
          }

groups    = {'Accor'               : 'Accor'}

def usage():
    """ Print the accepted options. """
    sys.stderr.write(__doc__)
    sys.exit(1)

def parseCount(string):
    """
    Convert a string into a integer.
    It removes the white space from the string, check it and convert it.
    """

    if string is None:
        return None

    s = string.strip()
    s = s.translate(None, ' ')
    s = s.translate(None, ' ')
    if not s.isdigit():
        print 'Error parsing ', string
        return -1

    return int(s)

def findCounts(page):
    """
    Get the number of people liking that and the number of people talking
    about that mentionned on the given page.
    
    Return the numbers.
    
    """
    counts = [0, 0]

    # The count is retrived using a regular expression.
    m = re.search('>([\d  ]+)(</span> personnes aiment)', page)
    if m is None:
        m = re.search('fcg">([\d  ]+)(personnes aiment)', page)
    if m is None:
        m = re.search('fcg">([\d  ]+)(.*aime )', page)
        
    if m is None:
        if debug:
            print 'Can''t find the like string'
        return [-1, -1]

    like = m.group(1)
    if debug:
        print 'Like : ', m.group(1)

    m = re.search('>([\d  ]+)(</span> personnes qui en parlent)', page)
    if m is None:
        m = re.search('a · ([\d  ]+)(personnes en parlent)', page)
    if m is None:
        m = re.search('e · ([\d  ]+)(personnes en parlent)', page)

    if m is None:
        if debug:
            print 'Can''t find the talk string'
        return counts

    talk = m.group(1)
    if debug:
        print 'Talk : ', m.group(1)
    
        
    return [parseCount(like), parseCount(talk)]

def getPage(link):
    """
    Get the facebook page.
    The link is the name of the facebook page.
    
    Return the content of the page.
    
    """
    global debug
    global fb_url

    try:
        response = urllib2.urlopen(fb_url + link)
        content  = response.read()

    except HTTPError, e:
        print 'Can''t read the page', link
        print e.code, e.reason
        return None
        
    if debug:
        print content

    return content
    
def main(argv):
    """
    Execute the program.

    It parses the command line.
    Then for each company it retrieves the facebook page, searches the counts
    in the page and prints the result.
    
    """

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

    global groups


    for company in sorted(groups.keys()):
        
        page = getPage(groups[company])
        
        c1, c2 = findCounts(page)

        when = datetime.now().strftime('%d/%m/%Y %H:%M')

        print '{0}, {1}, {2:d}, {3:d}'.format(when, company, c1, c2)

if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
