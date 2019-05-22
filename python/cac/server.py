#!/usr/bin/python
# -*- coding: utf-8 -*-
#_______________________________________________________________________________

"""
The http server used to display results of the cac40 survey.

Usage: server [-dh]

It is based on the CherryPy (http://cherrypy.org/) toolkit.
HTML templates are processed by Mako (http://www.makotemplates.org/).

By default result is accessible from http://localhost:8080/ but this
URL can be redefined in the configuration file cac40.conf.

"""

import sys
import os
import os.path
import logging
import getopt
import ConfigParser
import cherrypy
import sqlite3

from mako.template import Template
from mako.lookup   import TemplateLookup

lookup = TemplateLookup(directories=['html'], output_encoding='utf-8')
logger = None
rsrc   = None

def usage():
    """ Print the accepted options. """
    sys.stdout.write(__doc__)
    sys.exit(1)

class Root:
    """
    The main page proposed by the server.
    It displays the list of companies which are surveyed by the application.

    """
    def index(self):

        tmpl  = lookup.get_template("index.html")

        companies = self.getCompanies()
            
        return tmpl.render(companyList=companies)

    index.exposed = True

    def getCompanies(self):
        """
        Return the list of companies managed by the application.

        """

        names = []

        try:
            # The path to database is found in the configuration.
            dbKey  = 'cac40_db'
            liteDb = rsrc.get('cac40', dbKey)
            # The connection is made each time for sake of simplicity.
            conex  = sqlite3.connect(liteDb)
            conex.text_factory = sqlite3.OptimizedUnicode
            logger.info('Connection to', liteDb, 'successful !')

        except:
            logger.error(sys.exc_info()[1])
            return ['not found']

        cursor = conex.cursor()
        cursor.execute('select main_name from companies order by main_name')
        records = cursor.fetchall()

        for r in records:
            names.append(r[0])

        return names

class Display:

    """
    The page displaying data about a given company.

    """
    def index(self, company='company'):

        dbKey  = 'cac40_db'
        liteDb = rsrc.get('cac40', dbKey)
        # Open the connection each time.
        conex  = sqlite3.connect(liteDb)

        cursor = conex.cursor()
        cursor.execute('select * from companies where main_name=?', (company,))
        rows = cursor.fetchall()

        if rows is None or len(rows) < 1:
            logger.info('Page for ' + company + ' not found')
            return
        
        data = rows[0]
        print data
        company_id = int(data[0])
        
        record = {
            'id'           : data[0],
            'main_name'    : data[1],
            'entity'       : data[2],
            'web_site'     : data[3],
            'stock_index'  : data[4],
            'sector'       : data[5],
            'country_code' : data[6]
            }

        # Retrieve the path to pages fb or tw
        stmt = 'select path from company_pages where company_id=? and source=?'

        cursor.execute(stmt, (company_id, 'fb'))
        data = cursor.fetchone()
        if data is not None:
            record['fb_path'] = data[0]
        else:
            record['fb_path'] = ''

        cursor.execute(stmt, (company_id, 'tw'))
        data = cursor.fetchone()
        if data is not None:
            record['tw_path'] = data[0]
        else:
            record['tw_path'] = ''
        
        tmpl = lookup.get_template("company.html")
        
        return tmpl.render(companyRecord=record)

    index.exposed = True

class Select:

    """
    Fetch selected data from the database.
    For a given company and source either facebook or twitter.
    
    """
    def index(self, **kwargs):

        company=kwargs['company']
        src=kwargs['src']

        logger.debug('Scoring for ' + company + '/' + src)

        try:
            # The path to database is found in the configuration.
            dbKey  = 'cac40_db'
            liteDb = rsrc.get('cac40', dbKey)
            # The connection is made each time for sake of simplicity.
            conex  = sqlite3.connect(liteDb)
            conex.text_factory = sqlite3.OptimizedUnicode

        except:
            logger.error(sys.exc_info()[1])
            return None

        cursor = conex.cursor()
        
        stmt  = 'select * from scores where page_id in '
        stmt += '(select t1.id from company_pages as t1,' 
        stmt += 'companies as t2 where t1.company_id = t2.id ' 
        stmt += 'and t2.main_name=:name and t1.source=:src)'
        
        cursor.execute(stmt, {'name' : company, 'src' : src})

        records = cursor.fetchall()
        logger.info('Number of scores found for ' + company + '/' + src + ' '
                    + str(len(records)))

        response = ''
        for r in records:
            for s in r[:-1]:
                response += str(s) + ', '
            response += r[-1] + '\n'

        return response

    index.exposed = True

