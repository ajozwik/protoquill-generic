CREATE TABLE IF NOT EXISTS ADDRESS
(
    ID         INT         NOT NULL AUTO_INCREMENT,
    COUNTRY VARCHAR(50) NOT NULL,
    CITY VARCHAR(50) NOT NULL,
    STREET VARCHAR(50)  DEFAULT NULL,
    BUILDING_NUMBER VARCHAR(10)  DEFAULT NULL,
    LOCAL_NUMBER VARCHAR(10)  DEFAULT NULL,
    `UPDATED` DATETIME,
    `CREATED` DATE  DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS PERSON
(
    ID         INT         NOT NULL,
    FIRST_NAME VARCHAR(50) NOT NULL,
    LAST_NAME  VARCHAR(20) NOT NULL,
    BIRTH_DATE DATE,
    ADDRESS_ID INT DEFAULT NULL,
    CREATE_DATE_TIME DATETIME DEFAULT NULL
);


CREATE TABLE IF NOT EXISTS PERSON2
(
    ID         INT         NOT NULL AUTO_INCREMENT,
    FIRST_NAME VARCHAR(50) NOT NULL,
    LAST_NAME  VARCHAR(20) NOT NULL,
    BIRTH_DATE DATE,
    ADDRESS_ID INT DEFAULT NULL,
    CREATE_DATE_TIME DATETIME DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS PERSON3
(
    ID         INT         NOT NULL AUTO_INCREMENT,
    FIRST_NAME VARCHAR(50) NOT NULL,
    LAST_NAME  VARCHAR(20) NOT NULL,
    DOB DATE,
    ADDRESS_ID INT DEFAULT NULL,
    CREATE_DATE_TIME DATETIME DEFAULT NULL
);


CREATE TABLE IF NOT EXISTS CONFIGURATION (
    `CONFIGURATION_KEY`  VARCHAR(20) NOT NULL,
    `CONFIGURATION_VALUE`  VARCHAR(20) NOT NULL,
    PRIMARY KEY (`CONFIGURATION_KEY`)
);


CREATE TABLE IF NOT EXISTS PRODUCT (
    `ID`  INT NOT NULL AUTO_INCREMENT,
    `NAME`  VARCHAR(20) NOT NULL,
    `CREATED` DATE,
    PRIMARY KEY (`ID`)
);

CREATE TABLE IF NOT EXISTS SALE (
    `PRODUCT_ID`  INT NOT NULL,
    `PERSON_ID`  INT NOT NULL,
    `SALE_DATE` DATETIME,
    `SALE_DATE_TIME` DATETIME,
    `CREATE_DATE` DATE,
    PRIMARY KEY (`PRODUCT_ID`,`PERSON_ID`)
);


CREATE TABLE IF NOT EXISTS CELL4D (
    `X`  INT NOT NULL,
    `Y`  INT NOT NULL,
    `Z`  INT NOT NULL,
    `T`  BIGINT NOT NULL,
    `OCCUPIED` BOOLEAN,
    PRIMARY KEY (`X`, `Y`, `Z`, `T`)
);

