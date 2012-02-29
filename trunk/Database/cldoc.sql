DROP TABLE IF EXISTS Assignment;
DROP TABLE IF EXISTS Layoutdefinition;
DROP TABLE IF EXISTS Participation;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Person;
DROP TABLE IF EXISTS Organisation;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS Address;
DROP TABLE IF EXISTS ENTITY;
DROP TABLE IF EXISTS ItemField;
DROP TABLE IF EXISTS Catalog;
DROP TABLE IF EXISTS Item;
DROP TABLE IF EXISTS ItemClassField;
DROP TABLE IF EXISTS ItemClass;


CREATE TABLE IF NOT EXISTS Entity(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    type INTEGER NOT NULL,
    NAME VARCHAR(400)
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
    CO VARCHAR(400)
);

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
    SNDX_FIRSTNAME VARCHAR(400),
    SNDX_LASTNAME VARCHAR(400)
);

CREATE TABLE IF NOT EXISTS User(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    PERSON_ID INTEGER NOT NULL,
    CONSTRAINT FK_USER_PERSON FOREIGN KEY (PERSON_ID)
        REFERENCES PERSON (Id),
    NAME VARCHAR(400),
    HASH VARCHAR(400)
);

CREATE TABLE IF NOT EXISTS Organisation(
    Id INTEGER PRIMARY KEY NOT NULL,
    CONSTRAINT FK_Organisation_ENTITY FOREIGN KEY (Id)
        REFERENCES ENTITY (Id),
    ORN_ID INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS Room(
    Id INTEGER PRIMARY KEY NOT NULL,
    CONSTRAINT FK_Room_ENTITY FOREIGN KEY (Id)
        REFERENCES ENTITY (Id)
);


CREATE TABLE IF NOT EXISTS ItemClass(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(250) UNIQUE
);

CREATE TABLE IF NOT EXISTS ItemClassField(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ItemClassId INTEGER NOT NULL,
    CONSTRAINT FK_ItemClassField_ItemClass FOREIGN KEY (ItemClassId)
        references ItemClass (Id),
    type INTEGER NOT NULL,
    Name VARCHAR(250)
);

CREATE TABLE IF NOT EXISTS LayoutDefinition (
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ItemClassId INTEGER NOT NULL,
    CONSTRAINT FK_LayoutDefinition_ItemClass FOREIGN KEY (ItemClassId)
        references ItemClass (Id),
    TypeId INTEGER NOT NULL,
    xml VARCHAR(4000),
    Valid_To DATETIME
);

CREATE TABLE IF NOT EXISTS Item(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ParentId INTEGER,
    CONSTRAINT FK_Item_ItemId FOREIGN KEY (ParentId)
        references Item (Id),
    ItemClassId INTEGER NOT NULL,
    CONSTRAINT FK_Item_ItemClass FOREIGN KEY (ItemClassId)
        references ItemClass (Id),
    Date DATETIME,
    Summary VARCHAR(4000)
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

CREATE TABLE IF NOT EXISTS ItemField(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ItemId INTEGER NOT NULL,
    CONSTRAINT FK_ItemField_Item FOREIGN KEY (ItemId)
        references Item (Id),
    ClassFieldId INTEGER NOT NULL,
    CONSTRAINT FK_ItemField_ItemClassField FOREIGN KEY (ClassFieldId)
        references ItemClassField (Id),
    CatalogValue INTEGER,
    CONSTRAINT FK_ItemField_Catalog FOREIGN KEY (CatalogValue)
        references Catalog (ID),
    IntValue INTEGER,
    StringValue VARCHAR(4000),
    RealValue REAL,
    DateValue DATETIME,
    BlobValue LONGBLOB
);


CREATE TABLE IF NOT EXISTS Participation(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    ItemId INTEGER NOT NULL,
    CONSTRAINT FK_Participation_Item FOREIGN KEY (ItemId)
        references Item (Id),
    EntityId INTEGER NOT NULL,
    CONSTRAINT FK_Participation_Entity FOREIGN KEY (EntityId)
        references Entity (Id),
    type INTEGER NOT NULL,
    StartDate DATETIME NOT NULL,
    EndDate DATETIME
);

CREATE TABLE IF NOT EXISTS Assignment(
    Id INTEGER PRIMARY KEY AUTO_INCREMENT,
    EntityId INTEGER NOT NULL,
    CONSTRAINT FK_Assignment_Entity FOREIGN KEY (EntityId)
        references Entity (Id),
    type INTEGER NOT NULL,
    CONSTRAINT FK_Assignment_Catalog FOREIGN KEY (type)
        references Catalog (ID),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME
);


insert into Entity (TYPE) values (1);
insert into Person (ID, PER_ID, FIRSTNAME, LASTNAME, DATEOFBIRTH) 
values (1, 36762, 'Ralph', 'FIERGOLLA', '1969-08-05');

insert into Entity (TYPE,NAME) values (2, 'COMMISSION');
insert into Organisation (Id,ORN_ID) values ((select max(id) from entity),39);

insert into Entity (TYPE,NAME) values (3, 'JMO B2/097');
insert into Room (id) values ((select max(id) from entity));

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

insert into Catalog (id, parent, code, text, shorttext) values (13, null, 'BEISPIEL', '', '');
insert into Catalog (id, parent, code, text, shorttext) values (14, 13, 'BEISPIEL1', 'b1', 'b1');
insert into Catalog (id, parent, code, text, shorttext) values (15, 13, 'BEISPIEL2', 'b2', 'b2');
insert into Catalog (id, parent, code, text, shorttext) values (16, 14, 'BEISPIEL1.1', 'b1.1', 'b1.1');
insert into Catalog (id, parent, code, text, shorttext) values (17, 14, 'BEISPIEL1.2', 'b1.2', 'b1.2');
insert into Catalog (id, parent, code, text, shorttext) values (18, 14, 'BEISPIEL1.3', 'b1.3', 'b1.3');
insert into Catalog (id, parent, code, text, shorttext) values (19, 15, 'BEISPIEL2.1', 'b2.1', 'b2.1');
insert into Catalog (id, parent, code, text, shorttext) values (20, 15, 'BEISPIEL2.2', 'b2.2', 'b2.2');


insert into Assignment(entityid, type, startdate) values (1, 10, CURRENT_DATE);
insert into User (PERSON_ID, NAME) values (1, 'u');

insert into ItemClass (Id,Name) values (1, 'Beispiel1');
insert into LayoutDefinition(typeid, itemclassid, xml) values (1,1,'<form><line label="label" name="name" type="String"/></form>');

insert into ItemClass (Id,Name) values (2, 'Beispiel2');
insert into LayoutDefinition(typeid, itemclassid, xml) values (1,2,'<form>
   <line label="Personenauswahl" name="Arzt" type="Humanbeing"/> 
   <line name="KurzText" type="String"/>
   <line name="Text" type="Text"/>
   <line name="Katalog1" type="Catalog" parent="BEISPIEL.BEISPIEL1"/>
   <line name="Katalog2" type="Catalog" parent="BEISPIEL.BEISPIEL2"/>
</form>');

