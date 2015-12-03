package com.wxcampus.util;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
/**
@Use   通用Post方法
@author  Popez
@time    2015年9月24日 00:04:51

*/
public class GeneralPost
{
	/**
	 * 
	 * @param json   json字符串
	 * @param url    url地址
	 * @return       响应返回的内容
	 */
	 public static String getPostResponse(String json,String url)
	   {
		   HttpClient hc=new DefaultHttpClient();
		   HttpPost hPost=new HttpPost(url);
	   	   HttpResponse hResponse;
	   	   hPost.addHeader("Content-Type", "application/json;charset=utf-8");
	    try
	  		{
		   	   StringEntity se=new StringEntity(json,"UTF-8");
		   	   se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json")); 
		   	   hPost.setEntity(se);
		   	    
	  			hResponse=hc.execute(hPost);
	  			hPost.abort();
	  			return EntityUtils.toString(hResponse.getEntity());
	  		} catch (Exception e)
	  		{
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	    return null;
	   }
	 
	 public static String getResponseXML(String xml,String url)
	   {
		   HttpClient hc=new DefaultHttpClient();
		   HttpPost hPost=new HttpPost(url);
	   	   HttpResponse hResponse;
	   	   hPost.addHeader("Content-Type", "text/xml;charset=utf-8");
	    try
	  		{
		   	   StringEntity se=new StringEntity(xml,"UTF-8");
		   	   se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "text/xml")); 
		   	   hPost.setEntity(se);
		   	    
	  			hResponse=hc.execute(hPost);
	  			hPost.abort();
	  			return EntityUtils.toString(hResponse.getEntity());
	  		} catch (Exception e)
	  		{
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	    return null;
	   }
	 public static String uploadFile(File file,String url)
	 {
		 FileBody bin=null;
		 HttpClient httpclient = new DefaultHttpClient();  
	     HttpPost httppost = new HttpPost(url);  
	        if(file != null) {  
	            bin = new FileBody(file);  
	        }
	     MultipartEntity reqEntity = new MultipartEntity();
	     reqEntity.addPart("media", bin); 
	     
	     httppost.setEntity(reqEntity);  
	  //   System.out.println("执行: " + httppost.getRequestLine());  
	       
	     try
		{
			HttpResponse response = httpclient.execute(httppost);
			return EntityUtils.toString(response.getEntity());
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	     return null;
	     
	 }
}

