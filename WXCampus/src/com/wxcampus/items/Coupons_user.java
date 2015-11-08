package com.wxcampus.items;

import com.jfinal.plugin.activerecord.Model;

/**
 * mysql> desc coupons_user;
+-----------+------------+------+-----+---------+----------------+
| Field     | Type       | Null | Key | Default | Extra          |
+-----------+------------+------+-----+---------+----------------+
| cuid      | int(10)    | NO   | PRI | NULL    | auto_increment |
| cid       | int(10)    | NO   |     | NULL    |                |
| owner     | int(10)    | NO   |     | NULL    |                |
| used      | tinyint(1) | YES  |     | 0       |                |
| endDate   | date       | NO   |     | NULL    |                |
| addedDate | date       | NO   |     | NULL    |                |
| addedTime | time       | NO   |     | NULL    |                |
+-----------+------------+------+-----+---------+----------------+
 * @author Potato
 *
 */
@SuppressWarnings("serial")
public class Coupons_user extends Model<Coupons_user>{
	
	public static final Coupons_user dao=new Coupons_user();

}
