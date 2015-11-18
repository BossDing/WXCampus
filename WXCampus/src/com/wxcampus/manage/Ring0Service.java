package com.wxcampus.manage;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
import com.wxcampus.util.Util;

public class Ring0Service {
	private Controller c;
	private Managers manager;
	
	public Ring0Service(Controller controller,Managers manager)
	{
		this.c=controller;
		this.manager=manager;
	}
	public void trades()
	{
		 String location=c.getPara("location");
		 String date=c.getPara("date");		
		 List<Record> tradeList;
		 String state=c.getPara("state");
		 if(state==null)
		 tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.location=?  and a.addedDate=? order by a.addedTime desc",location,date);
		 else {
			if(state.equals("0"))
				tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.state=0 and a.location=? and a.addedDate=? order by a.addedTime desc",location,date);
			else if(state.equals("1"))
				tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.state=1 and a.location=? and a.addedDate=? order by a.addedTime desc",location,date);
			else {
				c.redirect("error.html");
				return;
			}
		}
		 c.setAttr("tradeList", tradeList);
		 c.renderJson();
	}
	
	public void setSellingTime()
	{
		 String location=c.getPara("location");
		 String startTime=c.getPara("stime")+":00";
		 String endTime=c.getPara("etime")+":00";	 
		 Areas areas=Areas.dao.findFirst("select * from areas where aid=?",location);
		 areas.set("startTime", Util.filterUserInputContent(startTime)).set("endTime", Util.filterUserInputContent(endTime)).update();
		 IndexService iService=new IndexService();
		 iService.updateShopState(areas);
		 areas=Areas.dao.findById(areas.getInt("aid"));//需不需要更新对象待测试
		 c.renderHtml(areas.getStr("state"));
	}
	public void addArea()
	{
		String city=c.getPara("city");
		String college=c.getPara("college");
		String building=c.getPara("building");
		if(city!=null && college!=null && building!=null)
		{
			Areas areas=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",city,college,building);
			if(areas==null)
			{
				Areas area=new Areas();
				area.set("city", Util.filterUserInputContent(city)).set("college", Util.filterUserInputContent(college)).set("building", Util.filterUserInputContent(building));
				area.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
				area.save();		
				ManageController.logger.info(manager.getStr("name")+"---添加了地区-"+city+"-"+college+"-"+building);
			}else {
				c.renderHtml("当前添加地区已存在！");
			}
		}else 
			c.redirect("/index/error.html");   //参数错误
	}
	
	public void setManager()
	{
		Managers manager=c.getModel(Managers.class);  //ring tel name password location
		manager.set("name",Util.filterUserInputContent(manager.getStr("name")));
		manager.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
		manager.save();
		c.redirect("/mgradmin/areas?"+manager.getInt("location"));
		
	}
	
}
