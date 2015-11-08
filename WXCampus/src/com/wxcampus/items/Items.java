package com.wxcampus.items;

import com.jfinal.plugin.activerecord.Model;

/**
 * mysql> desc items;
+-------------+---------------+------+-----+---------+----------------+
| Field       | Type          | Null | Key | Default | Extra          |
+-------------+---------------+------+-----+---------+----------------+
| iid         | int(10)       | NO   | PRI | NULL    | auto_increment |
| iname       | varchar(255)  | NO   |     | NULL    |                |
| icon        | varchar(255)  | NO   |     | NULL    |                |
| originPrice | decimal(10,2) | NO   |     | NULL    |                |
| realPrice   | decimal(10,2) | NO   |     | NULL    |                |
| category    | varchar(20)   | NO   |     | NULL    |                |
| restNum     | int(5)        | YES  |     | 0       |                |
| addedDate   | date          | NO   |     | NULL    |                |
| addedTime   | time          | NO   |     | NULL    |                |
+-------------+---------------+------+-----+---------+----------------+
 * @author Potato
 *
 */
@SuppressWarnings("serial")
public class Items extends Model<Items>{
	
	public static final Items dao=new Items();

}
