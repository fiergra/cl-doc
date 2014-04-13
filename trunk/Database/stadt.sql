
insert into Catalog (id, parent, code, text, shorttext) values (65, 51, 'Jugend_USER', 'Jugend_USER', 'Jugend_USER');
insert into Catalog (id, parent, code, text, shorttext) values (66, 51, 'Jugend_MANAGER', 'Jugend_MANAGER', 'Jugend_MANAGER');

insert into Entity (ID,TYPE,NAME) values (50, 182, 'Jugendhilfeplanung');

insert into Entity (ID,TYPE,NAME) values (501, 182, 'Bund der deutschen katholischen Jugend (BDKJ)');
insert into Entity (ID,TYPE,NAME) values (502, 182, 'Bürgerhaus Trier-Nord (Stadtteilorientierte Kinder- & Jugendarbeit)');
insert into Entity (ID,TYPE,NAME) values (503, 182, 'Bürgerservice GmbH');
insert into Entity (ID,TYPE,NAME) values (504, 182, 'Club Aktiv');
insert into Entity (ID,TYPE,NAME) values (505, 182, 'CVJM (Christlicher Verein Junger Menschen)');
insert into Entity (ID,TYPE,NAME) values (506, 182, 'Deutscher Gewerkschaftsbund (DGB Jugend)');
insert into Entity (ID,TYPE,NAME) values (507, 182, 'Deutscher Kinderschutzbund');
insert into Entity (ID,TYPE,NAME) values (508, 182, 'Die Tür - Suchtberatung Trier e.V.');
insert into Entity (ID,TYPE,NAME) values (509, 182, 'Exzellenzhaus e.V. Kinder-Jugend-Kultur');
insert into Entity (ID,TYPE,NAME) values (510, 182, 'Fachstelle für Kinder- und Jugendpastoral Trier');
insert into Entity (ID,TYPE,NAME) values (511, 182, 'Geschäftsstelle der Jugendvertretung Trier');
insert into Entity (ID,TYPE,NAME) values (512, 182, 'Jugendtreff DBH');
insert into Entity (ID,TYPE,NAME) values (513, 182, 'Jugendtreff Ehrang-Quint e.V.');
insert into Entity (ID,TYPE,NAME) values (514, 182, 'Jugendwerk Don Bosco');
insert into Entity (ID,TYPE,NAME) values (515, 182, 'Jugendzentrum Euren');
insert into Entity (ID,TYPE,NAME) values (516, 182, 'JUZ Südpol-Palais e.V.');
insert into Entity (ID,TYPE,NAME) values (517, 182, 'LERNEN FÖRDERN');
insert into Entity (ID,TYPE,NAME) values (518, 182, 'Malteser Hilfsdienst e.V. - Jugendreferat');
insert into Entity (ID,TYPE,NAME) values (519, 182, 'Marine-Jugend 1973 Trier e.V.');
insert into Entity (ID,TYPE,NAME) values (520, 182, 'MJC Jugendzentrum Mergener Hof e.V.');
insert into Entity (ID,TYPE,NAME) values (521, 182, 'mobile spielaktion e.V.');
insert into Entity (ID,TYPE,NAME) values (522, 182, 'Naturfreunde Trier-Quint e.V.');
insert into Entity (ID,TYPE,NAME) values (523, 182, 'Offener Jugendtreff Mariahof');
insert into Entity (ID,TYPE,NAME) values (524, 182, 'Palais e.V.');
insert into Entity (ID,TYPE,NAME) values (525, 182, 'pro familia Trier e.V.');
insert into Entity (ID,TYPE,NAME) values (526, 182, 'Sportjugend Rheinland');
insert into Entity (ID,TYPE,NAME) values (527, 182, 'Stadtsportverband');
insert into Entity (ID,TYPE,NAME) values (528, 182, 'Stadtverwaltung Trier');
insert into Entity (ID,TYPE,NAME) values (529, 182, 'Starthilfe Trier e.V.');
insert into Entity (ID,TYPE,NAME) values (530, 182, 'TINA e.V.');
insert into Entity (ID,TYPE,NAME) values (531, 182, 'Treffpunkt Am Weidengraben e.V.');
insert into Entity (ID,TYPE,NAME) values (532, 182, 'triki-büro');
insert into Entity (ID,TYPE,NAME) values (533, 182, 'ver.di-Jugend Region Trier');
insert into Entity (ID,TYPE,NAME) values (534, 182, 'Verein für Jugendfreizeiten e.V.');

