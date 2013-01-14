insert into Catalog (parent, code, text, shorttext) values (51, 'PALAIS_USER', 'PALAIS_USER', 'PALAIS_USER');

insert into Catalog (id, parent, code, text, shorttext, logical_order) values (3, 2, 'Personen', 'Personen', 'Personen', 2);

insert into Entity (TYPE, NAME) values (181, 'Stephanie Schlegel');
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME) 
values ((select id from Entity where name = 'Stephanie Schlegel'), 141, 'Stephanie', 'Schlegel');

insert into Entity (ID,TYPE,NAME) values (51, 182, 'palais');
insert into Organisation (Id) values (51);

/* Steffie is member of Palais */
insert into EntityRelation (type, subjectid, objectid) values (155, (select id from Entity where name = 'Stephanie Schlegel'), 51);

insert into User (PERSON_ID, NAME, ORGANISATION_ID) values ((select id from Entity where name = 'Stephanie Schlegel'), 'steffie', 51);

insert into Assignment(userid, role, startdate) values ((select id from User where name='steffie'), 51, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='steffie'), (select id from Catalog where parent=51 and code='PALAIS_USER'), CURRENT_DATE);

insert into Policy (role, objectType, action, startDate) values ((select id from Catalog where parent=51 and code='PALAIS_USER'),(select id from Catalog where code ='Personen'),71, CURRENT_DATE);
