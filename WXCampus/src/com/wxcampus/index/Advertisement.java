package com.wxcampus.index;

import com.jfinal.plugin.activerecord.Model;

/**
 * 
 * mysql> desc advertisement;
+-----------+--------------+------+-----+---------+----------------+
| Field     | Type         | Null | Key | Default | Extra          |
+-----------+--------------+------+-----+---------+----------------+
| astid     | int(10)      | NO   | PRI | NULL    | auto_increment |
| img       | varchar(255) | NO   |     | NULL    |                |
| addedDate | date         | NO   |     | NULL    |                |
| addedTime | time         | NO   |     | NULL    |                |
+-----------+--------------+------+-----+---------+----------------+
 *
 */
@SuppressWarnings("serial")
public class Advertisement extends Model<Advertisement>{
	
	public static final Advertisement dao=new Advertisement();

}
