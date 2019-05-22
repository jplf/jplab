
-- ---------------------------------------------------------------------------

-- Script SQL used to update the content of the cac40 database

-- DO NOT run this file as is. Statements stored here have to used as
-- examples.
-- See also : http://www.sqlite.org

-- Jean-Paul Le Fèvre  - February 2012

-- ---------------------------------------------------------------------------

update companies set web_site = 'www.credit-agricole.com' where id=15;
update companies set web_site = 'www.francetelecom.com' where id=20;
update companies set web_site = 'www.societegenerale.fr' where id=36;

update companies set web_site = 'www.accor.fr' where main_name = 'Accor';
update companies set web_site = 'www.airliquide.com' where main_name = 'Air liquide';
update companies set web_site = 'www.alcatel-lucent.com' where main_name = 'Alcatel-Lucent';
update companies set web_site = 'www.alstom.com' where main_name = 'Alstom';
update companies set web_site = 'www.arcelormittal.com' where main_name = 'Arcelor-Mittal';
update companies set web_site = 'www.axa.com' where main_name = 'AXA';
update companies set web_site = 'www.bnpparibas.com' where main_name = 'BNP Paribas';
update companies set web_site = 'www.bnpparibas.com' where main_name = 'BNP Paribas';
update companies set web_site = 'www.bouygues.com' where id = 9;
update companies set web_site = 'www.institutionnel.bouyguestelecom.fr' where id = 10;
update companies set web_site = 'www.bouygues-construction.com' where id = 11;
update companies set web_site = 'www.capgemini.com' where main_name = 'Cap Gemini';
update companies set web_site = 'www.capgemini.com' where main_name = 'Cap Gemini';
update companies set web_site = 'www.carrefour.com' where main_name = 'Carrefour';
update companies set web_site = 'www.danone.com' where main_name = 'Danone';
update companies set web_site = 'www.eads.com' where main_name = 'EADS';
update companies set web_site = 'www.edf.com' where main_name = 'EDF';
update companies set web_site = 'www.essilor.com' where main_name = 'Essilor International';
update companies set web_site = 'www.francetelecom.com' where main_name = 'France Télécom';
update companies set web_site = 'www.gdfsuez.com' where main_name = 'GDF Suez';
update companies set web_site = 'www.loreal.fr' where id = 22;
update companies set web_site = 'www.lafarge.com' where main_name = 'Lafarge SA';
update companies set web_site = 'www.legrand.com' where main_name = 'Legrand';
update companies set web_site = 'www.lvmh.com' where main_name = 'LVMH';
update companies set web_site = 'www.michelin.com' where main_name = 'Michelin';
update companies set web_site = 'www.pernod-ricard.com' where main_name = 'Pernod-Ricard';
update companies set web_site = 'www.peugeot.com' where main_name = 'Peugeot';
update companies set web_site = 'www.ppr.com' where main_name = 'PPR';
update companies set web_site = 'www.publicisgroupe.com' where main_name = 'Publicis Groupe';
update companies set web_site = 'www.renault.com' where main_name = 'Renault';
update companies set web_site = 'www.safran-group.com' where main_name = 'Safran';
update companies set web_site = 'www.saint-gobain.com' where main_name = 'Saint-Gobain';
update companies set web_site = 'www.sanofi.com' where main_name = 'Sanofi';
update companies set web_site = 'www.schneider-electric.com' where main_name = 'Schneider Electric';
update companies set web_site = 'www.societegenerale.fr' where main_name = 'Société générale';
update companies set web_site = 'www.st.com' where main_name = 'STMicroelectronics';
update companies set web_site = 'www.technip.com' where main_name = 'Technip';
update companies set web_site = 'www.total.com' where main_name = 'Total';
update companies set web_site = 'www.unibail-rodamco.com' where main_name = 'Unibail-Rodamco';
update companies set web_site = 'www.vallourec.com' where main_name = 'Vallourec';
update companies set web_site = 'www.veolia.com' where main_name = 'Veolia Environnement';
update companies set web_site = 'www.vinci.com' where main_name = 'Vinci';
update companies set web_site = 'www.vivendi.com' where main_name = 'Vivendi';
update companies set web_site = 'www.exxonmobil.com' where main_name = 'Exxon Mobil';
update companies set web_site = 'www.chevron.com' where main_name = 'Chevron';
update companies set web_site = 'www.petrobras.com' where main_name = 'Petrobras';
update companies set web_site = 'www.bp.com' where main_name = 'BP';
insert into company_pages (company_id, source, path) values
(2, 'fb','AirLiquide');
insert into company_pages (company_id, source, path) values
(5, 'fb','arcelornmittal');
insert into company_pages (company_id, source, path) values
(6, 'fb','AXAEquitable');
insert into company_pages (company_id, source, path) values
(17, 'fb','EADSfan');
insert into company_pages (company_id, source, path) values
(21, 'fb','gdfsuez');
insert into company_pages (company_id, source, path) values
(22, 'fb','lorealparis');
insert into company_pages (company_id, source, path) values
(23, 'fb','Lafarge');
insert into company_pages (company_id, source, path) values
(24, 'fb','Legrand');
insert into company_pages (company_id, source, path) values
(25, 'fb','lvmh');
insert into company_pages (company_id, source, path) values
(28, 'fb','Peugeot');
insert into company_pages (company_id, source, path) values
(30, 'fb','publicisgroupe');
insert into company_pages (company_id, source, path) values
(31, 'fb','renault');
insert into company_pages (company_id, source, path) values
(33, 'fb','saintgobaingroup');
insert into company_pages (company_id, source, path) values
(35, 'fb','SchneiderElectricFrance');
insert into company_pages (company_id, source, path) values
(36, 'fb','societegeneralebank');
insert into company_pages (company_id, source, path) values
(43, 'fb','Vinci.Group');

-- ---------------------------------------------------------------------------
