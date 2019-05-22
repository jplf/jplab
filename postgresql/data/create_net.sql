------------------------------------------------------------------------------

-- Script SQL used to make sure that all is okay with Postgresql.

-- Run psql -h svomtest.svom.fr postgres < create_net.sql

-- Jean-Paul Le Fèvre - April 2016

------------------------------------------------------------------------------

drop table hosts;

create table hosts (
id         serial,
hostname   varchar(16),
ip         inet,
macaddress macaddr,
os         varchar(16),
version    varchar(16),
info       varchar(256)
);

insert into hosts (id, hostname, ip, macaddress, os, version)
       values (default, 'irfupce211',
       '132.166.11.211',
       '70:f3:95:00:d1:f8',
       'slackware', '14.1');

------------------------------------------------------------------------------
