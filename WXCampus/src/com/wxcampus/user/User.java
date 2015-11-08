package com.wxcampus.user;

import com.jfinal.plugin.activerecord.Model;

/**
将表结构放在此，消除记忆负担
mysql> desc user;
+--------------+---------------+------+-----+---------+----------------+
| Field        | Type          | Null | Key | Default | Extra          |
+--------------+---------------+------+-----+---------+----------------+
| uid          | int(10)       | NO   | PRI | NULL    | auto_increment |
| tel          | int(11)       | NO   |     | NULL    |                |
| password     | varchar(255)  | NO   |     | NULL    |                |
| openid       | varchar(255)  | NO   |     | NULL    |                |
| name         | varchar(255)  | YES  |     | NULL    |                |
| room         | varchar(255)  | YES  |     | NULL    |                |
| location     | int(10)       | YES  | MUL | 1       |                |
| itemsStar    | varchar(1024) | YES  |     |         |                |
| registerDate | date          | NO   |     | NULL    |                |
| registerTime | time          | NO   |     | NULL    |                |
+--------------+---------------+------+-----+---------+----------------+
*/
@SuppressWarnings("serial")
public class User extends Model<User>{
	
	public static final User me = new User();

}
