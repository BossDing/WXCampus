package com.wxcampus.user;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class UserInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		// TODO Auto-generated method stub
		if(arg0.getController().getSessionAttr("sessionUser")!=null)
			arg0.getController().redirect("index.html");
		else {
			arg0.invoke();
		}
		
	}

}
