# Hadi.ai java/spring boot backend

## Setup & Run:

First create a user in mysql with the credentials **_java:java_** and a database with the name **spring_boot**,  
grant the user **java** permissions on the database "spring_boot".

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