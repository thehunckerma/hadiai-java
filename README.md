# Hadi.ai java/spring boot backend

## Setup & Run:

First create a user in mysql with the credentials **_java:java_** and a database with the name **spring_boot**,  
grant the user **java** permissions on the database "spring_boot".

Connect to mysql database and run the following:

```
create database spring_boot;
CREATE USER 'java'@'localhost' IDENTIFIED BY 'spring_boot';
GRANT ALL PRIVILEGES ON spring_boot . * TO 'java'@'localhost';
use spring_boot;
```

Run:  
`mvn install`  
`mvn spring-boot:run`

A local server will run on localhost:8080

Connect to mysql database and run the following:

```
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

Import the insomnia collection, and start with the signup
then login to get the jwt and use it to send requests to protected routes

Use `mvn spotless:check` to check if any files need formatting, and `mvn spotless:apply` to format all files.

ManyToOne => JsonBackReference
OneToMany => JsonManagedReference
ManyToMany => JsonIdentityInfo
