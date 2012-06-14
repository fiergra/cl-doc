
insert into Catalog (id, parent, code, text, shorttext) values (1005, 1000, 'Landkreis', 'Landkreis', 'Landkreis');
insert into Catalog (id, parent, code, text, shorttext) values (1006, 1000, 'Verbandsgemeinde', 'Verbandsgemeinde', 'Verbandsgemeinde');
insert into Catalog (id, parent, code, text, shorttext) values (1007, 1000, 'Gemeinde', 'Gemeinde', 'Gemeinde');
insert into Catalog (id, parent, code, text, shorttext) values (1008, 1000, 'Gebaeude', 'Gebaeude', 'Gebaeude');

insert into Catalog (id, parent, code, text, shorttext) values (10001, 150, 'Gebaeudetyp', 'Gebaeudetyp', 'Typ');
insert into Catalog (parent, code, text, shorttext) values (10001, 'Rathaus', 'Rathaus', 'Rathaus');
insert into Catalog (parent, code, text, shorttext) values (10001, 'Schule', 'Schule', 'Schule');
insert into Catalog (parent, code, text, shorttext) values (10001, 'KiTa', 'Kindertagesstaette', 'Kindertagesstaette');
insert into Catalog (parent, code, text, shorttext) values (10001, 'KiGa', 'Kindergarten', 'Kindergarten');
insert into Catalog (parent, code, text, shorttext) values (10001, 'Buergerhaus', 'Buergerhaus', 'Buergerhaus');


insert into Entity (ID, TYPE, NAME) values (1001, 1005, 'Trier-Saarburg');

insert into Entity (ID, TYPE, NAME) values (1101, 1006, 'VG Hermeskeil');
insert into entityrelation (type, subjectid, objectid) values (157, 1101, 1001);
insert into Entity (ID, TYPE, NAME) values (1102, 1006, 'VG Kell am See');
insert into entityrelation (type, subjectid, objectid) values (157, 1102, 1001);
insert into Entity (ID, TYPE, NAME) values (1103, 1006, 'VG Konz');
insert into entityrelation (type, subjectid, objectid) values (157, 1103, 1001);
insert into Entity (ID, TYPE, NAME) values (1104, 1006, 'VG Ruwer');
insert into entityrelation (type, subjectid, objectid) values (157, 1104, 1001);
insert into Entity (ID, TYPE, NAME) values (1105, 1006, 'VG Saarburg');
insert into entityrelation (type, subjectid, objectid) values (157, 1105, 1001);
insert into Entity (ID, TYPE, NAME) values (1106, 1006, 'VG Schweich');
insert into entityrelation (type, subjectid, objectid) values (157, 1106, 1001);
insert into Entity (ID, TYPE, NAME) values (1107, 1006, 'VG Trier-Land');
insert into entityrelation (type, subjectid, objectid) values (157, 1107, 1001);

insert into Entity (ID, TYPE, NAME) values (1401, 1007, 'Bonerath');
insert into entityrelation (type, subjectid, objectid) values (157, 1401, 1104);
insert into Entity (ID, TYPE, NAME) values (1402, 1007, 'Farschweiler');
insert into entityrelation (type, subjectid, objectid) values (157, 1402, 1104);
insert into Entity (ID, TYPE, NAME) values (1403, 1007, 'Gusterath');
insert into entityrelation (type, subjectid, objectid) values (157, 1403, 1104);
insert into Entity (ID, TYPE, NAME) values (1404, 1007, 'Gutweiler');
insert into entityrelation (type, subjectid, objectid) values (157, 1404, 1104);
insert into Entity (ID, TYPE, NAME) values (1405, 1007, 'Herl');
insert into entityrelation (type, subjectid, objectid) values (157, 1405, 1104);
insert into Entity (ID, TYPE, NAME) values (1406, 1007, 'Hinzenburg');
insert into entityrelation (type, subjectid, objectid) values (157, 1406, 1104);
insert into Entity (ID, TYPE, NAME) values (1407, 1007, 'Holzerath');
insert into entityrelation (type, subjectid, objectid) values (157, 1407, 1104);
insert into Entity (ID, TYPE, NAME) values (1408, 1007, 'Kasel');
insert into entityrelation (type, subjectid, objectid) values (157, 1408, 1104);
insert into Entity (ID, TYPE, NAME) values (1409, 1007, 'Korlingen');
insert into entityrelation (type, subjectid, objectid) values (157, 1409, 1104);
insert into Entity (ID, TYPE, NAME) values (1410, 1007, 'Lorscheid');
insert into entityrelation (type, subjectid, objectid) values (157, 1410, 1104);
insert into Entity (ID, TYPE, NAME) values (1411, 1007, 'Mertesdorf');
insert into entityrelation (type, subjectid, objectid) values (157, 1411, 1104);
insert into Entity (ID, TYPE, NAME) values (1412, 1007, 'Morscheid');
insert into entityrelation (type, subjectid, objectid) values (157, 1412, 1104);
insert into Entity (ID, TYPE, NAME) values (1413, 1007, 'Ollmuth');
insert into entityrelation (type, subjectid, objectid) values (157, 1413, 1104);
insert into Entity (ID, TYPE, NAME) values (1414, 1007, 'Osburg');
insert into entityrelation (type, subjectid, objectid) values (157, 1414, 1104);
insert into Entity (ID, TYPE, NAME) values (1415, 1007, 'Pluwig');
insert into entityrelation (type, subjectid, objectid) values (157, 1415, 1104);
insert into Entity (ID, TYPE, NAME) values (1416, 1007, 'Riveris');
insert into entityrelation (type, subjectid, objectid) values (157, 1416, 1104);
insert into Entity (ID, TYPE, NAME) values (1417, 1007, 'Schoendorf');
insert into entityrelation (type, subjectid, objectid) values (157, 1417, 1104);
insert into Entity (ID, TYPE, NAME) values (1418, 1007, 'Sommerau');
insert into entityrelation (type, subjectid, objectid) values (157, 1418, 1104);
insert into Entity (ID, TYPE, NAME) values (1419, 1007, 'Thomm');
insert into entityrelation (type, subjectid, objectid) values (157, 1419, 1104);
insert into Entity (ID, TYPE, NAME) values (1420, 1007, 'Waldrach');
insert into entityrelation (type, subjectid, objectid) values (157, 1420, 1104);