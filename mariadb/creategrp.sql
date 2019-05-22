
------------------------------------------------------------------------------

-- Script SQL used to rebuilt the test database.
-- May work on cloudscape, mysql.
-- See <http://www-3.ibm.com/software/data/cloudscape/pubs/collateral.html>

-- First run :  connect 'jdbc:cloudscape:jvs_database;create=true';
-- Then cloudscape -isql < groups.sql

-- Jean-Paul Le Fèvre April 2003
-- $Id: creategrp.sql,v 1.4 2006/05/11 09:39:24 lefevre Exp $

------------------------------------------------------------------------------

-- A list of groups of items.
drop table if exists groups;
create table groups (
   id	   smallint not null primary key auto_increment,
   name    char(16) not null comment 'name of the group'
) type = MyISAM;

insert into groups (id, name) values (1, 'g1');
insert into groups (id, name) values (2, 'g2');

-- A list of items.
drop table if exists items;
create table items (
   id	    smallint not null primary key auto_increment,
   group_id smallint, foreign key (group_id) references groups(id),
   name     char(16) unique
) type = MyISAM;

insert into items (id, name, group_id) values (1, 'a000', 1);
insert into items (id, name, group_id) values (2, 'b111', 1);
insert into items (id, name, group_id) values (3, 'c222', 2);
insert into items (id, name, group_id) values (4, 'd333', 2);
insert into items (id, name, group_id) values (5, 'e444', 2);

commit;


------------------------------------------------------------------------------
