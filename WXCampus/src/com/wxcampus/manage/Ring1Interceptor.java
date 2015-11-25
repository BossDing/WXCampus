package com.wxcampus.manage;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.util.Util;

public class Ring1Interceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		 Managers manager=arg0.getController().getSessionAttr(GlobalVar.BEUSER);
		 if(manager.getInt("ring")==0 || manager.getInt("ring")==1)
		 {
		   arg0.invoke();
		 }else
			 arg0.getController().redirect("/404/error?Msg="+Util.getEncodeText("无权访问"));   //无权访问	
	}

}
