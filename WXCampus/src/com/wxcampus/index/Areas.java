package com.wxcampus.index;

import com.jfinal.plugin.activerecord.Model;

/**
 * mysql> desc areas;
+-----------+--------------+------+-----+---------+----------------+
| Field     | Type         | Null | Key | Default | Extra          |
+-----------+--------------+------+-----+---------+----------------+
| aid       | int(10)      | NO   | PRI | NULL    | auto_increment |
| city      | varchar(255) | NO   |     | NULL    |                |
| college   | varchar(255) | NO   |     | NULL    |                |
| building  | varchar(255) | NO   |     | NULL    |                |
| addedDate | date         | NO   |     | NULL    |                |
| addedTime | time         | NO   |     | NULL    |                |
+-----------+--------------+------+-----+---------+----------------+
 * @author Potato
 *
 */
@SuppressWarnings("serial")
public class Areas extends Model<Areas>{
	
	public static final Areas dao=new Areas();

}
