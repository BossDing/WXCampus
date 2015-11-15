package com.wxcampus.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class OpenidInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		if(arg0.getController().getSessionAttr(GlobalVar.OPENID)==null)
			arg0.getController().redirect("/index");
		else
			arg0.invoke();
		
	}

}
