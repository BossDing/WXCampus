package com.wxcampus.user;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class SendMessageVcode {
	
	public static boolean send(String tel,String vcode)
	{
		String url="http://apis.baidu.com/kingtto_media/106sms/106sms?";
		String para="mobile="+tel+"&content=����֤�롿��������֤����"+vcode+"��5��������Ч��";
        String result=getResponse(url+para);
        if(result.contains("Success"))
        	return true;
        else
        	return false;
	}
	
	public static String getResponse(String url)
	   {
		   HttpClient hc=new DefaultHttpClient();
			HttpGet hGet=new HttpGet(url);
			hGet.addHeader("Content-Type","application/json;charset=utf-8");
			hGet.addHeader("apikey", "12585821319993fbfa592c59bfed8769");
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

}
