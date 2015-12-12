package com.wxcampus.index;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.user.User;
import com.wxcampus.util.Util;

public class LocationInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		Controller c=arg0.getController();
		String city=c.getPara("city");
		String college=c.getPara("college");
		String building=c.getPara("building");
		if(city!=null && college!=null && building!=null)
		{
		Areas areas=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",city,college,building);
		if(areas!=null)
		{
			User user=c.getSessionAttr(GlobalVar.WXUSER);
			if(user!=null)
				user.set("location", areas.getInt("aid")).update();
		}
		}else {
			User user=c.getSessionAttr(GlobalVar.WXUSER);
			if(user==null)
				{
				arg0.getController().redirect("/index/getCity");
				return;
				}else {
					if(user.getInt("location")==1)
					{
						arg0.getController().redirect("/index/getCity");
						return;
					}else {
						Areas area=Areas.dao.findById(user.getInt("location"));
						if(area.getStr("building")==null || area.getStr("building").equals(""))
						{
							arg0.getController().redirect("/index/area?city="+Util.getEncodeText(area.getStr("city"))+"&college="+Util.getEncodeText(area.getStr("college")));
							return;
						}
					}
				}
		}
		arg0.invoke();
		
	}

}
