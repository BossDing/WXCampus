package com.wxcampus.items;

import com.jfinal.plugin.activerecord.Model;

/**
 * mysql> desc coupons_use;
+-----------+---------------+------+-----+---------+----------------+
| Field     | Type          | Null | Key | Default | Extra          |
+-----------+---------------+------+-----+---------+----------------+
| cuid      | int(10)       | NO   | PRI | NULL    | auto_increment |
| cid       | int(10)       | NO   |     | NULL    |                |
| rid       | int(10)       | NO   |     | NULL    |                |
| realpay   | decimal(10,2) | NO   |     | NULL    |                |
| addedDate | date          | NO   |     | NULL    |                |
| addedTime | time          | NO   |     | NULL    |                |
+-----------+---------------+------+-----+---------+----------------+
 * @author Potato
 *
 */
@SuppressWarnings("serial")
public class Coupons_use extends Model<Coupons_use>{
	
	public static final Coupons_use dao=new Coupons_use();

}
