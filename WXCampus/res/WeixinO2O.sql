create database wxcampus;
use wxcampus;

DROP TABLE IF EXISTS `areas`;
CREATE TABLE `areas` (
  `aid` int(10) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) NOT NULL,
  `college` varchar(255) NOT NULL,
  `building` varchar(255) not null,
  `state` tinyint(1) default 0,  
  `startTime` time default "21:00:00",
  `endTime` time default "23:00:00",
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` int(10) NOT NULL AUTO_INCREMENT,
  `tel` int(11) NOT NULL,
  `password` varchar(255) NOT NULL,
  `openid` varchar(255) not null,
  `name` varchar(255) default null,
  `room` varchar(255) default null,
  `location` int(10) DEFAULT 1,
  `itemsStar` varchar(255) DEFAULT "",
  `registerDate` date NOT NULL,
  `registerTime` time NOT NULL,
  PRIMARY KEY (`uid`),
  KEY `location` (`location`),
  CONSTRAINT `user_area_fk1` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `items`;
CREATE TABLE `items` (
  `iid` int(10) NOT NULL AUTO_INCREMENT,
  `iname` varchar(255) NOT NULL,
  `icon` varchar(255) NOT NULL,
  `originPrice` decimal(10,2) not null,
  `realPrice` decimal(10,2) not null,
  `category` varchar(20) not null,
  `restNum` int(5) default 0,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`iid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `items_on_sale`;
CREATE TABLE `items_on_sale` (
  `iosid` int(10) NOT NULL AUTO_INCREMENT,
  `iid` int(10) NOT NULL,
  `restNum` int(5) not null,
  `location` int(10) not null,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`iosid`),
  KEY `iid` (`iid`),
  KEY `location` (`location`),
  CONSTRAINT `ios_items_fk1` FOREIGN KEY (`iid`) REFERENCES `items` (`iid`) on delete cascade on update cascade,
  CONSTRAINT `ios_items_fk2` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`) on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `advertisement`;
CREATE TABLE `advertisement` (
  `astid` int(10) NOT NULL AUTO_INCREMENT,
  `img` varchar(255) NOT NULL,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`astid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `managers`;
CREATE TABLE `managers` (
  `mid` int(10) NOT NULL AUTO_INCREMENT,
  `ring` int(1) NOT NULL,
  `tel`  int(11) not null,
  `name` varchar(255) not null,
  `password` varchar(255) not null,
  `location` int(10) not null,
  `say` varchar(255) default "",
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`mid`),
  KEY `location` (`location`),
  CONSTRAINT `manager_area_fk1` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `trades`;
CREATE TABLE `trades` (
  `tid` int(10) NOT NULL AUTO_INCREMENT,
  `rid` int(10) not null,
  `customer` int(10) NOT NULL,
  `seller` int(10) NOT NULL,
  `location` int(10) NOT NULL,
  `item` int(10) NOT NULL,
  `price` decimal(10,2) not null,
  `orderNum` int(5) not null,
  `state` int(1) not null,
  `addedDate` date not null,
  `addedTime` time not null,
  `finishedDate` date default NULL,
  `finishedTime` time default NULL,
  PRIMARY KEY (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `coupons`;
CREATE TABLE `coupons` (
  `cid` int(10) NOT NULL AUTO_INCREMENT,
  `money` decimal(10,2) NOT NULL,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `coupons_user`;
CREATE TABLE `coupons_user` (
  `cuid` int(10) NOT NULL AUTO_INCREMENT,
  `cid` int(10) NOT NULL,
  `owner` int(10) not null,
  `used` tinyint(1) default 0,
  `endDate` date not null,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`cuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `coupons_use`;
CREATE TABLE `coupons_use` (
  `cuid` int(10) NOT NULL AUTO_INCREMENT,
  `cid` int(10) NOT NULL,
  `rid` int(10) not null,
  `realpay` decimal(10,2) not null,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`cuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `advices`;
CREATE TABLE `advices` (
  `aid` int(10) NOT NULL AUTO_INCREMENT,
  `uid` int(10) NOT NULL,
  `content` text not null,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

select * from user;

select * from areas;

select * from managers;

INSERT INTO `wxcampus`.`managers` (`mid`, `ring`, `tel`, `name`, `password`, `location`, `say`, `addedDate`, `addedTime`) VALUES ('1', '1', '123', '测试', '123456', '1', '店长说', '2015-11-11', '00:00:00');

INSERT INTO `wxcampus`.`areas` (`aid`, `city`, `college`, `building`, `state`, `startTime`, `endTime`, `addedDate`, `addedTime`) VALUES ('1', '济南', '山东大学', '2号楼', '0', '21:00:00', '23:00:00', '2015-11-11', '00:00:00');

INSERT INTO `wxcampus`.`user` (`uid`, `tel`, `password`, `openid`, `name`, `room`, `location`, `itemsStar`, `registerDate`, `registerTime`) VALUES ('1', '123', '123456', '1111', '测试', '528', '1', '“”', '2015-11-11', '00:00:00');