mysql -u root --password=a -h localhost emt_db < c:\apache\htdocs\embesystems\emt\zip\geove_0_1.sql

mysql -u blofib_emt_db --password=VtywJo0hP5CRnHHr -h embesystems.com blofib_emt_db < c:\apache\htdocs\embesystems\emt\zip\geove_0_1.sql;

mysql -u blofib_emt_db --password=VtywJo0hP5CRnHHr -h localhost
GRANT ALL PRIVILEGES ON blofib_emt_db.* TO blofib_emt_db@% IDENTIFIED BY 'VtywJo0hP5CRnHHr';
GRANT ALL PRIVILEGES ON blofib_emt_db.* TO blofib_emt_db@localhost IDENTIFIED BY 'VtywJo0hP5CRnHHr';


mysql -u blofib_ve_db --password=RPDt4d9FZy -h localhost
mysql -u root --password=a -h localhost
mysql> use mysql;
mysql> update user set password=PASSWORD("NEW-ROOT-PASSWORD") where User='root';
mysql> flush privileges;
mysql> quit

"c:\Program Files\MariaDB 11.3\bin\mysql" -u root -h localhost emt_db



http://localhost:82/embesystems/emt/pt_i.php?verbose=1&ref_releve=24001&longitude=6.1844&latitude=48.6921&vitessegps=25&directiongps=0
http://192.168.1.17:82/embesystems/emt/pt_i.php?verbose=1&ref_releve=24001&longitude=6.18664&latitude=48.7921&vitessegps=85&directiongps=0&laps=3

INSERT INTO points(REF_RELEVE,LONGITUDE,LATITUDE,ALTITUDE,VITESSEGPS,DIRECTIONGPS,NBRSATGPS,DTHGPS,VITESSEMOY,COOLANT_TEMPERATURE,MAF,ENGINE_LOAD,RPM) VALUES (24001,6.1844,48.6921,0,25,0,0,0000-00-00,0,0,0,0,0);

mysqldump -u root --password= -h localhost -R emt_db > c:\apache\htdocs\embesystems\emt\zip\save.sql

http://192.168.1.17:82/embesystems/emt/pt_i.php?verbose=1
          &ref_releve=24001
          &temps=null
          &latitude=48.7921
          &longitude=48.7921
          &altitude=48.7921
          &vitessegps=85
          &directiongps=0
          &energie=150
          &tension=10
          &intensite=15
          &laps=3
          &nbsatgps=49

http://192.168.1.17:82/embesystems/emt/pt_i.php?verbose=1&ref_releve=24001&temps=null&latitude=48.7921&longitude=48.7921&altitude=48.7921&vitessegps=85&directiongps=0&energie=150&tension=10&intensite=15&laps=3&nbsatgps=49

REF_RELEVE,TEMPS,VITESSEGPS,VITESSEMOY,INTENSITE,TENSION,ENERGIE,LATITUDE,LONGITUDE,ALTITUDE,DISTANCE,LAPS,DIRECTIONGPS,NBRSATGPS)

INSERT INTO `points` VALUES (11696, 22009, '35m48', 0.0, 13.688, 0.01, 1.56, 95302.6, 48.30084417, 6.91856566, 382, 1937.51, 5, 0, 0);


*****************************************************************************************************************************************************************************************************************************
/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50096
Source Host           : localhost:3306
Source Database       : latvho

Target Server Type    : MYSQL
Target Server Version : 50096
File Encoding         : 65001

