package com.wxcampus.user;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class UserValidator extends Validator{

	@Override
	protected void handleError(Controller arg0) {
		// TODO Auto-generated method stub
		arg0.keepModel(User.class);
		arg0.render("registion.html");
	}

	@Override
	protected void validate(Controller arg0) {
		// TODO Auto-generated method stub
		User user=User.me.findFirst("select * from user where tel="+arg0.getModel(User.class).getStr("tel"));
		if(user!=null)
			addError("TelMsg", "当前手机号已被注册");	
	}

}
