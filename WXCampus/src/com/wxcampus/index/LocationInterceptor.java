package com.wxcampus.index;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.wxcampus.user.User;

public class LocationInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		Controller c=arg0.getController();
		String city=c.getPara("city");
		String college=c.getPara("college");
		String building=c.getPara("building");
		if(city!=null && college!=null && building!=null)
		{
		Areas areas=Areas.dao.findFirst("select * from areas where city="+city+" and college="+college+" and building="+building);
		if(areas!=null)
		{
			User user=c.getSessionAttr("sessionUser");
			if(user!=null)
				user.set("location", areas.get("aid")).update();
		}
		}
		arg0.invoke();
		
	}

}
