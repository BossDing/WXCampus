package com.wxcampus.user;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.wxcampus.common.GlobalVar;

public class UserInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		// TODO Auto-generated method stub
		if(arg0.getController().getSessionAttr(GlobalVar.WXUSER)==null)
			arg0.getController().redirect("/usr/registion");
		else {
			arg0.invoke();
		}
		
	}

}
