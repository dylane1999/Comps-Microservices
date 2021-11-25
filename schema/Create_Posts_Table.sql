DROP DATABASE IF EXISTS intellivest_posts_db;
CREATE DATABASE intellivest_posts_db;
USE intellivest_posts_db;

DROP TABLE IF EXISTS posts;
CREATE TABLE posts (
	id varchar(500) NOT NUll UNIQUE,
	poster_id varchar(500) NOT NULL,
    post_title varchar(100) NOT NULL,
	post_content varchar(1000) NOT NULL,
    time_of_post timestamp NOT NULL,
	PRIMARY KEY (id)
    );

INSERT INTO posts (id, poster_id, post_title, post_content, time_of_post) values ('00unfsrxoMlovRpMk5d6','4312iuydbfhjkd4', 'this is the title', 'this is a great post', '2008-10-29 14:56:59');