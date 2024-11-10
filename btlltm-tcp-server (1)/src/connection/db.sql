/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author: nddmanh
 * Created: Oct 18, 2021
 */

CREATE DATABASE  IF NOT EXISTS `btlltm`;

USE btlltm;

CREATE TABLE `btlltm`.`users` (
    `userId` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(45) NOT NULL,
    `password` VARCHAR(45) NOT NULL,
    `score` FLOAT NOT NULL,
    `win` INT NOT NULL,
    `draw` INT NOT NULL,
    `lose` INT NOT NULL,
    `avgCompetitor` FLOAT NOT NULL,
    `avgTime` FLOAT NOT NULL,
    PRIMARY KEY (`userId`));
    
CREATE TABLE match_history (
    matchId INT AUTO_INCREMENT PRIMARY KEY,
    player1Id INT,
    player2Id INT,
    player1Name VARCHAR(255),
    player2Name VARCHAR(255),
    player1Score FLOAT,
    player2Score FLOAT,
    dateTime DATETIME,
    FOREIGN KEY (player1Id) REFERENCES users(userId),
    FOREIGN KEY (player2Id) REFERENCES users(userId)
);
