package com.wxcampus.user;

import com.jfinal.plugin.activerecord.Model;

/**
 * mysql> desc advices;
+-----------+---------+------+-----+---------+----------------+
| Field     | Type    | Null | Key | Default | Extra          |
+-----------+---------+------+-----+---------+----------------+
| aid       | int(10) | NO   | PRI | NULL    | auto_increment |
| uid       | int(10) | NO   |     | NULL    |                |
| content   | text    | NO   |     | NULL    |                |
| addedDate | date    | NO   |     | NULL    |                |
| addedTime | time    | NO   |     | NULL    |                |
+-----------+---------+------+-----+---------+----------------+
 * @author Potato
 *
 */
@SuppressWarnings("serial")
public class Advices extends Model<Advices>{

	public static final Advices dao=new Advices();
}
