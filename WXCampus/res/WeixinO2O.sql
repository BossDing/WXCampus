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
  `startPrice` decimal(10,2) default 10.00,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
insert into areas(aid,city,college,building,addedDate,addedTime) values(0,"","","","2015-11-11","00:00:00");
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `uid` int(10) NOT NULL AUTO_INCREMENT,
  `tel` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `openid` varchar(255) not null,
  `headicon` varchar(255) not null,
  `name` varchar(255) default null,
  `room` varchar(255) default null,
  `location` int(10) DEFAULT 1,
  `itemsStar` varchar(255) DEFAULT "",
  `registerDate` date NOT NULL,
  `registerTime` time NOT NULL,
  PRIMARY KEY (`uid`),
  KEY `location` (`location`),
  CONSTRAINT `user_area_fk1` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`)  on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `items`;
CREATE TABLE `items` (
  `iid` int(10) NOT NULL AUTO_INCREMENT,
  `iname` varchar(255) NOT NULL,
  `icon` varchar(255) NOT NULL,
  `originPrice` decimal(10,2) default null,
  `realPrice` decimal(10,2) not null,
  `cost` decimal(10,2) not null,
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
  `minPrice` decimal(10,2) not null,
  `maxPrice` decimal(10,2) not null,
  `price` decimal(10,2) not null,
  `restNum` int(5) not null,
  `location` int(10) not null,
  `isonsale` tinyint(1) not null,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`iosid`),
  KEY `iid` (`iid`),
  KEY `location` (`location`),
  CONSTRAINT `ios_items_fk1` FOREIGN KEY (`iid`) REFERENCES `items` (`iid`) on delete cascade on update cascade,
  CONSTRAINT `ios_items_fk2` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`) on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `settings`;
