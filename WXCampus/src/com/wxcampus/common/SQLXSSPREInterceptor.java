package com.wxcampus.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class SQLXSSPREInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		String para=arg0.getController().getPara();
		if(para!=null)
		{
			if(para.contains("and 1=") || para.contains("or 1="))
			{
				arg0.getController().redirect("/index/error.html");
				return;
			}
		}
//		para=para.replaceAll("<", "&lt;").replaceAll(">", "&gt;"); //˫��Ŵ��
//		arg0.getController().setUrlPara(para);
		arg0.invoke();	
	}

}
