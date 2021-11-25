DROP DATABASE IF EXISTS intellivest_users_db;
CREATE DATABASE intellivest_users_db;
USE intellivest_users_db;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
	id varchar(500) NOT NUll UNIQUE,
	first_name varchar(200) NOT NULL,
	last_name varchar(200) NOT NULL,
	email varchar(200) NOT NULL,
	PRIMARY KEY (id)
    );

INSERT INTO users (id, first_name, last_name, email) values ('00unfsrxoMlovRpMk5d6','Dylan', 'Edwards', 'dylanedwards290@gmail.com');

