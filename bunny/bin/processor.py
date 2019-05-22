#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Simulates a processing pipeline.

Usage: processor.py [-hd] -n name 

The parameters needed by this program may be found in a ressource file.
This file must be json formatted. It is named '.processor.json' and should
be in the home directory.

This program does not do anything useful : it runs for a certain amount of time,
iterating over a couple of statements, sleeping a while between each step.

It expects an input file and produces itself an output file.


"""

import sys
import getopt
import json
import time
import hashlib
from datetime import date
from os.path import expanduser

debug = False

def usage():
    """
    Prints the accepted options
    """
    sys.stdout.write(__doc__)
    sys.exit(1)

def md5sum(inp):
    """
    Compute the md5 sum of the content of the input file
    """
    
    data = inp.read();
    md5 = hashlib.md5()
    md5.update(data);
    digest = md5.hexdigest()

    return digest


def process(rsrc, processor_name):
    """
    Crunches numbers for a certain time.
    
    The dictionnary rsrc provides most of the parameters.
    The specific data for this processor are also in the ressources.

    """

    # The common parameters used by every processors
    dt = rsrc.get('processing').get('time_unit', 5)
    data_dir = rsrc.get('processing').get('data_dir', '/tmp') + '/'

    # The specific parameters for this instance of processor.
    proc_param = rsrc.get('processors').get(processor_name, None)

    if proc_param is None:
        print "Can't find ressources for : ", processor_name
        sys.exit(1)

    count = proc_param.get('count', 0);
    fullname = proc_param.get('fullname', 'unknown');
    
    today = date.today()
    print 'On', today, 'Processor', fullname, 'running', count * dt, 's'

    # Start a mock processing
    # Check whether the input file is present
    # and compute a md5 sum.
    try:
        input = data_dir + proc_param.get('input')
        inp = open(input, 'r')
    except:
        print "Can't open input file : ", input
        sys.exit(1)

    digest = md5sum(inp)
    
    inp.close()
    
    try:
        output = data_dir + proc_param.get('output')
        out = open(output, 'w')
    
    except:
        print "Can't open ouput file : ", output
        sys.exit(1)
        
    out.write(time.strftime('%A %d %B') +  ' processor ' + fullname + '\n')
    out.write('Input file:' + input + '\nMD5: ' + digest + '\n')
        
    for i in range(count):
        now = time.strftime(' %H:%M:%S ');
        out.write('Iteration ' + str(i) + now + '\n')
        out.flush()
        time.sleep(dt);
        
    out.close()
    
    print 'On', today, 'Processor', fullname, 'has completed'
    
def main(argv):

    try:                                
        opts, args = getopt.getopt(argv, 'hdn:',
                                   ['help', 'debug', 'name='])
    except getopt.GetoptError:          
        usage()                         

    global debug
    processor_name = None
    
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-d', '--debug'):
            debug = True
        elif opt in ('-n', '--name'):
            processor_name = val

    if processor_name is None:
        print "The name of this processor is not set"
        usage()
        
    if debug:
        print 'Running', processor_name, 'in debug mode ...'
    else:
        print 'Running', processor_name, 'quietly ...'

    config_dir  = expanduser("~")
    config_file = config_dir + '/.processor.json'
    try:
        if debug:
                print "Looking for resources in: ", config_file
        config = open(config_file, 'r');
    except:
        print "Can't find the config file : ", config_file
        sys.exit(1)
        
    # The resources for this program are found in the config file.
    try:
        rsrc = json.load(config);
        if debug:
                print "Resources found: <", rsrc, '>'
    except:
        print "Can't parse config file : ", config_file
        sys.exit(1)
    
    process(rsrc, processor_name)

if __name__ == '__main__':
    main(sys.argv[1:])

#___________________________________________________________________________
