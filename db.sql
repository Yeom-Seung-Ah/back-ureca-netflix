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

CREATE TABLE wishlists (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(50) NOT NULL,
  movie_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_wishlist(user_id, movie_id)
);