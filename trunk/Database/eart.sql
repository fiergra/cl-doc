insert into Catalog (id, parent, code, text, shorttext) values (54, 51, 'EART_USER', 'EART_USER', 'EART_USER');

insert into Catalog (parent, code, text, shorttext, logical_order, number1) values (2, 'SucheEART', 'Suche', 'Suche', 1, 1001);


insert into Catalog (id, parent, code, text, shorttext) values (1005, 180, 'Landkreis', 'Landkreis', 'Landkreis');
insert into Catalog (id, parent, code, text, shorttext) values (1006, 180, 'Verbandsgemeinde', 'Verbandsgemeinde', 'Verbandsgemeinde');
insert into Catalog (id, parent, code, text, shorttext) values (1007, 180, 'Gemeinde', 'Gemeinde', 'Gemeinde');
insert into Catalog (id, parent, code, text, shorttext) values (1008, 180, 'Gebaeude', 'Gebaeude', 'Gebaeude');

insert into Catalog (id, parent, code, text, shorttext) values (10001, 150, 'Gebaeudetyp', 'Gebaeudetyp', 'Typ');
insert into Catalog (parent, code, text, shorttext) values (10001, 'Rathaus', 'Rathaus', 'Rathaus');
insert into Catalog (parent, code, text, shorttext) values (10001, 'Schule', 'Schule', 'Schule');
insert into Catalog (parent, code, text, shorttext) values (10001, 'KiTa', 'Kindertagesstaette', 'Kindertagesstaette');
insert into Catalog (parent, code, text, shorttext) values (10001, 'KiGa', 'Kindergarten', 'Kindergarten');
insert into Catalog (parent, code, text, shorttext) values (10001, 'Buergerhaus', 'Buergerhaus', 'Buergerhaus');

insert into Catalog (id, parent, code, text, shorttext) values (20002, 150, 'Verfahren', 'Verfahren', 'Verfahren');
insert into Catalog (parent, code, text, shorttext) values (20002, 'ausfuehrlich', 'ausfuehrlich', 'ausfuehrlich');
insert into Catalog (parent, code, text, shorttext) values (20002, 'kurz', 'kurz', 'kurz');


insert into Entity (ID, TYPE, NAME) values (5, 181, 'Stefan Metzdorf');
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME) 
values (5, 45679, 'Stefan', 'Metzdorf');

insert into Entity (ID,TYPE,NAME) values (23, 182, 'EART');
insert into Organisation (Id) values (23);

insert into Entity (ID,TYPE,NAME) values (24, 182, 'mevik');
insert into Organisation (Id) values (24);

/* Stefan is member of mevik */
insert into EntityRelation (type, subjectid, objectid) values (155, 5, 24);

insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (1, 'eart', 23);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (1, 'eart_admin', 23);


insert into Assignment(userid, role, startdate) values ((select id from User where name='eart_admin'), 51, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='eart'), 54, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='eart_user'), 54, CURRENT_DATE);


insert into Policy (role, objectType, action, startDate) values (54,(select id from Catalog where code ='Suche LK'),71, CURRENT_DATE);



insert into Entity (ID, TYPE, NAME) values (1001, 1005, 'Trier-Saarburg');

insert into Entity (ID, TYPE, NAME) values (1101, 1006, 'VG Hermeskeil');
insert into EntityRelation (type, subjectid, objectid) values (157, 1101, 1001);
insert into Entity (ID, TYPE, NAME) values (1102, 1006, 'VG Kell am See');
insert into EntityRelation (type, subjectid, objectid) values (157, 1102, 1001);
insert into Entity (ID, TYPE, NAME) values (1103, 1006, 'VG Konz');
insert into EntityRelation (type, subjectid, objectid) values (157, 1103, 1001);
insert into Entity (ID, TYPE, NAME) values (1104, 1006, 'VG Ruwer');
insert into EntityRelation (type, subjectid, objectid) values (157, 1104, 1001);
insert into Entity (ID, TYPE, NAME) values (1105, 1006, 'VG Saarburg');
insert into EntityRelation (type, subjectid, objectid) values (157, 1105, 1001);
insert into Entity (ID, TYPE, NAME) values (1106, 1006, 'VG Schweich');
insert into EntityRelation (type, subjectid, objectid) values (157, 1106, 1001);
insert into Entity (ID, TYPE, NAME) values (1107, 1006, 'VG Trier-Land');
insert into EntityRelation (type, subjectid, objectid) values (157, 1107, 1001);

