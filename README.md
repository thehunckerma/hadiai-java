# Hadi.ai java/spring boot backend

## Setup & Run:

First create a user in mysql with the credentials **_java:littleknot_** and a database with the name **spring_boot**,  
grant the user **java** permissions on the database "spring_boot".

Run:  
`mvn install`  
`mvn spring-boot:run`

A local server will run on localhost:8080  
You can login with **user** and the generated password in the console (i.e `Using generated security password: c197110a-4cd1-4b15-91c5-ace7191d5f23`)  
Hit localhost:8080/hello for the hello world example

Connect to mysql database and run the following:

```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```
