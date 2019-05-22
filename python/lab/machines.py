#!/usr/bin/env python
# -*- coding: utf-8 -*-
#______________________________________________________________________________

"""
Provides a sample of code to figure out how to use SonarQube.

"""
class Machine:
    """ An object used to test a couple of things """
    name = 'localhost'
    ip_nbr = '127.0.0.1'

    def get_id(self):
        """ Returns the identity of the machine """
        return 'Host : {} {}'.format(self.get_name(), self.get_ip_nbr())

    def set_name(self, host_name):
        """ Changes the name of the machine """
        self.name = host_name

    def get_name(self):
        """ Returns the name of the machine """
        return self.name

    def set_ip_nbr(self, nn_nn_nn_nn):
        """ Changes the ip number of the machine """

        if not self.is_ip_nbr(nn_nn_nn_nn):
            raise ValueError

        self.ip_nbr = nn_nn_nn_nn

    def is_octet(self, nn):
        """ Checks is a string is an int between 0 & 255 """
        if not nn.isdigit():
            return False

        nint = int(nn)
        if nint < 0 or nint > 255:
            return False

        return True

    def is_ip_nbr(self, nn_nn_nn_nn):
        """ Checks the format of an ip number """
        octets = nn_nn_nn_nn.split('.')

        if len(octets) != 4:
            return False

        for octet in octets:
            if not self.is_octet(octet):
                return False

        return True

    def get_ip_nbr(self):
        """ Returns the ip number of the machine """
        return self.ip_nbr


#______________________________________________________________________________