Date: 2012-06-22 16:11:26
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `logs`
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs` (
                        `ID` int(11) NOT NULL auto_increment,
                        `DTH` datetime NOT NULL default '0000-00-00 00:00:00',
                        `MSG` varchar(200) default '',
                        PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of logs
-- ----------------------------
-- INSERT INTO `logs` VALUES ('1', '2012-06-22 10:11:55', 'ID=4, NOM=demo');

-- ----------------------------
-- Table structure for `points`
-- ----------------------------
DROP TABLE IF EXISTS `points`;
CREATE TABLE `points` (
                          `ID` int(11) NOT NULL auto_increment,
                          `REF_RELEVE` int(11) default '0',
                          `LONGITUDE` float default '0',
                          `LATITUDE` float default '0',
                          `ALTITUDE` float default '0',
                          `VITESSEGPS` float default '0',
                          `DIRECTIONGPS` float default '0',
                          `NBRSATGPS` float default '0',
                          `DTHGPS` datetime default '0000-00-00 00:00:00',
                          `VITESSEMOY` float default '0',
                          `CONSOMOY` float default '0',
                          `COOLANT_TEMPERATURE` float default '0',
                          `MAP` float default '0',
                          `THROTTLE` float default '0',
                          `GEAR` int(11) default '0',
                          `RPM` float default '0',
                          `ENGINE_LOAD` float default '0',
                          `MAF` float default '0',
                          PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=4828 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of points
-- ----------------------------

-- ----------------------------
-- Table structure for `releves`
-- ----------------------------
DROP TABLE IF EXISTS `releves`;
CREATE TABLE `releves` (
                           `ID` int(11) NOT NULL auto_increment,
                           `REF` int(50) NOT NULL default '0',
                           `DTH_DEBUT` datetime default '0000-00-00 00:00:00',
                           `DTH_FIN` datetime default '0000-00-00 00:00:00',
                           `ID_VEHICULE` int(11) default '0',
                           `ID_USER` int(11) default '0',
                           PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of releves
-- ----------------------------
INSERT INTO `releves` VALUES ('1', '12002', '2012-03-08 06:51:20', '0000-00-00 00:00:00', '1', '2');
INSERT INTO `releves` VALUES ('2', '12006', '2012-03-08 20:22:29', '0000-00-00 00:00:00', '1', '2');
INSERT INTO `releves` VALUES ('3', '12007', '2012-03-08 18:30:22', '0000-00-00 00:00:00', '1', '2');
INSERT INTO `releves` VALUES ('4', '12009', '2012-03-10 10:45:30', '0000-00-00 00:00:00', '1', '2');
INSERT INTO `releves` VALUES ('5', '12011', '2012-03-11 09:38:43', '0000-00-00 00:00:00', '1', '2');
INSERT INTO `releves` VALUES ('6', '12013', '2012-03-11 15:27:30', '0000-00-00 00:00:00', '1', '2');
INSERT INTO `releves` VALUES ('7', '12023', '2012-06-21 09:36:41', '0000-00-00 00:00:00', '3', '2');
INSERT INTO `releves` VALUES ('8', '12020', '2012-06-20 18:59:13', '0000-00-00 00:00:00', '2', '2');
INSERT INTO `releves` VALUES ('9', '12024', '2012-06-22 11:46:31', '0000-00-00 00:00:00', '4', '2');

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `ID` int(11) NOT NULL auto_increment,
                         `NAME` varchar(64) NOT NULL default '',
                         `PW` varchar(128) NOT NULL default '',
                         `STATUS` int(3) NOT NULL default '0',
                         `LOGTIME` datetime default NULL,
                         PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', 'admin', 'admin1', '1', null);
INSERT INTO `users` VALUES ('2', 'demo', 'demoemt', '3', null);


-- ----------------------------
-- Table structure for `vehicules`
-- ----------------------------
DROP TABLE IF EXISTS `vehicules`;
CREATE TABLE `vehicules` (
                             `ID` int(11) NOT NULL auto_increment,
                             `MARQUE` varchar(50) NOT NULL default '',
                             `MODELE` varchar(50) NOT NULL default '',
                             `TYPE` varchar(20) default '',
                             `MATRICULE` varchar(20) default '',
                             `NOM_CONDUCTEUR` varchar(64) default '',
                             `PF` int(11) default '0',
                             `PKW` float default '0',
                             PRIMARY KEY  (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of vehicules
-- ----------------------------
INSERT INTO `vehicules` VALUES ('1', 'Volkwagen', 'Touran', 'VP', '7035-109-13', 'Baghli', '5', '77');
INSERT INTO `vehicules` VALUES ('2', 'PolyWatt', 'EMT', 'VP', '', 'Yacine', '5', '0');
