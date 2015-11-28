package com.wxcampus.manage;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.util.Util;

public class Ring0Interceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		// TODO Auto-generated method stub
		 Managers manager=arg0.getController().getSessionAttr(GlobalVar.BEUSER);
		 if(manager.getInt("ring")==0)
		 {
		   arg0.invoke();
		 }else
			 arg0.getController().redirect("/mgradmin/error?Msg="+Util.getEncodeText("无权访问"));   //无权访问
	}

}
