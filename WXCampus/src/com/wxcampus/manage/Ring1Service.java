package com.wxcampus.manage;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
import com.wxcampus.util.Util;

public class Ring1Service {
	
	private Controller c;
	private Managers manager;
	
	public Ring1Service(Controller controller,Managers manager)
	{
		this.c=controller;
		this.manager=manager;
	}
	
	public void trades()
	{
		 String date=c.getPara("date");		
		 List<Record> tradeList;
		 String state=c.getPara("state");
		 if(state==null)
		 tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.seller=?  and a.addedDate=? order by a.addedTime desc",manager.getInt("mid"),date);
		 else {
			if(state.equals("0"))
				tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.state=0 and a.seller=? and a.addedDate=? order by a.addedTime desc",manager.getInt("mid"),date);
			else if(state.equals("1"))
				tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.state=1 and a.seller=? and a.addedDate=? order by a.addedTime desc",manager.getInt("mid"),date);
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
		 String startTime=c.getPara("stime")+":00";
		 String endTime=c.getPara("etime")+":00";	 
		 Areas areas=Areas.dao.findFirst("select * from areas where aid=?",manager.getInt("location"));
		 areas.set("startTime", Util.filterUserInputContent(startTime)).set("endTime", Util.filterUserInputContent(endTime)).update();
		 IndexService iService=new IndexService();
		 iService.updateShopState(areas);
		 areas=Areas.dao.findById(areas.getInt("aid"));//需不需要更新对象待测试
		 c.renderHtml(areas.getStr("state"));
	}

}
