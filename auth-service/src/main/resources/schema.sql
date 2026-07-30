CREATE DATABASE auth_service_db;

USE auth_service_db;

SHOW tables;

SELECT * FROM user;

UPDATE user
SET role = "ADMIN"
WHERE username = "sharvari";