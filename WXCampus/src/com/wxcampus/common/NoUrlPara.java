package com.wxcampus.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class NoUrlPara implements Interceptor{

	@Override
	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub
		if(inv.getController().getPara()!=null)
		{
			inv.getController().redirect("/404");
			//inv.getController().renderError(404);
		}else
			inv.invoke();
	}

}
