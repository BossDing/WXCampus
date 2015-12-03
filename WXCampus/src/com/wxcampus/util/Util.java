package com.wxcampus.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import Decoder.BASE64Encoder;

import com.jfinal.kit.PathKit;
import com.mysql.jdbc.StringUtils;
import com.wxcampus.items.Trades;

import freemarker.cache.StringTemplateLoader;

public class Util {
	public static String APPID="wx116ad715812ea4e7";
	public static String APPSECRET="c9f55326f01963db32178214bcd11747";
	public static String MCH_ID="1290419701";
	public static String MCH_KEY="Missu251abcdefghijklmnopqrstuvwx";
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

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
	public static String getTimeStamp()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
	public static String getEndTimeStamp()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date(System.currentTimeMillis()+1000*60*6));
	}
	public static String getJsonText(String text)
	{
		return "{\"Msg\":\""+text+"\"}";
	}
	private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

	private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

	public static String Md5(String str){
        //确定计算方法
        MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
	      //  BASE64Encoder base64en = new BASE64Encoder();
	        //加密后的字符串
	        String newstr=byteArrayToHexString(md5.digest(str.getBytes("utf-8"))).toUpperCase();   
	        return newstr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;    
    }
	public static String getSign(List<Element> elements)
	{
		Collections.sort(elements, new ElementComparator());
		//elements.sort();
	    Iterator<Element> iterator11=elements.iterator();
	    String sign="";
		while(iterator11.hasNext())
		{
			Element ele=iterator11.next();
			if(ele.getName().equals("sign") || ele.getText().equals(""))
				continue;
			sign+=(ele.getName()+"="+ele.getText()+"&");
		}
		sign+=("key="+Util.MCH_KEY);
		System.out.println(sign);
		sign=Util.Md5(sign);
		return sign;
	}
	public static String getTradeNo()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String tn=sdf.format(new Date())+getRandomString(10);
//		while(Trades.dao.findFirst("select tid from trades where tradeNo=?",tn)!=null)
//		{
//			tn=sdf.format(new Date())+getRandomString(10);
//		}
		return tn;
	}
	public static String getRandomString(int length) { //length表示生成字符串的长度  
	    String base = "0123456789"; 
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	 } 
	public static String getRandomString() { //length表示生成字符串的长度  
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789"; 
	    int length=16;
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
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
		//return System.getProperty("user.dir")+"/imgs/";
		return PathKit.getWebRootPath()+"/imgs/";
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
				appender = new FileAppender(layout, "./logs/"+currentDay+".log");				  
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
