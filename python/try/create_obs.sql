-- -----------------------------------------------------------------------------

-- Script SQL used to create tables for MySQL.

-- Jean-Paul Le Fèvre October 2011
-- $Id$
-- -----------------------------------------------------------------------------

use jvs_database;

-- Table observatories

drop table if exists observatories;

create table observatories (
   id	     int not null primary key auto_increment,
   name      varchar(32),
   location  varchar(128),
   latitude  real,
   longitude real,
   web_site  varchar(128),
   weather   varchar(256)
);

insert into observatories
(name, location, latitude, longitude, web_site, weather)
values('La Silla', '-29 d 15 m, 70 d 44 m',
-29.25, -70.733,
'http://www.eso.org/sci/facilities/lasilla/index.html',
'http://archive.eso.org/asm/ambient-server?site=lasilla');

insert into observatories
(name, location, latitude, longitude, web_site, weather)
values('Paranal',  '-24 d 37 m, 70 d 24 m',
-24.617, -70.4,
'http://www.eso.org/sci/facilities/paranal/index.html',
'http://archive.eso.org/asm/ambient-server?site=paranal');

insert into observatories
(name, location, latitude, longitude, web_site, weather)
values('CFHT',  '19 d 49.6 m, 155 d 28.3 m',
19.827, -155.472,
'http://www.cfht.hawaii.edu/',
'http://www.cfht.hawaii.edu/en/science/weather.php');

insert into observatories
(name, location, latitude, longitude, web_site, weather)
values('La Palma',  '28 d 45.5 m, 17 d 52.8 m',
28.758, -18.213,
'http://www.iac.es/eno.php?op1=2&lang=en',
'http://www.not.iac.es/weather/index.php');

insert into observatories
(name, location, latitude, longitude, web_site, weather)
values('Siding Spring',  '-31.267, 149.067',
-31.267, 149.067,
'http://www.mso.anu.edu.au/info/sso/',
'http://150.203.153.131/~hatuser/wth/');

-- -----------------------------------------------------------------------------