CREATE TABLE `settings` (
  `sid` int(10) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) NOT NULL,
  `value` int(10) NOT NULL,
  `addedDT` timestamp not null,
  PRIMARY KEY (`sid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
--insert into settings(`key`,`value`,`addedDT`) values("promotionShowNum",3,"2015-11-25 00:00:00");

DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion` (
  `pid` int(10) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `location` int(10) default 0,
  `isshow` tinyint(1) not null,
  `addedDT` timestamp not null,
  PRIMARY KEY (`pid`),
  KEY `location` (`location`),
  CONSTRAINT `pro_area_fk1` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`)  on delete cascade on update cascade
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
  `tel`  varchar(255) not null,
  `name` varchar(255) not null,
  `password` varchar(255) not null,
  `location` int(10) not null,
  `shopname` varchar(255) default "",
  `shopimg` varchar(255) default "",
  `say` varchar(255) default "",
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`mid`),
  KEY `location` (`location`),
  CONSTRAINT `mana_area_fk1` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`)  on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
insert into managers(ring,tel,name,password,location,addedDate,addedTime) values(0,"","管理员","admin666",0,"2015-11-25","00:00:00");

DROP TABLE IF EXISTS `informs`;
CREATE TABLE `informs` (
  `iid` int(10) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) NOT NULL,
  `tos` int(10) not null,
  `addedDT` timestamp not null,
  PRIMARY KEY (`iid`),
   KEY `tos` (`tos`),
  CONSTRAINT `inform_area_fk1` FOREIGN KEY (`tos`) REFERENCES `managers` (`mid`)  on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `incomes`;
CREATE TABLE `incomes` (
  `iid` int(10) NOT NULL AUTO_INCREMENT,
  `mid` int(10) NOT NULL,
  `sales` decimal(10,2) not null,
  `addedDT` timestamp not null,
  PRIMARY KEY (`iid`),
    KEY `mid` (`mid`),
  CONSTRAINT `income_mana_fk1` FOREIGN KEY (`mid`) REFERENCES `managers` (`mid`)  on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
insert into incomes(mid,sales,addedDT) values(1,0.00,"2015-11-25 00:00:00");

DROP TABLE IF EXISTS `ingoods`;
CREATE TABLE `ingoods` (
  `iid` int(10) NOT NULL AUTO_INCREMENT,
  `rid` int(10) not null,
  `froms` int(10) NOT NULL,
  `tos` int(10) not null,
  `item` int(10) not null,
  `num` int(5) not null,
  `state` int(1) not null,
  `addedDT` timestamp not null,
  PRIMARY KEY (`iid`),
   KEY `froms` (`froms`),
   KEY `tos` (`tos`),
   KEY `item` (`item`),
  CONSTRAINT `ingood_fk1` FOREIGN KEY (`froms`) REFERENCES `managers` (`mid`)  on delete cascade on update cascade,
  CONSTRAINT `ingood_fk2` FOREIGN KEY (`tos`) REFERENCES `managers` (`mid`)  on delete cascade on update cascade,
  CONSTRAINT `ingood_fk3` FOREIGN KEY (`item`) REFERENCES `items` (`iid`)  on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `trades`;
CREATE TABLE `trades` (
  `tid` int(10) NOT NULL AUTO_INCREMENT,
  `rid` int(10) not null,
  `tradeNo` varchar(255) not null,
  `totalmoney` decimal(10,2) not null,
  `wxtradeNo` varchar(255) default null,
  `customer` int(10) NOT NULL,
  `seller` int(10) NOT NULL,
  `location` int(10) NOT NULL,
  `item` int(10) NOT NULL,
  `price` decimal(10,2) not null,
  `orderNum` int(5) not null,
  `room` varchar(127) not null,
  `state` int(1) not null,
  `addedDate` date not null,
  `addedTime` time not null,
  `finishedTimeStamp` varchar(32) default NULL,
  PRIMARY KEY (`tid`),
   KEY `customer` (`customer`),
   KEY `seller` (`seller`),
   KEY `location` (`location`),
   KEY `item` (`item`),
  CONSTRAINT `trade_fk1` FOREIGN KEY (`customer`) REFERENCES `user` (`uid`)  on delete cascade on update cascade,
  CONSTRAINT `trade_fk2` FOREIGN KEY (`seller`) REFERENCES `managers` (`mid`)  on delete cascade on update cascade,
  CONSTRAINT `trade_fk3` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`)  on delete cascade on update cascade,
  CONSTRAINT `trade_fk4` FOREIGN KEY (`item`) REFERENCES `items` (`iid`)  on delete cascade on update cascade
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
  `type` varchar(32) not null,
  `content` text not null,
  `location` int(10) not null,
  `addedDate` date NOT NULL,
  `addedTime` time NOT NULL,
  PRIMARY KEY (`aid`),
    KEY `uid` (`uid`),
   KEY `location` (`location`),
  CONSTRAINT `advice_fk1` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)  on delete cascade on update cascade,
  CONSTRAINT `advice_fk2` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`)  on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `areasales`;
CREATE TABLE `areasales` (
  `asid` int(10) NOT NULL AUTO_INCREMENT,
  `item` int(10) NOT NULL,
  `num` int(10) not null,
  `money` decimal(10,2) not null,
  `location` int(10) not null,
  `month` varchar(127) not null,
  `addedDT` timestamp NOT NULL,
  PRIMARY KEY (`asid`),
   KEY `item` (`item`),
   KEY `location` (`location`),
  CONSTRAINT `areasale_fk1` FOREIGN KEY (`item`) REFERENCES `items` (`iid`)  on delete cascade on update cascade,
  CONSTRAINT `areasale_fk2` FOREIGN KEY (`location`) REFERENCES `areas` (`aid`)  on delete cascade on update cascade
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `applyfor`;
CREATE TABLE `applyfor` (
  `aid` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(127) NOT NULL,
  `tel` varchar(127) not null,
  `city` varchar(127) not null,
  `college` varchar(127) not null,
  `building` varchar(127) not null,
  `state` int(1) not null,
  `addedDT` timestamp NOT NULL,
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `applyincome`;
CREATE TABLE `applyincome` (
  `aid` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(127) NOT NULL,
  `tel` varchar(127) not null,
  `cardNo` varchar(127) not null,
  `sales` decimal(10,2) not null,
  `income` decimal(10,2) not null,
  `state` int(1) not null,
  `addedDT` timestamp NOT NULL,
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

select * from user;

select * from areas;

select * from managers;

INSERT INTO `wxcampus`.`managers` (`mid`, `ring`, `tel`, `name`, `password`, `location`, `say`, `addedDate`, `addedTime`) VALUES ('1', '1', '123', '测试', '123456', '1', '店长说', '2015-11-11', '00:00:00');

INSERT INTO `wxcampus`.`areas` (`aid`, `city`, `college`, `building`, `state`, `startTime`, `endTime`, `addedDate`, `addedTime`) VALUES ('1', '济南', '山东大学', '2号楼', '0', '21:00:00', '23:00:00', '2015-11-11', '00:00:00');

INSERT INTO `wxcampus`.`user` (`uid`, `tel`, `password`, `openid`, `name`, `room`, `location`, `itemsStar`, `registerDate`, `registerTime`) VALUES ('1', '123', '123456', '1111', '测试', '528', '1', '“”', '2015-11-11', '00:00:00');