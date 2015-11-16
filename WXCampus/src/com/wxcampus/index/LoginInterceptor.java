package com.wxcampus.index;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.user.User;

/**
 * 用于快速登录
 * @author Potato
 *
 */
public class LoginInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		if(arg0.getController().getSessionAttr(GlobalVar.WXUSER)==null)
		{
	    arg0.getController().setSessionAttr(GlobalVar.OPENID, "666666");
		String openid=arg0.getController().getSessionAttr(GlobalVar.OPENID);
		User user=User.me.findFirst("select * from user where openid=?",openid);
		if(user!=null)
		  arg0.getController().setSessionAttr(GlobalVar.WXUSER, user);	  
		}
		
		arg0.invoke();
			
		
	}

}
