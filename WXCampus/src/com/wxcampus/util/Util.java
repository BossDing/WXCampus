package com.wxcampus.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	
	
	public static String getDate()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date().toString());
	}
	
	public static String getTime()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
		return sdf.format(new Date().toString());
	}

}
