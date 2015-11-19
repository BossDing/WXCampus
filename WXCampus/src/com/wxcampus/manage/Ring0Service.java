package com.wxcampus.manage;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
import com.wxcampus.items.Trades;
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
		 List<Trades> ridList;
		 String state=c.getPara("state");
		 if(state==null)
			 ridList=Trades.dao.find("select distinct rid,state,addedDate,addedTime from trades where location=? and addedDate=? order by addedTime desc",location,date);
		 else {
			if(state.equals("0"))
				ridList=Trades.dao.find("select distinct rid,state,addedDate,addedTime from trades where state=0 and location=? and addedDate=? order by addedTime desc",location,date);
			else if(state.equals("1"))
				ridList=Trades.dao.find("select distinct rid,state,addedDate,addedTime from trades where state=1 and location=? and addedDate=? order by addedTime desc",location,date);
			else {
				c.redirect("/404/error");
				return;
			}
		}
			List<Record> records=new ArrayList<Record>();
			for(int i=0;i<ridList.size();i++)
			{
				int rid=ridList.get(i).getInt("rid");
				List<Record> itemsRecords=Db.find("select b.iname,b.icon,a.price,a.orderNum from trades as a,items as b where a.item=b.iid and a.rid=?",rid);
				//Record [] items=itemsRecords.toArray(new Record[itemsRecords.size()]);
				Record temp=new Record();
				temp.set("rid", rid);
				temp.set("state", ridList.get(i).getInt("state"));
				temp.set("addedDate", ridList.get(i).get("addedDate"));
				temp.set("addedTime", ridList.get(i).get("addedTime"));
				temp.set("items", itemsRecords);
				records.add(temp);
			}
		 c.setAttr("tradeList", records);
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
