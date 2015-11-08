package com.wxcampus.items;

import com.jfinal.plugin.activerecord.Model;

/**
 * mysql> desc items_on_sale;
+-----------+---------+------+-----+---------+----------------+
| Field     | Type    | Null | Key | Default | Extra          |
+-----------+---------+------+-----+---------+----------------+
| iosid     | int(10) | NO   | PRI | NULL    | auto_increment |
| iid       | int(10) | NO   | MUL | NULL    |                |
| restNum   | int(5)  | NO   |     | NULL    |                |
| location  | int(10) | NO   | MUL | NULL    |                |
| addedDate | date    | NO   |     | NULL    |                |
| addedTime | time    | NO   |     | NULL    |                |
+-----------+---------+------+-----+---------+----------------+
 * @author Potato
 *
 */
@SuppressWarnings("serial")
public class Items_on_sale extends Model<Items_on_sale>{

	public static final Items_on_sale dao=new Items_on_sale();
}
