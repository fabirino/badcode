# DataBase

### Connect to database
```shell
psql -h localhost -p 5432 -d postgres -U postgres
```
### Create User
``` shell
create user postgres with encrypted password 'postgres';
```
### Create Database
``` shell
create database badcode;
```
### Give Privileges to user
``` shell
grant all privileges on database badcode to postgres;
```
### Run Sql files
```txt
1. init.sql
```
