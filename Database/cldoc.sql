DROP DATABASE ClDoc;

CREATE DATABASE ClDoc;

USE ClDoc;

DROP TABLE IF EXISTS Assignment;
DROP TABLE IF EXISTS LayoutDefinition;
DROP TABLE IF EXISTS Participation;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Person;
DROP TABLE IF EXISTS Organisation;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS Address;
DROP TABLE IF EXISTS LogEntry;
DROP TABLE IF EXISTS Entity;
DROP TABLE IF EXISTS ActField;
DROP TABLE IF EXISTS Act;
DROP TABLE IF EXISTS ActClassField;
DROP TABLE IF EXISTS ActClass;
DROP TABLE IF EXISTS Report;
DROP TABLE IF EXISTS Catalog;
DROP TABLE IF EXISTS EntityRelation;


CREATE TABLE IF NOT EXISTS Catalog (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    Parent INTEGER,
    CONSTRAINT FK_CATALOG_CATALOG FOREIGN KEY (Parent)
        references Catalog (ID) on delete CASCADE,
    CODE VARCHAR(64),
    CONSTRAINT UK_CATALOG_CODE UNIQUE KEY (PARENT,CODE),
    TEXT VARCHAR(400),
    SHORTTEXT VARCHAR(400),
    Date DATE,
    Number1 INTEGER,
    Number2 INTEGER,
    LOGICAL_ORDER INTEGER 
    );

ALTER TABLE Catalog AUTO_INCREMENT = 10000;

