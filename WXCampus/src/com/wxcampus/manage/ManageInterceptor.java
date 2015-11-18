package com.wxcampus.manage;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.wxcampus.common.GlobalVar;

public class ManageInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		Managers manager=arg0.getController().getSessionAttr(GlobalVar.BEUSER);
		if(manager==null)
			arg0.getController().redirect("/mgradmin/login");
		else
			arg0.invoke();
	}

}
