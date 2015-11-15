package com.wxcampus.index;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.wxcampus.common.GlobalVar;

public class GetOpenidInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		Controller c=arg0.getController();
		if(c.getSessionAttr(GlobalVar.OPENID)==null)
		c.redirect("微信授权页");    //回调/index/authorize
		else
		arg0.invoke();
	
	}

}
