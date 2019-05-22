
-- ---------------------------------------------------------------------------

-- Script SQL used to check the content of the cac40 database

-- Run  : sqlite3 cac40.db < dump_cac.sql
-- See also : http://www.sqlite.org

-- Jean-Paul Le Fèvre  - February 2012

-- ---------------------------------------------------------------------------

select 'companies ', count(*) from companies;
select 'company_pages ', count(*) from company_pages;
select 'scores ', count(*) from scores;

-- To get all rows from a table try something like :
-- select * from companies;

-- ---------------------------------------------------------------------------
