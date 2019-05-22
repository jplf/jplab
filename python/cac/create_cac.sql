
-- ---------------------------------------------------------------------------

-- Script SQL used to rebuilt the CAC 40 database

-- Run  : sqlite3 cac40.db < create_cac.sql
-- TAKE CARE : running this script as is deletes an existing database.

-- See also : http://www.sqlite.org/lang_createtable.html

-- Jean-Paul Le Fèvre  - February 2012

-- To backup or restore the content of the db use :
-- .backup  main cac40.bak
-- .restore main cac40.bak

-- To dump or load the constant tables.
-- .output companies.sql
-- .dump %comp%

-- ---------------------------------------------------------------------------

drop table if exists companies;
create table if not exists companies (
       id integer primary key autoincrement,
       main_name     text not null unique,
       group_name    text,
       web_site      text,
       stock_index   text,
       sector        text,
       country_code  text
);

-- For instance the following records may be inserted.
-- insert into companies
-- (main_name, group_name, web_site, stock_index, sector, country_code) values 
-- ('Accor', null, 'www.accor.com', 'cac 40', 'hotel', 'fr');

-- insert into companies
-- (main_name,  group_name, web_site, stock_index, sector, country_code) values 
-- ('EDF', null, 'www.edf.com', 'cac 40', 'electricity', 'fr');

-- insert into companies
-- (main_name,  group_name, web_site, stock_index, sector, country_code) values 
-- ('Total', null, 'www.total.com', 'cac 40', 'energy', 'fr');


-- The source column indicates if it is a facebook or a twitter page.
-- The path is the link to the page on the source site.
-- The type specifies the kind of page.

drop table if exists company_pages;
create table if not exists company_pages (
       id integer primary key autoincrement,
       company_id integer references companies(id),
       source text,
       path   text,
       type   text
);

-- Examples :
-- insert into company_pages (company_id, source, path, type)
-- values (1, 'fb', 'Accor', 'corporate');

-- insert into company_pages (company_id, source, path, type)
-- values (1, 'fb', 'accorhotels', 'b2c');

-- insert into company_pages (company_id, source, path, type)
-- values (2, 'fb', 'edf', 'corporate');

-- insert into company_pages (company_id, source, path, type)
-- values (2, 'fb', 'teamedf', 'sponsoring');

-- insert into company_pages (company_id, source, path, type)
-- values (3, 'fb', 'Total', 'corporate');

-- The last table stores the interesting values fetched from the pages.
-- The content of the count columns depends on the pages.
drop table if exists scores;
create table if not exists scores (
       id integer primary key autoincrement,
       page_id integer references company_pages(id),
       count_1 int default 0,
       count_2 int default 0,
       count_3 int default 0,
       date text default current_timestamp
);

-- ---------------------------------------------------------------------------
