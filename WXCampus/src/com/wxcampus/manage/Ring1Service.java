package com.wxcampus.manage;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;

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
		 tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.seller="+manager.getStr("mid")+" and a.addedDate="+date+" order by a.addedTime desc");
		 else {
			if(state.equals("0"))
				tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.state=0 and a.seller="+manager.getStr("mid")+" and a.addedDate="+date+" order by a.addedTime desc");
			else if(state.equals("1"))
				tradeList=Db.find("select a.rid,a.item,a.price,a.orderNum,a.state,a.addedTime,b.tel,b.room,b.name from trades as a,user as b where a.customer=b.uid and a.state=1 and a.seller="+manager.getStr("mid")+" and a.addedDate="+date+" order by a.addedTime desc");
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
		 Areas areas=Areas.dao.findFirst("select * from areas where aid="+manager.getStr("location"));
		 areas.set("startTime", startTime).set("endTime", endTime).update();
		 IndexService iService=new IndexService();
		 iService.updateShopState(areas);
		 areas=Areas.dao.findById(areas.get("aid"));//需不需要更新对象待测试
		 c.renderHtml(areas.getStr("state"));
	}

}
