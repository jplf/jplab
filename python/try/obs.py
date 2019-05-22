#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Show how to use classes and json.

Usage: obs.py [-r|-w|-q] [specification]

Option -q (query) is based on the Google Maps API and can be used to retrieve
information about a given site.

"""

__version__ = "$Revision: 499 $"

import sys
import getopt
import json
import pprint
from googlemaps import GoogleMaps

class ObsEncoder(json.JSONEncoder):
     """ A custom Json encoder for ObsRecord objects. """

     def default(self, obj):
         if isinstance (obj, ObsRecord):
             return obj.__dict__
         return json.JSONEncoder.default(self, obj)

class ObsDecoder(json.JSONDecoder):
     """
     A custom Json decoder for ObsRecord objects.
     I've been unable to use it :(
     """

     def __init__(self):
         json.JSONDecoder.__init__(self, object_hook=self.dict_to_object)

     def dict_to_object(self, dict):
         print dict
         return ObsRecord(dict['ident'], dict['website'])
     
class ObsRecord:
    """ An observatory record. """
    
    ident   = None
    website = None
    
    def __init__(self, ident, website):
        """ Unique constructor. """
        self.ident   = ident
        self.website = website
        
    def write(self):
        print self.__dict__

def query_obs(location):
    """
    Query information about a given location.
    
    'CFHT Hawaii'
    'Roque de los muchachos, Canary Islands'
    'Siding Spring, Australia'
    
    """
    
    print "Where the fuck is", location
    gmaps  = GoogleMaps()
    result = gmaps.geocode(location)
    
    pprint.pprint(result['Placemark'][0])

def record_decoder(dict):
    """ Create a record from a dictionnary. """
    return ObsRecord(dict['ident'], dict['website'])
        
def usage():
    """ Print the accepted options """
    sys.stderr.write(__doc__)
    sys.exit(1)

def main(argv):
    
    try:                                
        opts, args = getopt.getopt(argv, 'hrwq',
                                   ['help', 'read', 'write', 'query'])
        
    except getopt.GetoptError:          
        usage()                         

    do_write = False
    do_read  = True
    do_query = False
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
            
        elif opt in ('-r', '--read'):
            do_write = False
            do_read  = True
            
        elif opt in ('-w', '--write'):
            do_write = True
            do_read  = False
            
        elif opt in ('-q', '--query'):
            do_query = True
            do_read  = False
           
    if len(args) > 0:
        spec = args[0]
    else:
        spec = None

    if do_write:
        if spec is None:
            out = sys.stdout
        else:
            out = open(spec, 'w')

        obs = ObsRecord('first', 'http://www.first.org')
        json.dump(obs, out, cls=ObsEncoder)
        
    elif do_read:
        if spec is None:
            usage()
        fin = open(spec, 'r')
        rec = json.load(fin, object_hook=record_decoder)
        rec.write()
        
    elif do_query:
        if spec is None:
            usage()
        query_obs(spec)
        
    else:
        usage()
            
            
if __name__ == "__main__":
    main(sys.argv[1:])