class DebugSelection:

    """
    Fetch selected data from a local constant array.
    This routine is used only to debug the program in case of trouble
    otherwise it is not called.

    """
    def index(self, company='company'):

        array = [
            ['20',  '22', '6500', '406', '0', '2012-02-14 15:15:00'],
            ['43',  '22', '6500', '406', '0', '2012-02-14 15:20:29'],
            ['66',  '22', '6502', '406', '0', '2012-02-14 16:20:35'],
            ['89',  '22', '6505', '393', '0', '2012-02-14 19:30:31'],
            ['112', '22', '6506', '393', '0', '2012-02-14 23:30:44'],
            ['135', '22', '6508', '393', '0', '2012-02-15 03:30:32'],
            ['158', '22', '6509', '393', '0', '2012-02-15 07:30:39'],
            ['181', '22', '6514', '393', '0', '2012-02-15 11:30:30'],
            ['206', '22', '6520', '393', '0', '2012-02-15 15:30:36'],
            ['250', '22', '6535', '393', '0', '2012-02-15 23:30:32'],
            ['295', '22', '6537', '393', '0', '2012-02-16 07:30:28'],
            ['340', '22', '6548', '405', '0', '2012-02-16 15:30:31'],
            ['385', '22', '6561', '405', '0', '2012-02-16 23:30:37'],
            ['430', '22', '6566', '405', '0', '2012-02-17 07:30:27'],
            ['475', '22', '6577', '405', '0', '2012-02-17 15:30:32'],
            ['520', '22', '6585', '405', '0', '2012-02-17 23:30:29'],
            ['565', '22', '6589', '405', '0', '2012-02-18 07:30:32'],
            ['610', '22', '6598', '405', '0', '2012-02-18 15:30:32'],
            ['655', '22', '6608', '405', '0', '2012-02-18 23:30:29'],
            ['700', '22', '6611', '405', '0', '2012-02-19 07:30:26'],
            ['745', '22', '6614', '405', '0', '2012-02-19 15:30:31'],
            ['790', '22', '6626', '405', '0', '2012-02-19 23:30:26'],
            ['835', '22', '6629', '405', '0', '2012-02-20 07:30:30']
            ]

        response = ''
        for row in array:
            s =  ', '.join(row)
            response += s + '\n'

        return response

    index.exposed = True
    
def main(argv):
    """
    Execute the program.

    It parses the command line, reads the configuration.
    It prepares the logger.

    """

    appli = 'cac40'
    debug = False

    # The resources for this program are found in the config file.
    global rsrc
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
    onScreen = False
    if rsrc.has_option(appli, 'log_file'):
        filename = rsrc.get(appli, 'log_file')
        handler = logging.FileHandler(filename)
    else:
        handler  = logging.StreamHandler(sys.stderr)
        onScreen = True

    if rsrc.has_option(appli, 'log_format'):
        format = rsrc.get(appli, 'log_format', True)
        handler.setFormatter(logging.Formatter(format))

    # Initialize the logger.
    logger.addHandler(handler)
    cherrypy.log.access_log.addHandler(handler)
    cherrypy.log.error_log.addHandler(handler)

    currentDir = os.path.dirname(os.path.abspath(__file__))
    
    # Update the Cherrypy config and avoid messing up with different approach.
    cherrypy.config.update(
        {'server.socket_port': rsrc.getint(appli, 'server.port'),
         'server.socket_host': rsrc.get(appli, 'server.host'),

         'tools.encode.on'       : True,
         'tools.encode.encoding' : 'utf-8',
         
         'log.screen' : onScreen})

    # Configuration needed to serve static files.
    cssConf = {
        '/' : {
            'tools.staticdir.root' : currentDir
            },
        '/css' : {
            'tools.staticdir.on' : True,
            'tools.staticdir.dir': 'css'
            },
        '/js' : {
            'tools.staticdir.on' : True,
            'tools.staticdir.dir': 'js'
            },
        '/data' : {
            'tools.staticdir.on' : True,
            'tools.staticdir.dir': 'data'
            }
        }

    root = Root()
    root.display = Display()
    root.select  = Select()

    logger.info('Cherry is ready to start !')
    cherrypy.quickstart(root, config=cssConf)
    
if __name__ == '__main__':
    main(sys.argv[1:])

#_______________________________________________________________________________

