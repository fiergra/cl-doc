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
DROP TABLE IF EXISTS Catalog;

CREATE TABLE IF NOT EXISTS Entity(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    type INTEGER NOT NULL,
    NAME VARCHAR(400)
);

ALTER TABLE Entity AUTO_INCREMENT = 10000;

CREATE TABLE IF NOT EXISTS Address(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ENTITY_ID INTEGER NOT NULL,
    CONSTRAINT FK_ADDRESS_ENTITY FOREIGN KEY (ENTITY_ID)
        REFERENCES ENTITY (Id),
    STREET VARCHAR(400),
    NUMBER VARCHAR(400),
    CITY VARCHAR(400),
    POSTCODE VARCHAR(400),
    CO VARCHAR(400)
);

CREATE TABLE IF NOT EXISTS Catalog (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    Parent INTEGER,
    CONSTRAINT FK_CATALOG_CATALOG FOREIGN KEY (Parent)
        references Catalog (ID),
    CODE VARCHAR(64),
    CONSTRAINT UK_CATALOG_CODE UNIQUE KEY (PARENT,CODE),
    TEXT VARCHAR(400),
    SHORTTEXT VARCHAR(400),
    Date TIMESTAMP NOT NULL,
    LOGICAL_ORDER INTEGER 
    );

ALTER TABLE Catalog AUTO_INCREMENT = 10000;



CREATE TABLE IF NOT EXISTS Person(
    Id INTEGER PRIMARY KEY NOT NULL,
    CONSTRAINT FK_PERSON_ENTITY FOREIGN KEY (Id)
        REFERENCES ENTITY (Id),
    PER_ID INTEGER NOT NULL,
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
    Name VARCHAR(250) UNIQUE
);

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
    Date DATETIME,
    Summary VARCHAR(4000)
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
    RealValue REAL,
    DateValue DATETIME,
    BlobValue LONGBLOB
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
    EntityId INTEGER NOT NULL,
    CONSTRAINT FK_Assignment_Entity FOREIGN KEY (EntityId)
        references Entity (Id),
    role INTEGER NOT NULL,
    CONSTRAINT FK_Assignment_Catalog FOREIGN KEY (role)
        references Catalog (ID),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME
);


insert into Entity (ID, TYPE) values (1, 1);
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (1, 36762, 'Ralph', 'Fiergolla', '1969-08-05');

insert into Entity (ID, TYPE) values (2, 1);
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (2, 12345, 'Christian', 'Laschek', '1978-07-07');

insert into Entity (ID, TYPE) values (3, 1);
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (3, 45678, 'Bernd', 'Krieger', '1968-07-07');

insert into Entity (ID, TYPE) values (4, 1);
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (4, 45678, 'Carlita', 'Metzdorf-Klos', '1962-07-07');

insert into Entity (ID,TYPE,NAME) values (20, 39, 'CeRES');
insert into Organisation (Id) values (20);

insert into Entity (ID,TYPE,NAME) values (21, 39, 'Krebsgesellschaft-Rheinland-Pfalz');
insert into Organisation (Id) values (21);

insert into Entity (ID,TYPE,NAME) values (22, 39, 'DRK Sozialwerk');
insert into Organisation (Id) values (22);

insert into Entity (ID, TYPE,NAME) values (31, 40, 'JMO B2/097');
insert into Room (id) values (31);


insert into Catalog (id, parent, code, text, shorttext) values (1, null, 'CLDOC', 'CLDOC', 'CLDOC');
insert into Catalog (id, parent, code, text, shorttext) values (2, 1, 'MAIN', 'MAIN', 'MAIN');
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (3, 2, 'HOME', 'HOME', 'HOME', 1);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (4, 2, 'CONFIG', 'CONFIG', 'CONFIG', 2);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (5, 2, 'DEBUG', 'DEBUG', 'DEBUG', 3);
insert into Catalog (id, parent, code, text, shorttext) values (6, 1, 'PERSONALFILE', 'PERSONALFILE', 'PERSONALFILE');
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (7, 6, 'HISTORY', 'HISTORY', 'HISTORY', 1);
insert into Catalog (id, parent, code, text, shorttext, logical_order) values (8, 6, 'DETAILS', 'DETAILS', 'DETAILS', 2);

insert into Catalog (id, parent, code, text, shorttext) values (9, null, 'ROLES', 'ROLES', 'ROLES');
insert into Catalog (id, parent, code, text, shorttext) values (10, 9, 'ADMIN', 'ADMIN', 'ADMIN');
insert into Catalog (id, parent, code, text, shorttext) values (11, 9, 'USER', 'USER', 'USER');
insert into Catalog (id, parent, code, text, shorttext) values (12, 9, 'GUEST', 'GUEST', 'GUEST');

insert into Catalog (id, parent, code, text, shorttext) values (101, 9, 'PATIENT', 'PATIENT', 'PATIENT');

insert into Catalog (id, parent, code, text, shorttext) values (13, null, 'BEISPIEL', '', '');
insert into Catalog (id, parent, code, text, shorttext) values (14, 13, 'BEISPIEL1', 'b1', 'b1');
insert into Catalog (id, parent, code, text, shorttext) values (15, 13, 'BEISPIEL2', 'b2', 'b2');
insert into Catalog (id, parent, code, text, shorttext) values (16, 14, 'BEISPIEL1.1', 'b1.1', 'b1.1');
insert into Catalog (id, parent, code, text, shorttext) values (17, 14, 'BEISPIEL1.2', 'b1.2', 'b1.2');
insert into Catalog (id, parent, code, text, shorttext) values (18, 14, 'BEISPIEL1.3', 'b1.3', 'b1.3');
insert into Catalog (id, parent, code, text, shorttext) values (19, 15, 'BEISPIEL2.1', 'b2.1', 'b2.1');
insert into Catalog (id, parent, code, text, shorttext) values (20, 15, 'BEISPIEL2.2', 'b2.2', 'b2.2');

insert into Catalog (id, parent, code, text, shorttext) values (50, null, 'MASTERDATA', 'MASTERDATA', 'MASTERDATA');
insert into Catalog (id, parent, code, text, shorttext) values (51, 50, 'GENDER', 'GENDER', 'GENDER');
insert into Catalog (id, parent, code, text, shorttext) values (52, 51, 'MALE', 'M', 'M');
insert into Catalog (id, parent, code, text, shorttext) values (53, 51, 'FEMALE', 'F', 'F');


insert into Assignment(entityid, role, startdate) values (1, 10, CURRENT_DATE);
insert into Assignment(entityid, role, startdate) values (2, 10, CURRENT_DATE);

insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (2, 'laschek', 21);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (1, 'fiergra', 20);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (3, 'krieger', 22);
insert into User (PERSON_ID, NAME, ORGANISATION_ID) values (4, 'carlita', 21);

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
   <line name="TextSeite2" type="Text"/>
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

