-- Create Tables
DROP TABLE IF EXISTS users;

CREATE TABLE users (
	id SERIAL,
	username VARCHAR(512) UNIQUE NOT NULL,
	password VARCHAR(512) NOT NULL,
	PRIMARY KEY(id)
);

select * from users;
