
------------------------------------------------------------------------------

-- Script SQL used to recreate accounts in the databases.

-- Run mysql -u root -p  < create_users.sql

-- Jean-Paul Le Fèvre may 2003
-- $Id: create_users.sql,v 1.10 2009/11/19 14:45:01 lefevre Exp $

------------------------------------------------------------------------------

-- Database jvs_database

create user lefevre@'%' identified by 'mot2passe';
create user lefevre@'localhost' identified by 'mot2passe';

grant all privileges on jvs_database.* to lefevre@'%';
grant select, update on jvs_database.* to guest@'%';
grant select         on jvs_database.* to xmmdba@'%';

grant select on test.* to guest@'%';

set password for lefevre@'%' = password('mot2passe');

-- Database xmm_l3s_db

grant all privileges on xmm_l3s_db.* to xmmdba@'%';
grant select         on xmm_l3s_db.* to lefevre@'%';

use mysql;
select host, user, password from user;
select host, user, db from db;

set password for 'formica'@'localhost' = password('mot2passe');


grant select, update, delete, insert on webfwlog.* to logueur@'localhost';
grant select, update, delete, insert on webfwlog.* to logueur@'%';


create database sonar;
grant all privileges on sonar.* to 'lefevre'@'localhost';
grant all privileges on sonar.* to 'lefevre'@'%';

------------------------------------------------------------------------------
