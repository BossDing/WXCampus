package com.wxcampus.manage;

import com.jfinal.plugin.activerecord.Model;

/**
 * mysql> desc managers;
+-----------+--------------+------+-----+----------+----------------+
| Field     | Type         | Null | Key | Default  | Extra          |
+-----------+--------------+------+-----+----------+----------------+
| mid       | int(10)      | NO   | PRI | NULL     | auto_increment |
| ring      | int(1)       | NO   |     | NULL     |   0:����Ա   1:�곤      
| tel       | int(11)      | NO   |     | NULL     |                |
| name      | varchar(255) | NO   |     | NULL     |                |
| password  | varchar(255) | NO   |     | NULL     |                |
| location  | int(10)      | NO   | MUL | NULL     |                |
| say       | varchar(255) | YES  |     |          |                | 
| addedDate | date         | NO   |     | NULL     |                |
| addedTime | time         | NO   |     | NULL     |                |
+-----------+--------------+------+-----+----------+----------------+
 * @author Potato
 *
 */
@SuppressWarnings("serial")
public class Managers extends Model<Managers>{
	
	public static final Managers dao=new Managers();

}
