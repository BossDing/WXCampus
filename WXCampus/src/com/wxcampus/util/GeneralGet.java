package com.wxcampus.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
@Use   通用Get方法
@author  Popez
@time    2015年9月24日 00:02:55
ddddddddddddddddddddddddddddddddddddddddddddd
*/
public class GeneralGet
{
   public static String getResponse(String url)
   {
	   HttpClient hc=new DefaultHttpClient();
		HttpGet hGet=new HttpGet(url);
		hGet.addHeader("Content-Type","application/json;charset=utf-8");
   	HttpResponse hResponse;
    try
  		{
  			hResponse=hc.execute(hGet);
  			 hGet.abort();
  			return EntityUtils.toString(hResponse.getEntity(),"utf-8");
  		} catch (Exception e)
  		{
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
    return null;
   }
   
   /**
    * 下载二维码图片到本地
    * @param url   
    * @return   图片路径
    */
   public static String downloadImage(String url)
   {
	   HttpClient hc=new DefaultHttpClient();
		HttpGet hGet=new HttpGet(url);
		HttpResponse hResponse;
		String imagePath="F:/temp22222.jpg";
		try
		{
			hResponse=hc.execute(hGet);
			
		    HttpEntity httpEntity=hResponse.getEntity();
			InputStream is = httpEntity.getContent();
			BufferedImage sourcebm=ImageIO.read(is);
			ImageIO.write(sourcebm, "jpg", new File(imagePath));
			hGet.abort();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imagePath;
	    
   }
}
