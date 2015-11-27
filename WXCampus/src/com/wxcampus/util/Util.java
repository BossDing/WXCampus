package com.wxcampus.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.mysql.jdbc.StringUtils;

import freemarker.cache.StringTemplateLoader;

public class Util {
	private final static Whitelist user_content_filter = Whitelist.relaxed();  
	static {  
	    user_content_filter.addTags("embed","object","param","span","div");  
	    user_content_filter.addAttributes(":all", "style", "class", "id", "name");  
	    user_content_filter.addAttributes("object", "width", "height","classid","codebase");      
	    user_content_filter.addAttributes("param", "name", "value");  
	    user_content_filter.addAttributes("embed", "src","quality","width","height","allowFullScreen","allowScriptAccess","flashvars","name","type","pluginspage");  
	}
	public static Logger logger=null;
	public static String currentDay="2015-11-11";
	public static String getDate()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	public static String getMonth()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
		return sdf.format(new Date());
	}
	public static String getTime()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
		return sdf.format(new Date());
	}
	public static String getJsonText(String text)
	{
		return "{\"Msg\":\""+text+"\"}";
	}
	public static String getEncodeText(String text)
	{
		String str="";
		try {
			str=URLEncoder.encode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	public static String getImgPath()
	{
		return System.getProperty("user.dir")+"/imgs/";
	}
	/**
	 * XSS过滤
	 * @param html
	 * @return
	 */
	public static String filterUserInputContent(String html) {  
	    if(StringUtils.isNullOrEmpty(html)) return "";  
	    return Jsoup.clean(html, user_content_filter);  
	    //return filterScriptAndStyle(html);  
	}
	public static Logger getLogger()
	{
		String day=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
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
