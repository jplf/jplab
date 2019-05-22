#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Find out how many people like, talk about, follow, etc. a given list
of companies.

Usage: escore.py [-d]

The flag -d sets the debug mode in case of necessity.
Other parameters are defined in the configuration file cac40.conf.
See below its path.

The script updates the cac40 database. Each time it is executed a backup copy
of the database is created in the .old suffixed file. But, nevertheless, be
prudent !

It can be executed by a cron(1) once a day for instance.

Author  : Jean-Paul Le Fèvre <jean-paul.lefevre at cea dot fr>
Date    : 14 February 2012
Licence : http://www.cecill.info/licences/Licence_CeCILL_V2-en.html

"""

__version__ = "$Revision: 770 $"

import os
import sys
import getopt
import shutil
import urllib2
import sqlite3
import ConfigParser
import logging
import re
from datetime import datetime

# Used to log various messages.
logger = None
# The connection to the database
conex  = None
# The cursor used to fetch data from the database.
cursor = None

# The URL of the sites to check.
baseUrl = {'fb' : 'http://www.facebook.com/',
           'tw' : 'https://www.twitter.com/'}

def usage():
    """ Print the accepted options. """
    sys.stdout.write(__doc__)
    sys.exit(1)

def parseCount(string):
    """
    Convert a string into a integer.
    It removes the white space, comma, etc. from the string, checks it
    and converts it.
    Return -1 in case of error.

    """

    if string is None:
        return None

    s = string.strip()
    s = s.translate(None, ' ')
    s = s.translate(None, ',')
    s = s.translate(None, ' ')

    if not s.isdigit():
        logger.error('Error parsing %s', string)
        return -1

    return int(s)

def findFbCounts(page):
    """
    Get the number of people liking that and the number of people talking
    about that mentionned on a given page from Facebook.
    
    Return the couple of numbers or -1s in case of error.
    
    """
    counts = [-1, -1]

    # The count is retrieved using this regular expression.
    m = re.search('>([\d  ]+)(</span> personnes aiment)', page)
    if m is None:
        m = re.search('fcg">([\d  ]+)(personnes aiment)', page)
    if m is None:
        m = re.search('fcg">([\d  ]+)(.*aime )', page)
        

    if m is None:
        logger.error('No fb like string')
        return counts

    like = m.group(1)
    logger.debug('Fb like count %s', m.group(1))

    # Sometimes talk is missing.
    m = re.search('>([\d  ]+)(</span> personnes qui en parlent)', page)
    if m is None:
        m = re.search('a · ([\d  ]+)(personnes en parlent)', page)
    if m is None:
        m = re.search('e · ([\d  ]+)(personnes en parlent)', page)

    if m is None:
       logger.warning('No fb talk string')
       talk = '0'
    else:
       talk = m.group(1)
       logger.debug('Fb talk count %s', m.group(1))

    # Return the couple of numbers.
    return [parseCount(like), parseCount(talk)]

def findTwCounts(page):
    """
    Get the numbers on the Twitter page.

    """
    
    return findTweCounts(page)

def findTwfCounts(page):
    """
    Get the number of people mentionned on the given page in French.
    
    Return the numbers.
    
    """
    logger.debug('Parsing the French version')
    counts = [-1, -1, -1]

    # The count is retrived using a regular expression.
    chunk = '>([\d,]+)(<span class=\'user-stats-stat\'>'

    m = re.search(chunk + 'Tweets</span>)', page)
    if m is None:
        logger.error('Can\'t find the tweets string')
        return counts

    tweets = m.group(1)
    logger.debug('Tweets : %s', m.group(1))
    
    m = re.search(chunk + 'Abonnements</span>)', page)
    if m is None:
        logger.error('Can\'t find the abonnements string')
        return counts

    following = m.group(1)
    logger.debug('Following :%s ', m.group(1))
    
    
    m = re.search(chunk + 'Abonnés</span>)', page)
    if m is None:
        logger.error('Can\'t find the abonnés string')
        return counts

    followers = m.group(1)
    logger.debug('Followers : %s', m.group(1))
        
    return [parseCount(tweets), parseCount(following), parseCount(followers)]

def findTweCounts(page):
    """
    Get the numbers on the given page from Twitter in English.
    
    Return the numbers.
    
    """

    logger.debug('Parsing the English version')
    counts = [-1, -1, -1]

    # The count is retrieved using this regular expression.

    chunk = 'statuses_count&quot;:([\d]+)'
    m = re.search(chunk, page)
    if m is None:
        logger.error('Can\'t find the tweets string')
        return counts

    tweets = m.group(1)
    logger.debug('Tweets : %s', m.group(1))

    chunk = 'friends_count&quot;:([\d]+),'
    m = re.search(chunk, page)
    if m is None:
        logger.error('Can\'t find the following string')
        return counts

    following = m.group(1)
    logger.debug('Following : %s', m.group(1))
    
    chunk = 'followers_count&quot;:([\d]+),'
    m = re.search(chunk, page)
    if m is None:
        logger.error('Can\'t find the followers string')
        return counts

    followers = m.group(1)
    logger.debug('Followers : %s', m.group(1))
        
    return [parseCount(tweets), parseCount(following), parseCount(followers)]

def getUrl(src, path):
    """
    Get the url of a specific page.
    Src can be 'fb' for facebook, 'tw' for twitter.

    Return the url.

    """

    return baseUrl[src] + path

def getPage(url):
    """
    Get the page to examine knowing its URL.
    It is based on the urllib2 python standard module.
    
    Return the content of the page.
    
    """

    try:
        response = urllib2.urlopen(url)
        content  = response.read()

    except urllib2.HTTPError, e:
        logger.error('Can''t read the page %s (code %s)', url, e.code)
        return None

    logger.debug(content)

    return content
    
def main(argv):
    """
    Execute the program.

    It parses the command line.
    Then for each company it retrieves the societal network pages,
    searches the counts in them and stores the results.
    
    """
    appli = 'cac40'
    debug = False

    # The resources for this program are found in the config file.
    rsrc = ConfigParser.SafeConfigParser()
    rsrc.read(os.path.expanduser('~/etc/cac40.conf'))

    # Prepare the log file.
    global logger
    logger = logging.getLogger(appli)
    logger.setLevel(logging.NOTSET)
    level = rsrc.get(appli, 'log_level').lower()

    if level == 'quiet':
        logger.setLevel(logging.NOTSET)
    elif level == 'verbose':
        logger.setLevel(logging.INFO) 
    elif level == 'debug':
        logger.setLevel(logging.DEBUG)
    else:
        logger.setLevel(logging.WARN)

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

    # Force the debug mode if the flag -d is set.
    if debug:
        logger.setLevel(logging.DEBUG)

    # Specify how messages are handled.
    if rsrc.has_option(appli, 'log_file'):
        filename = rsrc.get(appli, 'log_file')
        handler = logging.FileHandler(filename)
    else:
        handler = logging.StreamHandler(sys.stderr)

    if rsrc.has_option(appli, 'log_format'):
        format = rsrc.get(appli, 'log_format', True)
        handler.setFormatter(logging.Formatter(format))

    logger.addHandler(handler)

    try:
        # The path to the database is found in the configuration.
        dbKey = 'cac40_db'
        liteDb = rsrc.get(appli, dbKey)
        if os.path.getsize(liteDb) < 1:
              logger.critical('Invalid empty db : %s', liteDb)
              sys.exit(1)
             
        global conex
        conex  = sqlite3.connect(liteDb)
        # Try to handle correctly different types of string.
        # Non ansi characters may cause problem.
        conex.text_factory = sqlite3.OptimizedUnicode

        logger.info('Connection to %s successful !', liteDb)

        # Create a backup copy of the current version of the database.
        backup = liteDb + '.old'
        shutil.copyfile(liteDb, backup)

    except:
        logger.critical(sys.exc_info()[1])
        sys.exit(1)

    # Fetch the list of pages to survey.
    cursor = conex.cursor()
    cursor.execute('select id, company_id, source, path from company_pages')
    records = cursor.fetchall()

    # Text of the statement that will be executed.
    statement  = 'insert into scores (page_id, count_1, count_2, count_3) '
    statement +=  'values (?, ?, ?, ?)'

    # Keep track of the number of pages succesfully parsed.
    numberOfPage = 0
    
    for r in records:

        # Retrieve the name of the company.
        cursor.execute('select main_name from companies where id=?', (r[1],))
        result      = cursor.fetchone()
        companyName = result[0]
        logger.debug('Processing company %s', companyName)

        pid  = r[0] # The page id
        src  = r[2] # The source either facebook or twitter
        url  = getUrl(src, r[3])
        page = getPage(url)

        if page is None:
            continue

        if src == 'fb':
            c1, c2 = findFbCounts(page)
            c3 = 0

            if c1 < 0:
               logger.warning('No like value found for %s', companyName)
               continue

            if c2 < 0:
               logger.warning('No talk value found for %s', companyName)
               continue

            values = (pid, c1, c2, c3)
           
        elif src == 'tw':
            c1, c2, c3 = findTwCounts(page)

            if c1 < 0:
               logger.warning('No tweets value found for %s', companyName)
               continue

            if c2 < 0:
               logger.warning('No following value found for %s', companyName)
               continue

            if c3 < 0:
               logger.warning('No followers value found for %s', companyName)
               continue

            values = (pid, c1, c2, c3)
           
        else:
            logger.error('Unknown source %s', src)
            continue

        # Insert a new record into the database.
        cursor.execute(statement, values)
        conex.commit()
        numberOfPage += 1

        ascii = companyName.encode('ascii', 'ignore')
        logger.debug('{0}, {1:d}, {2:d}, {3:d}'.format(ascii,c1,c2,c3))

    # At the end see how many rows are now in the score table.
    cursor.execute('select count(*) from scores')
    records = cursor.fetchone()

    logger.info(str(records[0]) + ' scores stored for ' + str(numberOfPage)
                + ' pages examined')
    
if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
