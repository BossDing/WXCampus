package com.wxcampus.manage;

import com.jfinal.core.Controller;
import com.wxcampus.index.Areas;
import com.wxcampus.util.Util;

public class Ring0Service {
	private Controller c;
	private Managers manager;
	
	public Ring0Service(Controller controller,Managers manager)
	{
		this.c=controller;
		this.manager=manager;
	}
	
	public void addArea()
	{
		String city=c.getPara("city");
		String college=c.getPara("college");
		String building=c.getPara("building");
		if(city!=null && college!=null && building!=null)
		{
			Areas areas=Areas.dao.findFirst("select * from areas where city="+city+" and college="+college+" and building="+building);
			if(areas==null)
			{
				Areas area=new Areas();
				area.set("city", city).set("college", college).set("building", building);
				area.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
				area.save();		
				ManageController.logger.info(manager.get("name")+"---添加了地区-"+city+"-"+college+"-"+building);
			}else {
				c.renderHtml("当前添加地区已存在！");
			}
		}else 
			c.redirect("error.html");   //参数错误
	}
	
}
