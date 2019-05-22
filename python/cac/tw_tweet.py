#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Find out how many tweets and follows are displayed on a page.

Usage: tw_tweet.py [-d]
The flag -d sets the debug mode in case of necessity.

The script prints : date, company id, tweets, following, followers.
These csv values can be stored in a file for later processing.
It is very similar to what has been done for Facebook.

Author  : Jean-Paul Le Fèvre <jean-paul.lefevre at cea dot fr>
Date    : 15 February 2012
Licence : http://www.cecill.info/licences/Licence_CeCILL_V2-en.html

"""

__version__ = "$Revision: 770 $"

import sys
import getopt
import urllib2
import re
from datetime import datetime

# The Tweeter web site
tw_url = 'https://www.twitter.com/'
debug  = False

# The list of companies to survey. It is a dictionnary of
# couples (company id, tw page)
#groups    = {'Credit Agricole' : '1CreditAgricole'}
groups    = {'Legrand' : 'Legrand_news'}

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
    s = s.translate(None, ',')
    if not s.isdigit():
        print 'Error parsing ', string
        return -1

    return int(s)

def findCounts(page):

    m = re.search('following_count', page)

    return findCountsE(page)

def findCountsE(page):
    """
    Get the number of people mentionned on the given page.
    
    Return the numbers.
    
    """

    print 'Parse the English version'
    counts = [-1, -1, -1]

    # The count is retrieved using a regular expression.

    chunk = 'statuses_count&quot;:([\d]+)'
    m = re.search(chunk, page)
    if m is None:
        if debug:
            print 'Can''t find the tweets string'
        return counts

    tweets = m.group(1)
    if debug:
        print 'Tweets : ', m.group(1)
    
    chunk = 'friends_count&quot;:([\d]+),'
    m = re.search(chunk, page)
    if m is None:
        if debug:
            print 'Can''t find the following string'
        return counts

    following = m.group(1)
    if debug:
        print 'Following : ', m.group(1)
    
    
    chunk = 'followers_count&quot;:([\d]+),'
    m = re.search(chunk, page)
    if m is None:
        if debug:
            print 'Can''t find the followers string'
        return counts

    followers = m.group(1)
    if debug:
        print 'Followers : ', m.group(1)
        
    return [parseCount(tweets), parseCount(following), parseCount(followers)]


def findCountsF(page):
    """
    Get the number of people mentionned on the given page.
    
    Return the numbers.
    
    """

    print 'Parse the French version'
    counts = [-1, -1, -1]

    # The count is retrived using a regular expression.
    chunk = '>([\d,]+)(<span class=\'user-stats-stat\'>'

    m = re.search(chunk + 'Tweets</span>)', page)
    if m is None:
        if debug:
            print 'Can''t find the tweets string'
        return counts

    tweets = m.group(1)
    if debug:
        print 'Tweets : ', m.group(1)
    
    m = re.search(chunk + 'Abonnements</span>)', page)
    if m is None:
        if debug:
            print 'Can''t find the following string'
        return counts

    following = m.group(1)
    if debug:
        print 'Following : ', m.group(1)
    
    
    m = re.search(chunk + 'Abonnés</span>)', page)
    if m is None:
        if debug:
            print 'Can''t find the followers string'
        return counts

    followers = m.group(1)
    if debug:
        print 'Followers : ', m.group(1)
        
    return [parseCount(tweets), parseCount(following), parseCount(followers)]

def getPage(link):
    """
    Get the twitter page.
    The link is the name of the twitter page.
    
    Return the content of the page.
    
    """

    try:
        response = urllib2.urlopen(tw_url + link)
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
    Then for each company it retrieves the twitter page, searches the counts
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
        
        c1, c2, c3 = findCounts(page)

        when = datetime.now().strftime('%d/%m/%Y %H:%M')

        print '{0}, {1}, {2:d}, {3:d}, {4:d}'.format(when, company, c1,c2,c3)

if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
