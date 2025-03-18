CREATE TABLE users (
    userId VARCHAR(255) PRIMARY KEY, 
    userName VARCHAR(100) NOT NULL,
    userPwd VARCHAR(255) NOT NULL
);

CREATE TABLE `netflix_ureca`.`login` (
  `userId` VARCHAR(50) NOT NULL primary key,
  `token` VARCHAR(256) NOT NULL unique,
  `loginTime` TIMESTAMP NOT NULL DEFAULT current_timestamp);
  
  

create table saltInfo(
userId varchar(50) primary key,
salt varchar(256) );