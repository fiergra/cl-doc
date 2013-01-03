
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (3, 2, 'Personen', 'Personen', 'Personen', 2);
insert into Catalog (parent, code, text, shorttext, logical_order, number1) values (2, 'SucheDKG', 'Organisation', 'Organisation', 1, 21);

insert into Catalog (parent, code, text, shorttext) values (6, 'Veranstaltungen', 'Veranstaltungen', 'Veranstaltungen');
insert into Catalog (parent, code, text, shorttext) values (6, 'Beratungen', 'Beratungen', 'Beratungen');

insert into Catalog (id, parent, code, text, shorttext) values (55, 51, 'DKG_USER', 'DKG_USER', 'DKG_USER');

insert into Report(NAME, TYPE, XML) values ('Statistik1', 161, '');
insert into Report(NAME, TYPE, XML) values ('Statistik2', 161, '');

insert into Entity (ID, TYPE, NAME) values (2, 181, 'Christian LASCHEK');
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (2, 12345, 'Christian', 'Laschek', '1978-07-07');

insert into Entity (ID, TYPE, NAME) values (4, 181, 'Carlita Metzdorf-Klos');
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (4, 45678, 'Carlita', 'Metzdorf-Klos', '1962-07-07');

insert into Entity (ID,TYPE,NAME) values (21, 182, 'Krebsgesellschaft-Rheinland-Pfalz');
insert into Organisation (Id) values (21);

insert into Entity (ID,TYPE,NAME) values (210, 182, 'Krebsgesellschaft-Trier');
insert into Organisation (Id) values (210);
insert into Address(entity_id, street, number, postcode, city) values(210, 'Brotstr.', '22a', 54290, 'Trier');

insert into Entity (ID,TYPE,NAME) values (211, 182, 'Krebsgesellschaft-Koblenz');
insert into Organisation (Id) values (211);
insert into Address(entity_id, street, number, postcode, city) values(211, 'Löhrstraße', '119', 56068 , 'Koblenz');

insert into Entity (ID,TYPE,NAME) values (212, 182, 'Krebsgesellschaft-Ludwigshafen');
insert into Organisation (Id) values (212);
insert into Address(entity_id, street, number, postcode, city) values(210, 'Ludwigstraße', '65', 67059 , 'Ludwigshafen');

insert into Entity (ID,TYPE,NAME) values (213, 182, 'Krebsgesellschaft-Kaiserslautern');
insert into Organisation (Id) values (213);
insert into Address(entity_id, street, number, co, postcode, city) values(210, 'Hellmut-Hartert-Straße', '1', 'Westpfalz-Klinikum GmbH', 67655 , 'Kaiserslautern');



/* DKG Trier is-part-of DKG RLP */
insert into EntityRelation (type, subjectid, objectid) values (157, 210, 21);
/* DKG Koblenz is-part-of DKG RLP */
insert into EntityRelation (type, subjectid, objectid) values (157, 211, 21);
/* DKG Ludwigshafen is-part-of DKG RLP */
insert into EntityRelation (type, subjectid, objectid) values (157, 212, 21);
/* DKG Kaiserslautern is-part-of DKG RLP */
insert into EntityRelation (type, subjectid, objectid) values (157, 213, 21);

insert into EntityRelation (type, subjectid, objectid) values (155, 1, 25);
insert into EntityRelation (type, subjectid, objectid) values (155, 6, 25);

insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (2, 'laschek', 211);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (4, 'carlita', 210);

insert into Assignment(userid, role, startdate) values ((select id from User where name='laschek'), 51, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='carlita'), 52, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='carlita'), 55, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='carlita'), 52, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='carlita'), 55, CURRENT_DATE);

insert into Policy (role, objectType, action, startDate) values (55,(select id from Catalog where code ='Personen'),71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (55,(select id from Catalog where code ='SucheDKG'),71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (55,(select id from Catalog where code ='Veranstaltungen'),71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (55,(select id from Catalog where code ='Beratungen'),71, CURRENT_DATE);

