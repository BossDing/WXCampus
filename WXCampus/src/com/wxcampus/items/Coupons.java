package com.wxcampus.items;

import com.jfinal.plugin.activerecord.Model;

/**
 * mysql> desc coupons;
+-----------+---------------+------+-----+---------+----------------+
| Field     | Type          | Null | Key | Default | Extra          |
+-----------+---------------+------+-----+---------+----------------+
| cid       | int(10)       | NO   | PRI | NULL    | auto_increment |
| money     | decimal(10,2) | NO   |     | NULL    |                |
| addedDate | date          | NO   |     | NULL    |                |
| addedTime | time          | NO   |     | NULL    |                |
+-----------+---------------+------+-----+---------+----------------+
 * @author Potato
 *
 */
@SuppressWarnings("serial")
public class Coupons extends Model<Coupons>{
	
	public static final Coupons dao=new Coupons();

}
