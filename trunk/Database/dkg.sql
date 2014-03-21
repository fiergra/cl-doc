
insert into Catalog (parent, code, text, shorttext, logical_order, number1) values (2, 'SucheDKG', 'Organisation', 'Organisation', 1, 21);

insert into Catalog (parent, code, text, shorttext) values (6, 'Veranstaltungen', 'Veranstaltungen', 'Veranstaltungen');
insert into Catalog (parent, code, text, shorttext) values (6, 'Beratungen', 'Beratungen', 'Beratungen');

insert into Catalog (id, parent, code, text, shorttext) values (55, 51, 'DKG_USER', 'DKG_USER', 'DKG_USER');

insert into Report(NAME, TYPE, XML) values ('Veranstaltungen', 161, '<?xml version="1.0" encoding="UTF-8"?>
<report name="Certificates" autoexec="false" aggregate="true" maxRecords="1000">
  <params>
     <param name="perId" label="person" type="person" mandatory="false" column="pers.per_id"></param>
     <param label="encodingDate" name="ENCODINGDATEFROM" type="Date" initialValue="sysdate - 7" mandatory="false" column="i.date_create" operators="&gt;,="></param>
     <param label="hideSent" mandatory="false" name="hideSent" type="boolean" initialValue="true" where="NOT EXISTS (SELECT /*+ INDEX (icf)*/ field_date
          FROM smd_item_fields f
          INNER JOIN smd_item_class_fields icf on f.icf_id = icf.icf_id AND icf.field_name = ''sentDate'' AND f.field_date is not null
         WHERE f.ite_id = i.ite_id)"></param>
     <param label="Geschlecht" name="state" type="list" parent="MASTERDATA.GENDER" mandatory="false" />
     <param label="Geschlecht" name="state" type="option" parent="MASTERDATA.GENDER" mandatory="false" />
     <param label="Geschlecht" name="state" type="multiselect" parent="MASTERDATA.GENDER" mandatory="false" />
</params>
  <result>
    <column name="ITE_ID" type="Long" visible="false"></column>
    <column name="OST_CD" label="state" type="string" visible="false"></column>
    <column name="PER_ID" label="id" type="Long"></column>
    <column name="LASTNAME" label="lastname" type="String"></column>
    <column name="FIRSTNAME" label="firstname" type="String"></column>
    <column name="BIRTH_DATE" label="birthDate" type="Date"></column>
    <column name="DATE_FROM" label="from" type="Date"></column>
    <column name="PERIODFROM" label="" type="String" encrypted="true"></column>
    <column name="DATE_TO" label="to" type="Date"></column>
    <column name="PERIODTO" label="" type="String" encrypted="true"></column>
    <column name="TYPECODE" label="Code" type="String" translate="true"></column>
    <column name="ACCEPTED" label="accepted" type="String"></column>
    <column name="DATE_CREATE" label="encodingDate" type="Date"></column>
    <column name="SENTDATE" label="sentDate" type="Date"></column>
    <column name="GECOCOMMENT" label="GecoComment" type="String" encrypted="true"></column>
  </result>
  <query>
select /*+ first_rows INDEX (p) INDEX(e) INDEX(pers)*/ i.ost_cd, i.ite_id, i.date_from, i.date_to, i.date_create, e.per_id, pers.firstname,
       pers.lastname, pers.birth_date, pers.gender, field_name, icf.ift_cd, f.*
from smd_participations p
  INNER JOIN smd_items i ON (p.ite_id = i.ite_id OR p.ite_id = i.ite_id_parent)
  INNER JOIN smd_entities e ON e.ent_id = p.ent_id
  INNER JOIN smd_persons pers ON e.per_id = pers.per_id
  INNER JOIN smd_item_fields f ON f.ITE_ID = i.ITE_ID
  INNER JOIN smd_item_class_fields icf on f.icf_id = icf.icf_id  AND icf.field_name IN(''accepted'')  
 WHERE i.icl_id = 78  
</query>
</report>');
insert into Report(NAME, TYPE, XML) values ('Beratungen', 161, '');

insert into Entity (ID, TYPE, NAME) values (2, 181, 'Christian LASCHEK');
insert into Person (ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (2, 'Christian', 'Laschek', '1978-07-07');

insert into Entity (ID, TYPE, NAME) values (4, 181, 'Carlita Metzdorf-Klos');
insert into Person (ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (4, 'Carlita', 'Metzdorf-Klos', '1962-07-07');

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