insert into Entity (ID, TYPE, NAME) values (1401, 1007, 'Bonerath');
insert into EntityRelation (type, subjectid, objectid) values (157, 1401, 1104);
insert into Entity (ID, TYPE, NAME) values (1402, 1007, 'Farschweiler');
insert into EntityRelation (type, subjectid, objectid) values (157, 1402, 1104);
insert into Entity (ID, TYPE, NAME) values (1403, 1007, 'Gusterath');
insert into EntityRelation (type, subjectid, objectid) values (157, 1403, 1104);
insert into Entity (ID, TYPE, NAME) values (1404, 1007, 'Gutweiler');
insert into EntityRelation (type, subjectid, objectid) values (157, 1404, 1104);
insert into Entity (ID, TYPE, NAME) values (1405, 1007, 'Herl');
insert into EntityRelation (type, subjectid, objectid) values (157, 1405, 1104);
insert into Entity (ID, TYPE, NAME) values (1406, 1007, 'Hinzenburg');
insert into EntityRelation (type, subjectid, objectid) values (157, 1406, 1104);
insert into Entity (ID, TYPE, NAME) values (1407, 1007, 'Holzerath');
insert into EntityRelation (type, subjectid, objectid) values (157, 1407, 1104);
insert into Entity (ID, TYPE, NAME) values (1408, 1007, 'Kasel');
insert into EntityRelation (type, subjectid, objectid) values (157, 1408, 1104);
insert into Entity (ID, TYPE, NAME) values (1409, 1007, 'Korlingen');
insert into EntityRelation (type, subjectid, objectid) values (157, 1409, 1104);
insert into Entity (ID, TYPE, NAME) values (1410, 1007, 'Lorscheid');
insert into EntityRelation (type, subjectid, objectid) values (157, 1410, 1104);
insert into Entity (ID, TYPE, NAME) values (1411, 1007, 'Mertesdorf');
insert into EntityRelation (type, subjectid, objectid) values (157, 1411, 1104);
insert into Entity (ID, TYPE, NAME) values (1412, 1007, 'Morscheid');
insert into EntityRelation (type, subjectid, objectid) values (157, 1412, 1104);
insert into Entity (ID, TYPE, NAME) values (1413, 1007, 'Ollmuth');
insert into EntityRelation (type, subjectid, objectid) values (157, 1413, 1104);
insert into Entity (ID, TYPE, NAME) values (1414, 1007, 'Osburg');
insert into EntityRelation (type, subjectid, objectid) values (157, 1414, 1104);
insert into Entity (ID, TYPE, NAME) values (1415, 1007, 'Pluwig');
insert into EntityRelation (type, subjectid, objectid) values (157, 1415, 1104);
insert into Entity (ID, TYPE, NAME) values (1416, 1007, 'Riveris');
insert into EntityRelation (type, subjectid, objectid) values (157, 1416, 1104);
insert into Entity (ID, TYPE, NAME) values (1417, 1007, 'Schoendorf');
insert into EntityRelation (type, subjectid, objectid) values (157, 1417, 1104);
insert into Entity (ID, TYPE, NAME) values (1418, 1007, 'Sommerau');
insert into EntityRelation (type, subjectid, objectid) values (157, 1418, 1104);
insert into Entity (ID, TYPE, NAME) values (1419, 1007, 'Thomm');
insert into EntityRelation (type, subjectid, objectid) values (157, 1419, 1104);
insert into Entity (ID, TYPE, NAME) values (1420, 1007, 'Waldrach');
insert into EntityRelation (type, subjectid, objectid) values (157, 1420, 1104);

insert into Entity (ID, TYPE, NAME) values (2001, 1008, 'St. Martin Grundschule');
insert into EntityRelation (type, subjectid, objectid) values (156, 2001, 1403);
insert into Entity (ID, TYPE, NAME) values (2002, 1008, 'St. Monika');
insert into EntityRelation (type, subjectid, objectid) values (156, 2002, 1403);

/*
insert into ActClass (Id,Name) values (11001, 'Gebaeude');
insert into LayoutDefinition(typeid, Actclassid, xml) values (3,11001,'<form>
   <line name="Gebaeudetyp" type="List" parent="MASTERDATA.Gebaeudetyp"/>
   <line name="Adresse" type="text"/>
   <line name="Baujahr Gebaeude" type="integer" length="4"/>
   <line name="Baujahr Heizung" type="integer" length="4"/>
   <line name="Nutzflaeche (m2)" type="integer" length="4"/>
<!--   <line name="Farbe" type="List" parent="MASTERDATA.Farbe"/> -->
</form>');

insert into ActClass (Id,Name) values (12001, 'Energiepass');
insert into LayoutDefinition(typeid, Actclassid, xml) values (1,12001,'<form>
   <line source="energiepass.png" type="image" width="100%"/>
   <line name="Nummer" type="integer"/>
   <line label="Erstellt am" name="ErstellDatum" type="date"/>
   <line label="Heizenergieverbrauchskennwert" name="Strom" type="integer"/>
   <line label="Stromverberbrauchskennwert (kWh)" name="Heizung" type="integer"/>
   <line name="Verfahren" type="option" parent="MASTERDATA.Verfahren"/>
   <line source="energiepass_gesamt.png" type="image" width="100%"/>
   </form>');
insert into LayoutDefinition(typeid, Actclassid, xml) values (2,12001,'<pdf size="A4">
    <paragraph align="center" spacingAfter="36" fontSize="36">Energiepass</paragraph>
    <paragraph>Nummer: {Nummer}</paragraph>
    <paragraph spacingBefore="12" spacingAfter="12" fontStyle="bold" fontSize="16">Kennwerte</paragraph>
    <paragraph indentationLeft="18"><phrase fontStyle="bold">Heizung: </phrase><phrase>{Heizung}</phrase></paragraph>
    <paragraph indentationLeft="18"><phrase fontStyle="bold">Strom: </phrase><phrase>{Strom}</phrase></paragraph>
</pdf>');
*/
