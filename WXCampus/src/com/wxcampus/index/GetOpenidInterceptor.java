package com.wxcampus.index;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.wxcampus.common.GlobalVar;

public class GetOpenidInterceptor implements Interceptor{

	public static String APPID="wx116ad715812ea4e7";
	public static String APPSECRET="c9f55326f01963db32178214bcd11747";
	private String Redirect_URL="http://www.missjzp.cn/index/authorize";
	@Override
	public void intercept(Invocation arg0) {
		Controller c=arg0.getController();
		try {
			//Redirect_URL=URLEncoder.encode(Redirect_URL, "utf-8");
			Redirect_URL="http%3a%2f%2fwww.missjzp.cn%2findex%2fauthorize";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(c.getSessionAttr(GlobalVar.OPENID)==null)
		{
		   c.redirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+APPID+"&redirect_uri="+Redirect_URL+"&response_type=code&scope=snsapi_userinfo&state=6666#wechat_redirect");    //index/authorize
		}
		else
		arg0.invoke();
	
	}

}
