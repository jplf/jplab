
-- ---------------------------------------------------------------------------

-- A collection of useful SQL statements used to manipulate the content of
-- the cac40 database.

-- Do NOT run this script as is but instead copy, path statements herein.

-- See also : http://www.sqlite.org

-- Jean-Paul Le Fèvre  - February 2012

-- ---------------------------------------------------------------------------

-- Get the companies and the associated pages.
select t1.main_name, t2.source, t2.path from companies as t1, company_pages as t2 where t1.id = t2.company_id;

-- Get the number of records in the time series.
select count(*) from scores;

-- Fix a path for a page.
select id, main_name from companies where main_name like '%Air%';
select id, path from company_pages where company_id = 2;
update company_pages set path = 'pages/Air-Liquide/108579179167299' where id=30;

-- Select the twitter pages.
select t1.main_name, t2.source, t2.path from companies as t1,
company_pages as t2 where t1.id = t2.company_id and t2.source = 'tw';

-- Select fb scores for a given company
select * from scores where page_id in (select t1.id from company_pages as t1,
companies as t2 where t1.company_id = t2.id and t1.source = 'fb'
and t2.main_name = 'Renault');

-- Select scores for every companies.
select companies.id, companies.main_name, company_pages.id, company_pages.source,
scores.count_1, scores.count_2, scores.count_3 , scores.date from companies
left join company_pages on companies.id = company_pages.company_id
left join scores on company_pages.id = scores.page_id;

-- Remove some scores for messed up data.
select * from companies where main_name like '%Luc%'; --> 3 Alcatel-Lucent
select * from company_pages where company_id = 3; --> 3 fb page
select * from scores where page_id = 3; --> 2733 
delete from scores where page_id = 3 and id < 2700;

-- Fix the database : insert, delete, etc.
update companies set main_name='Legrand' where id = 43;
insert into company_pages (company_id, source, path)
       values (43, 'fb', 'Legrand');

insert into company_pages (company_id, source, path)
       values (46, 'fb', 'pages/Exxon-Mobil/103179436431279');

insert into company_pages (company_id, source, path)
       values (50, 'fb', 'pages/Boeing/25362251550');

update companies set main_name='ATT B2C' where id=40;
update companies set main_name='ATT SAV' where id=48;

insert into company_pages (company_id, source, path)
       values (48, 'fb', 'ATT');
insert into company_pages (company_id, source, path)
       values (44, 'fb', 'lorealparis');

insert into company_pages (company_id, source, path)
       values (49, 'fb', 'BuildingOpportunity');

-- Select companies without twitter pages.
select id, main_name from companies where main_name not in (select t1.main_name
from companies as t1, company_pages as t2
where t1.id = t2.company_id and t2.source = 'tw');

insert into company_pages (company_id, source, path)
       values (8, 'tw', 'Bouygues_C');
insert into company_pages (company_id, source, path)
       values (3, 'tw', 'Alcatel_Lucent');
insert into company_pages (company_id, source, path)
       values (2, 'tw', 'accorhotels');
insert into company_pages (company_id, source, path)
       values (25, 'tw', 'shell');
insert into company_pages (company_id, source, path)
       values (35, 'tw', 'schneiderna');
insert into company_pages (company_id, source, path)
       values (20, 'tw', 'totalsocialhub');
insert into company_pages (company_id, source, path)
       values (10, 'tw', 'capgeminiconsul');
insert into company_pages (company_id, source, path)
       values (15, 'tw', 'edfofficiel');
insert into company_pages (company_id, source, path)
       values (26, 'tw', 'orangeworldwide');

-- Select companies without facebook pages.
select id, main_name from companies where main_name not in (select t1.main_name from companies as t1, company_pages as t2 where t1.id = t2.company_id and t2.source = 'fb');

-- ---------------------------------------------------------------------------
