#!/usr/bin/env python
# -*- coding: utf-8 -*-
#______________________________________________________________________________

"""
Provides a test suite for Machine.

"""
from machines import Machine

def test_get_id():
    """ Returns the identity of the machine """
    host = Machine()
    assert host.get_id() == 'Host : localhost 127.0.0.1'

def test_set_name():
    """ Changes the name of the machine """
    host = Machine()
    name = 'hurukan'
    host.set_name(name)
    assert host.get_name() == name
    
def test_is_octet_1():
    """ Checks that a string represents an octet """
    host = Machine()
    val = '132'
    assert host.is_octet(val)
    
def test_is_octet_2():
    """ Checks that a string represents an octet """
    host = Machine()
    val = 'val'
    assert not host.is_octet(val)

def test_is_octet_3():
    """ Checks that a string represents an octet """
    host = Machine()
    val = '258'
    assert not host.is_octet(val)
    
def test_set_ip_nbr():
    """ Sets the ip number of the machine """
    host = Machine()
    ip_nbr = '132.166.28.183'
    host.set_ip_nbr(ip_nbr)
    assert host.get_ip_nbr() == ip_nbr


#______________________________________________________________________________