CREATE TABLE IF NOT EXISTS Report (
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(400) NOT NULL,
    type INTEGER NOT NULL,
    CONSTRAINT FK_Report_CATALOG FOREIGN KEY (type)
        references Catalog (ID),
    XML TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Entity(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    type INTEGER NOT NULL,
    CONSTRAINT FK_Entity_CATALOG FOREIGN KEY (type)
        references Catalog (ID),
    NAME VARCHAR(400)
);

ALTER TABLE Entity AUTO_INCREMENT = 100000;


CREATE TABLE IF NOT EXISTS Setting (
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    EntityID INTEGER,
    CONSTRAINT FK_Setting_ENTITY FOREIGN KEY (EntityID)
        REFERENCES ENTITY (Id),
    NAME VARCHAR(250) NOT NULL,
    VALUE VARCHAR(400)
);




CREATE TABLE IF NOT EXISTS EntityRelation (
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    type INTEGER NOT NULL,
    CONSTRAINT FK_EntityRelation_CATALOG FOREIGN KEY (type)
        references Catalog (ID),
    SubjectID INTEGER NOT NULL,
    CONSTRAINT FK_EntityRelationSubject_ENTITY FOREIGN KEY (SubjectID)
        REFERENCES ENTITY (Id),
    ObjectID INTEGER NOT NULL,
    CONSTRAINT FK_EntityRelationObject_ENTITY FOREIGN KEY (ObjectID)
        REFERENCES ENTITY (Id),
    StartDate DATE NOT NULL,
    EndDate DATE 
);


CREATE TABLE IF NOT EXISTS Address(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ENTITY_ID INTEGER NOT NULL,
    CONSTRAINT FK_ADDRESS_ENTITY FOREIGN KEY (ENTITY_ID)
        REFERENCES ENTITY (Id),
    STREET VARCHAR(400),
    NUMBER VARCHAR(400),
    CITY VARCHAR(400),
    POSTCODE VARCHAR(400),
    PHONE VARCHAR(400),
    NOTE VARCHAR(400),
    CO VARCHAR(400)
);


CREATE TABLE IF NOT EXISTS Person(
    Id INTEGER PRIMARY KEY NOT NULL,
    CONSTRAINT FK_PERSON_ENTITY FOREIGN KEY (Id)
        REFERENCES ENTITY (Id),
    FIRSTNAME VARCHAR(400),
    SECONDNAME VARCHAR(400),
    LASTNAME VARCHAR(400),
    MAIDENNAME VARCHAR(400),
    DATEOFBIRTH DATE,
    GENDER INTEGER,
    CONSTRAINT FK_PERSON_CATALOG FOREIGN KEY (GENDER)
        references Catalog (ID),
    SNDX_FIRSTNAME VARCHAR(400),
    SNDX_LASTNAME VARCHAR(400)
);


CREATE TABLE IF NOT EXISTS Patient (
    Id INTEGER PRIMARY KEY NOT NULL,
    CONSTRAINT FK_PATIENT_PERSON FOREIGN KEY (Id)
        REFERENCES PERSON (Id),
    PER_ID INTEGER NOT NULL
);


CREATE TABLE IF NOT EXISTS Organisation(
    Id INTEGER PRIMARY KEY NOT NULL,
    CONSTRAINT FK_Organisation_ENTITY FOREIGN KEY (Id)
        REFERENCES ENTITY (Id)
);

CREATE TABLE IF NOT EXISTS User(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    PERSON_ID INTEGER NOT NULL,
    CONSTRAINT FK_USER_PERSON FOREIGN KEY (PERSON_ID)
        REFERENCES PERSON (Id),
    ORGANISATION_ID INTEGER NOT NULL,
    CONSTRAINT FK_USER_ORGANISATION FOREIGN KEY (ORGANISATION_ID)
        REFERENCES ORGANISATION (Id),
    NAME VARCHAR(400),
    HASH VARCHAR(400)
);



CREATE TABLE IF NOT EXISTS Room(
    Id INTEGER PRIMARY KEY NOT NULL,
    CONSTRAINT FK_Room_ENTITY FOREIGN KEY (Id)
        REFERENCES ENTITY (Id)
);


CREATE TABLE IF NOT EXISTS ActClass(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(250) UNIQUE NOT NULL,
    summaryDef VARCHAR(400),
    EntityType INTEGER,
    CONSTRAINT FK_ActClass_Catalog FOREIGN KEY (EntityType)
        references Catalog (ID),
    Singleton TINYINT(1) NOT NULL DEFAULT false
);


/*
CREATE TABLE IF NOT EXISTS ActEntity(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ActClassId INTEGER NOT NULL,
    CONSTRAINT FK_ActEntity_ActClass FOREIGN KEY (ActClassId)
        references ActClass (Id),
    EntityId INTEGER NOT NULL,
    CONSTRAINT FK_ActEntity_Entity FOREIGN KEY (EntityId)
        REFERENCES ENTITY (Id),
    Type INTEGER,
    CONSTRAINT FK_ActEntity_Catalog FOREIGN KEY (Type)
        references Catalog (ID)
);
*/


CREATE TABLE IF NOT EXISTS ActClassField(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ActClassId INTEGER NOT NULL,
    CONSTRAINT FK_ActClassField_ActClass FOREIGN KEY (ActClassId)
        references ActClass (Id),
    type INTEGER NOT NULL,
    Name VARCHAR(250)
);



CREATE TABLE IF NOT EXISTS LayoutDefinition (
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ActClassId INTEGER NOT NULL,
    CONSTRAINT FK_LayoutDefinition_ActClass FOREIGN KEY (ActClassId)
        references ActClass (Id),
    TypeId INTEGER NOT NULL,
    xml VARCHAR(4000),
    Valid_To DATETIME
);

CREATE TABLE IF NOT EXISTS Act(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ParentId INTEGER,
    CONSTRAINT FK_Act_ActId FOREIGN KEY (ParentId)
        references Act (Id),
    ActClassId INTEGER NOT NULL,
    CONSTRAINT FK_Act_ActClass FOREIGN KEY (ActClassId)
        references ActClass (Id),
    Date DATE,
    Summary VARCHAR(4000) NOT NULL,
    CreatedByUserId INTEGER NOT NULL,
    CONSTRAINT FK_Act_User1 FOREIGN KEY (CreatedByUserId)
        references User (Id),
    ModifiedByUserId INTEGER NOT NULL,
    CONSTRAINT FK_Act_User2 FOREIGN KEY (ModifiedByUserId)
        references User (Id) 

);

CREATE TABLE IF NOT EXISTS List(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    Type INTEGER NOT NULL
);
 
CREATE TABLE IF NOT EXISTS CatalogListEntry(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    List INTEGER NOT NULL,
    CONSTRAINT FK_CatalogListEntry_List FOREIGN KEY (List)
        references List (ID),
    Catalog INTEGER,
    CONSTRAINT FK_CatalogListEntry_Catalog FOREIGN KEY (Catalog)
        references Catalog (ID)
);


CREATE TABLE IF NOT EXISTS ActField(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ActId INTEGER NOT NULL,
    CONSTRAINT FK_ActField_Act FOREIGN KEY (ActId)
        references Act (Id),
    ClassFieldId INTEGER NOT NULL,
    CONSTRAINT FK_ActField_ActClassField FOREIGN KEY (ClassFieldId)
        references ActClassField (Id),
    CatalogValue INTEGER,
    CONSTRAINT FK_ActField_Catalog FOREIGN KEY (CatalogValue)
        references Catalog (ID),
    IntValue INTEGER,
    StringValue VARCHAR(4000),
    FloatValue REAL,
    DateValue DATETIME,
    ListValue INTEGER,
    CONSTRAINT FK_ActField_List FOREIGN KEY (ListValue)
        references List (ID)
);


CREATE TABLE IF NOT EXISTS Attachment (
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ActId INTEGER NOT NULL,
    CONSTRAINT FK_Attachment_Act FOREIGN KEY (ActId)
        references Act (Id),
    Filename VARCHAR(400) NOT NULL,
    Description VARCHAR(400),
    type INTEGER,
    CONSTRAINT FK_Attachment_CATALOG FOREIGN KEY (type)
        references Catalog (ID),
    DOCID INTEGER NOT NULL
);


CREATE TABLE IF NOT EXISTS LogEntry (
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    UserId INTEGER NOT NULL,
    CONSTRAINT FK_LogEntry_User FOREIGN KEY (UserId)
        references User (Id),
    ActId INTEGER,
    CONSTRAINT FK_LogEntry_Act FOREIGN KEY (ActId)
        references Act (Id),
    EntityId INTEGER,
    CONSTRAINT FK_LogEntry_Entity FOREIGN KEY (EntityId)
        references Entity (Id),
    type INTEGER NOT NULL,
    logDate TIMESTAMP NOT NULL,
    logEntry VARCHAR(4000)
);

CREATE TABLE IF NOT EXISTS Participation(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ActId INTEGER NOT NULL,
    CONSTRAINT FK_Participation_Act FOREIGN KEY (ActId)
        references Act (Id),
    EntityId INTEGER NOT NULL,
    CONSTRAINT FK_Participation_Entity FOREIGN KEY (EntityId)
        references Entity (Id),
    role INTEGER NOT NULL,
    CONSTRAINT FK_Participation_Catalog FOREIGN KEY (role)
        references Catalog (ID),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME
);

CREATE TABLE IF NOT EXISTS Assignment(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    UserId INTEGER,
    CONSTRAINT FK_Assignment_User FOREIGN KEY (UserId)
        references User (Id),
    EntityId INTEGER,
    CONSTRAINT FK_Assignment_Entity FOREIGN KEY (EntityId)
        references Entity (Id),
    role INTEGER NOT NULL,
    CONSTRAINT FK_Assignment_Catalog FOREIGN KEY (role)
        references Catalog (ID),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME
);

CREATE TABLE IF NOT EXISTS Policy (
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    Role INTEGER NOT NULL,
    CONSTRAINT FK_Policy_Catalog_Role FOREIGN KEY (Role)
        references Catalog (Id),
    ObjectType INTEGER NOT NULL,
    CONSTRAINT FK_Policy_Catalog_ObjectType FOREIGN KEY (ObjectType)
        references Catalog (Id),
    Action INTEGER NOT NULL,
    CONSTRAINT FK_Policy_Catalog_Action FOREIGN KEY (Action)
        references Catalog (Id),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME
);


insert into Catalog (id, parent, code, text, shorttext) values (1, null, 'CLDOC', 'CLDOC', 'CLDOC');
insert into Catalog (id, parent, code, text, shorttext) values (2, 1, 'MAIN', 'MAIN', 'MAIN');
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (3, 2, 'Personen', 'Personen', 'Personen', 2);

insert into Catalog (id, parent, code, text, shorttext, logical_order) values (5, 2, 'Configuration', 'Configuration', 'CONFIG', 3);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (21, 5, 'Layouts', 'Layouts', 'Layouts', 1);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (22, 5, 'Kataloge', 'Kataloge', 'Kataloge', 2);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (23, 5, 'Entitaeten', 'Entitaeten', 'Entitaeten', 3);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (24, 5, 'Berechtigungen', 'Berechtigungen', 'Berechtigungen', 4);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (25, 5, 'Einstellungen', 'Einstellungen', 'Einstellungen', 5);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (6, 2, 'Reporting', 'Reporting', 'Reporting', 4);
/*insert into Catalog (id, parent, code, text, shorttext, logical_order) values (10, 2, 'Calendar', 'Calendar', 'Calendar', 5);*/


/*insert into Catalog (id, parent, code, text, shorttext, logical_order) values (30, 2, 'TimeRegistration', 'Anwesenheit', 'Anwesenheit', 6);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (31, 2, 'LeaveRegistration', 'Abwesend', 'Abwesend', 7);
*/insert into Catalog (id, parent, code, text, shorttext, logical_order) values (32, 2, 'TimeSheet', 'Arbeitszeiten', 'Arbeitszeiten', 8);

insert into Catalog (id, parent, code, text, shorttext) values (7, 1, 'PERSONALFILE', 'PERSONALFILE', 'PERSONALFILE');
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (8, 7, 'Formulare', 'Formulare', 'Formulare', 1);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (9, 7, 'Stammdaten', 'Stammdaten', 'Stammdaten', 2);

insert into Catalog (id, parent, code, text, shorttext) values (50, null, 'ROLES', 'ROLES', 'ROLES');
insert into Catalog (id, parent, code, text, shorttext) values (51, 50, 'ADMIN', 'ADMIN', 'ADMIN');
insert into Catalog (id, parent, code, text, shorttext) values (52, 51, 'USER', 'USER', 'USER');
insert into Catalog (id, parent, code, text, shorttext) values (53, 52, 'GUEST', 'GUEST', 'GUEST');
insert into Catalog (id, parent, code, text, shorttext) values (54, 51, 'DEV', 'DEV', 'DEV');
insert into Catalog (id, parent, code, text, shorttext) values (56, 51, 'CONFIGURATOR', 'CONFIGURATOR', 'CONFIGURATOR');

insert into Catalog (id, parent, code, text, shorttext) values (70, null, 'ACTIONS', 'ACTIONS', 'ACTIONS');
insert into Catalog (id, parent, code, text, shorttext) values (71, 70, 'VIEW', 'VIEW', 'VIEW');
insert into Catalog (id, parent, code, text, shorttext) values (72, 70, 'EDIT', 'EDIT', 'EDIT');
insert into Catalog (id, parent, code, text, shorttext) values (73, 70, 'DELETE', 'DELETE', 'DELETE');
insert into Catalog (id, parent, code, text, shorttext) values (74, 70, 'CREATE', 'CREATE', 'CREATE');

insert into Catalog (id, parent, code, text, shorttext) values (101, 50, 'PROTAGONIST', 'PROTAGONIST', 'PROTAGONIST');
insert into Catalog (id, parent, code, text, shorttext) values (102, 50, 'ORGANISATION', 'ORGANISATION', 'ORGANISATION');
insert into Catalog (id, parent, code, text, shorttext) values (103, 50, 'ADMINISTRATOR', 'ADMINISTRATOR', 'ADMINISTRATOR');

insert into Catalog (id, parent, code, text, shorttext) values (13, null, 'BEISPIEL', '', '');
insert into Catalog (id, parent, code, text, shorttext) values (14, 13, 'BEISPIEL1', 'b1', 'b1');
insert into Catalog (id, parent, code, text, shorttext) values (15, 13, 'BEISPIEL2', 'b2', 'b2');
insert into Catalog (id, parent, code, text, shorttext) values (16, 14, 'BEISPIEL1.1', 'b1.1', 'b1.1');
insert into Catalog (id, parent, code, text, shorttext) values (17, 14, 'BEISPIEL1.2', 'b1.2', 'b1.2');
insert into Catalog (id, parent, code, text, shorttext) values (18, 14, 'BEISPIEL1.3', 'b1.3', 'b1.3');
insert into Catalog (id, parent, code, text, shorttext) values (19, 15, 'BEISPIEL2.1', 'b2.1', 'b2.1');
insert into Catalog (id, parent, code, text, shorttext) values (20, 15, 'BEISPIEL2.2', 'b2.2', 'b2.2');

insert into Catalog (id, parent, code, text, shorttext) values (150, null, 'MASTERDATA', 'MASTERDATA', 'MASTERDATA');

insert into Catalog (id, parent, code, text, shorttext) values (151, 150, 'GENDER', 'GENDER', 'GENDER');
insert into Catalog (id, parent, code, text, shorttext) values (152, 151, 'MALE', 'M', 'M');
insert into Catalog (id, parent, code, text, shorttext) values (153, 151, 'FEMALE', 'F', 'F');

insert into Catalog (id, parent, code, text, shorttext) values (154, 150, 'ER', 'EntityRelation', 'E.R.');
insert into Catalog (id, parent, code, text, shorttext) values (155, 154, 'IsMemberOf', 'IsMemberOf', 'IsMemberOf');
insert into Catalog (id, parent, code, text, shorttext) values (156, 154, 'IsLocatedIn', 'IsLocatedIn', 'IsLocatedIn');
insert into Catalog (id, parent, code, text, shorttext) values (157, 154, 'IsPartOf', 'IsPartOf', 'IsPartOf');
insert into Catalog (id, parent, code, text, shorttext) values (158, 154, 'ReportsTo', 'ReportsTo', 'ReportsTo');
insert into Catalog (id, parent, code, text, shorttext) values (159, 154, 'WorksFor', 'WorksFor', 'WorksFor');

insert into Catalog (id, parent, code, text, shorttext) values (160, 150, 'REPORTTYPE', 'REPORTTYPE', 'REPORTTYPE');
insert into Catalog (id, parent, code, text, shorttext) values (161, 160, 'SYSTEM', 'SYSTEM', 'SYSTEM');
insert into Catalog (id, parent, code, text, shorttext) values (162, 160, 'USERDEF', 'USERDEF', 'USERDEF');
/*
insert into Catalog (id, parent, code, text, shorttext) values (170, 150, 'ActClass2EntityRelation', 'ActClass2EntityRelation', 'ActClass2EntityRelation');
insert into Catalog (id, parent, code, text, shorttext) values (171, 170, 'Default', 'Default', 'Default');
insert into Catalog (id, parent, code, text, shorttext) values (172, 170, 'MasterData', 'MasterData', 'MasterData');
insert into Catalog (id, parent, code, text, shorttext) values (173, 170, 'Singleton', 'Singleton', 'Singleton');
*/
insert into Catalog (id, parent, code, text, shorttext) values (180, 150, 'EntityTypes', 'EntityTypes', 'E.T.');
insert into Catalog (id, parent, code, text, shorttext) values (181, 180, 'Person', 'Person', 'Person');
insert into Catalog (id, parent, code, text, shorttext) values (182, 180, 'Organisation', 'Organisation', 'Organisation');

insert into Catalog (id, parent, code, text, shorttext) values (190, 150, 'LEAVETYPES', 'LEAVETYPES', 'LEAVETYPES');
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (191, 190, 'ANNUAL LEAVE', 'Urlaub', 'Urlaub', 1);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (192, 190, 'SICK LEAVE', 'Krankmeldung', 'Krankmeldung', 2);



insert into Entity (ID, TYPE, NAME) values (1, 181, 'Ralph FIERGOLLA');
insert into Person (ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (1, 'Ralph', 'Fiergolla', '1969-08-05');

insert into Entity (ID, TYPE, NAME) values (6, 181, 'Kypros Kyprianou');
insert into Person (ID, FIRSTNAME, LASTNAME) 
values (6, 'Kypros', 'Kyprianou');

insert into Entity (ID,TYPE,NAME) values (20, 182, 'CeRES');
insert into Organisation (Id) values (20);

insert into Entity (ID,TYPE,NAME) values (25, 182, 'C.I.T');
insert into Organisation (Id) values (25);

/* Ralph is member of CERES */
insert into EntityRelation (type, subjectid, objectid, startdate) values (155, 1, 20, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (155, 1, 25, CURRENT_DATE);
insert into EntityRelation (type, subjectid, objectid, startdate) values (155, 6, 25, CURRENT_DATE);

insert into Assignment(entityid, role, startdate) values (1, 51, CURRENT_DATE);

insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (1, 'u', 20);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (1, 'fiergra', 20);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (6, 'kypriky', 25);

insert into Assignment(userid, role, startdate) values ((select id from User where name='fiergra'), 51, CURRENT_DATE);
insert into Assignment(userid, role, startdate) values ((select id from User where name='u'), 54, CURRENT_DATE);

insert into Policy (role, objectType, action, startDate) values (51,3,71, CURRENT_DATE);
/*insert into Policy (role, objectType, action, startDate) values (52,30,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (52,10,71, CURRENT_DATE);*/
insert into Policy (role, objectType, action, startDate) values (56,5,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (52,6,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (52,7,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (52,8,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (52,9,71, CURRENT_DATE);

insert into Policy (role, objectType, action, startDate) values (56,21,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (56,22,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (56,23,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (56,24,71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (56,25,71, CURRENT_DATE);

insert into ActClass (Id,Name) values (301, 'WorkingTime');

/*

insert into ActClass (Id,Name) values (1, 'Beispiel1');
insert into LayoutDefinition(typeid, Actclassid, xml) values (1,1,'<form><line label="label" name="name" type="String"/></form>');

insert into ActClass (Id,Name) values (2, 'BsplDatentypen');
insert into LayoutDefinition(typeid, Actclassid, xml) values (1,2,'<form>
   <line label="Personenauswahl" name="Arzt" type="Humanbeing"/> 
   <line label="Rollen" name="edvler" role="ADMIN" type="Humanbeing"/> 
   <line name="KurzText" type="String"/>
   <line name="Text" type="Text"/>
   <line name="KatalogListe" type="List" parent="BEISPIEL.BEISPIEL2"/>
   <line name="KatalogOptionen" type="Option" parent="BEISPIEL.BEISPIEL1"/>
   <line name="KatalogMulti" type="multiSelect" parent="BEISPIEL.BEISPIEL1"/>
   <line name="TextSeite2" type="Text"/>
   <line name="ActDatum" type="actdate"/>
   <line name="Datum" type="Date"/>
   <line name="Ja/Nein" type="Boolean"/>
</form>');

insert into ActClass (Id,Name) values (3, 'BsplMehrereSeiten');
insert into LayoutDefinition(typeid, Actclassid, xml) values (1,3,'<pages>
   <form label="Seite1">
      <line label="Personenauswahl" name="Arzt" type="Humanbeing"/> 
      <line label="EDV" name="edvler" role="ADMIN" type="Humanbeing"/> 
      <line name="KurzText" type="String"/>
      <line name="Text" type="Text"/>
      <line name="Katalog1" type="List" parent="BEISPIEL.BEISPIEL1"/>
      <line name="Katalog2" type="List" parent="BEISPIEL.BEISPIEL2"/>
      <line name="Katalog3" type="Option" parent="BEISPIEL.BEISPIEL1"/>
   </form>
   <form label="Seite 2">
      <line name="TextSeite2" type="Text"/>
      <line name="Datum" type="Date"/>
      <line name="Ja/Nein" type="Boolean"/>
   </form>
</pages>');

insert into LayoutDefinition(typeid, Actclassid, xml) values (2,3,'<pdf size="A4"><paragraph>Statischer Text.</paragraph><paragraph>Dynamischer Text: {KurzText}</paragraph></pdf>');

insert into ActClass (Id,Name) values (4, 'BsplKataloge');
insert into LayoutDefinition(typeid, Actclassid, xml) values (1,4,'<form>
   <line name="Katalog1" type="List" parent="BEISPIEL.BEISPIEL1"/>
   <line name="Katalog2" type="List" parent="BEISPIEL.BEISPIEL2"/>
   <line name="Katalog3" type="Option" parent="BEISPIEL.BEISPIEL1"/>
</form>');

*/



insert into Catalog (id, parent, code, text, shorttext) values (67, 51, 'Zeiterfassung', 'Zeiterfassung', 'Zeiterfassung');
insert into Catalog (id, parent, code, text, shorttext) values (68, 51, 'ZeitManager', 'ZeitManager', 'ZeitManager');

DROP TABLE IF EXISTS WorkPattern;

CREATE TABLE IF NOT EXISTS WorkPattern(
    Id INTEGER PRIMARY KEY NOT NULL,
    CONSTRAINT FK_WorkPattern_ENTITY FOREIGN KEY (Id)
        REFERENCES ENTITY (Id),
    Pattern VARCHAR(20) NOT NULL,
    LeaveEntitlement INTEGER NOT NULL
);


insert into Catalog (id, parent, code, text, shorttext) values (1001, 180, 'Arbeitszeitmuster', 'Arbeitszeitmuster', 'Arbeitszeitmuster');
insert into Entity (id, TYPE, NAME) values (1101, 1001, 'Vollzeit');
insert into Entity (id, TYPE, NAME) values (1102, 1001, 'Teilzeit50');
insert into Entity (id, TYPE, NAME) values (1103, 1001, 'Teilzeit80');

insert into WorkPattern (id, Pattern, LeaveEntitlement) values (1101, '8-8-8-8-7.5', 30);
insert into WorkPattern (id, Pattern, LeaveEntitlement) values (1102, '8-8-4-0-0', 30);
insert into WorkPattern (id, Pattern, LeaveEntitlement) values (1103, '8-8-8-8-0', 30);

insert into Catalog (parent, code, text, shorttext) values (154, 'arbeitet entsprechend', 'arbeitet entsprechend', 'arbeitet entsprechend');
insert into Catalog (parent, code, text, shorttext) values (154, 'WorksFor', 'WorksFor', 'WorksFor');

/* time user: EDIT */
insert into Policy (role, objectType, action, startDate) values (67,(select id from Catalog where code ='TimeSheet'),71, CURRENT_DATE);
/* time manager: EDIT + VIEW */
insert into Policy (role, objectType, action, startDate) values (68,(select id from Catalog where code ='TimeSheet'),71, CURRENT_DATE);
insert into Policy (role, objectType, action, startDate) values (68,(select id from Catalog where code ='TimeSheet'),72, CURRENT_DATE);