insert into Organisation (Id) values (50);
insert into Organisation (Id) values (501);
insert into Organisation (Id) values (502);
insert into Organisation (Id) values (503);
insert into Organisation (Id) values (504);
insert into Organisation (Id) values (505);
insert into Organisation (Id) values (506);
insert into Organisation (Id) values (507);
insert into Organisation (Id) values (508);
insert into Organisation (Id) values (509);
insert into Organisation (Id) values (510);
insert into Organisation (Id) values (511);
insert into Organisation (Id) values (512);
insert into Organisation (Id) values (513);
insert into Organisation (Id) values (514);
insert into Organisation (Id) values (515);
insert into Organisation (Id) values (516);
insert into Organisation (Id) values (517);
insert into Organisation (Id) values (518);
insert into Organisation (Id) values (519);
insert into Organisation (Id) values (520);
insert into Organisation (Id) values (521);
insert into Organisation (Id) values (522);
insert into Organisation (Id) values (523);
insert into Organisation (Id) values (524);
insert into Organisation (Id) values (525);
insert into Organisation (Id) values (526);
insert into Organisation (Id) values (527);
insert into Organisation (Id) values (528);
insert into Organisation (Id) values (529);
insert into Organisation (Id) values (530);
insert into Organisation (Id) values (531);
insert into Organisation (Id) values (532);
insert into Organisation (Id) values (533);
insert into Organisation (Id) values (534);


insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 501, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 502, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 503, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 504, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 505, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 506, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 507, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 508, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 509, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 510, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 511, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 512, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 513, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 514, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 515, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 516, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 517, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 518, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 519, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 520, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 521, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 522, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 523, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 524, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 525, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 526, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 527, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 528, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 529, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 530, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 531, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 532, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 533, 50, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (158, 534, 50, CURRENT_DATE);

insert into Entity (ID, TYPE, NAME) values (5000, 181, 'Bettina Mann');
insert into Person (ID, FIRSTNAME, LASTNAME) values (5000, 'Bettina', 'Mann');
insert into EntityRelation (type, subjectid, objectid, startdate) values (155, 5000, 50, CURRENT_DATE);

insert into Entity (ID, TYPE, NAME) values (5001, 181, 'Stefan Zawar-Schlegel');
insert into Person (ID, FIRSTNAME, LASTNAME) values (5001, 'Stefan', 'Zawar-Schlegel');
insert into EntityRelation (type, subjectid, objectid, startdate) values (155, 5001, 531, CURRENT_DATE);

insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (5000, 'bmann', 50);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (5001, 'tawsteff', 531);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (1, 'ze', 531);

insert into Assignment(userid, role, startdate) values ((select id from User where name='bmann'), 65, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='bmann'), 66, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='tawsteff'), 65, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='tawsteff'), 66, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='ze'), 67, CURRENT_DATE);

insert into Catalog (parent, code, text, shorttext, logical_order, number1) values (2, 'JugendOrga', 'Organisation', 'Organisation', 1, 50);

/*insert into Policy (role, objectType, action, startDate) values (66,(select id from Catalog where code ='SucheDKG'),71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (65,(select id from Catalog where code ='Personen'),71, CURRENT_DATE);*/
insert into Policy (role, objectType, action, startDate) values (65,(select id from Catalog where code ='JugendOrga'),71, CURRENT_DATE);



insert into Catalog (id, parent, code, text, shorttext) values (550, 150, 'Altersgruppe', 'Altersgruppe', 'Altersgruppe');
insert into Catalog (parent, code, text, shorttext) values (550, '6-12', '6-12 Jahre', '6-12');
insert into Catalog (parent, code, text, shorttext) values (550, '13-15', '13-15 Jahre', '13-15');
insert into Catalog (parent, code, text, shorttext) values (550, '16-20', '16-20 Jahre', '16-20');
insert into Catalog (parent, code, text, shorttext) values (550, '21-27', '21-27 Jahre', '21-27');
