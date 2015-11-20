package com.wxcampus.shop;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.wxcampus.index.Areas;

/**
 * 用于拦截在店铺打烊之后的非法请求
 * @author Potato
 *
 */
public class ShopInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		int areaID=arg0.getController().getSessionAttr("areaID");
		Areas areas=Areas.dao.findById(areaID);
		if(areas.getBoolean("state")==true)
			arg0.invoke();
		else
		{
			try {
				arg0.getController().redirect("/404/error?Msg="+URLEncoder.encode("当前商铺未营业", "utf-8"));
				} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
