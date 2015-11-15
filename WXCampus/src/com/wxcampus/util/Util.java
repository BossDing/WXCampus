package com.wxcampus.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Util {
	
	public static Logger logger=null;
	public static String currentDay="2015-11-11";
	public static String getDate()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	
	public static String getTime()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
		return sdf.format(new Date());
	}
	public static String getImgPath()
	{
		return System.getProperty("user.dir")+"/imgs/";
	}
	public static Logger getLogger()
	{
		String day=new SimpleDateFormat("yyyy-MM-dd").format(new Date().toString());
		if(day.equals(currentDay))
			return logger;
		else {
			currentDay=day;
			Logger dayLogger = Logger.getLogger("dayLogger");  	  
			Layout layout = new PatternLayout("%d %p [%c] - %m%n");  	  
			Appender appender;
			try {
				appender = new FileAppender(layout, "./logs+/"+currentDay+".log");				  
				dayLogger.addAppender(appender); 
				logger=dayLogger;
				return logger;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			return null;
		}
		
	}

}
