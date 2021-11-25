DROP DATABASE IF EXISTS relationships_database;
CREATE DATABASE relationships_database;
USE relationships_database;

DROP TABLE IF EXISTS relationships;
CREATE TABLE relationships (
	relationship_id varchar(500) UNIQUE NOT NULL,
	user_id varchar(500) NOT NUll,
	followed_user_id varchar(500) NOT NULL
    );

INSERT INTO relationships (relationship_id, user_id, followed_user_id) values ("jahdfijkah45893wyrt789h35", "00unfsrxoMlovRpMk5d6",'00uqtzt6tMJyaDYyj5d6');