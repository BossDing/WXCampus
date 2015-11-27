package com.wxcampus.manage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.render.Render;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
import com.wxcampus.items.Areasales;
import com.wxcampus.items.Trades;
import com.wxcampus.util.Util;

public class Ring2Service {
	
	private Controller c;
	private Managers manager;
	
	public Ring2Service(Controller controller,Managers manager)
	{
		this.c=controller;
		this.manager=manager;
	}
	
	public void trades()
	{
		int page=1;
		int flag=0;  // 0 全部 1未处理 2已完成
		if(c.getParaToInt(0)!=null){
			page=c.getParaToInt(0);
		}
		 String date=Util.getDate();
		if(c.getPara("date")!=null){
			date=c.getPara("date");	
		}
		 List<Trades> ridList;
		 String state=c.getPara("state");
		 if(state==null)
			 ridList=Trades.dao.paginate(page, 10, "select distinct rid,room,state,addedDate,addedTime", "from trades where seller=? and addedDate=? order by addedTime desc",manager.getInt("mid"),date).getList();
		 else {
			if(state.equals("0"))
			{ ridList=Trades.dao.paginate(page, 10, "select distinct rid,room,state,addedDate,addedTime", "from trades where state=? and seller=? and addedDate=? order by addedTime desc",Integer.parseInt(state),manager.getInt("mid"),date).getList();
			  flag=1;
			}
			else if(state.equals("1"))
			{
		     ridList=Trades.dao.paginate(page, 10, "select distinct rid,room,state,addedDate,addedTime", "from trades where state=? and seller=? and addedDate=? order by addedTime desc",Integer.parseInt(state),manager.getInt("mid"),date).getList();
             flag=2;
			}
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
				double money=0;
				for(int k=0;k<itemsRecords.size();k++)
				{
					money+=itemsRecords.get(k).getBigDecimal("price").doubleValue();
				}
				Record temp=new Record();
				temp.set("rid", rid);
				temp.set("state", ridList.get(i).getInt("state"));
				temp.set("addedDate", ridList.get(i).get("addedDate"));
				temp.set("addedTime", ridList.get(i).get("addedTime"));
				temp.set("items", itemsRecords);
				temp.set("money", money);
				temp.set("room", ridList.get(i).get("room"));
				records.add(temp);
			}
		 c.setAttr("tradeList", records);
		 c.setAttr("flag", flag);
		 c.setAttr("date_info", date);
		 c.setAttr("page", page);
	}
	

	
	public void confirmTrade()
	{
		int rid=c.getParaToInt("rid");
		List<Trades> trades=Trades.dao.find("select state,seller from trades where rid=?", rid);
		if(trades!=null)
		{
			if(trades.get(0).getInt("seller")!=manager.getInt("mid"))
			{
				c.redirect("/404/error");
				return;
			}
			for(int i=0;i<trades.size();i++)
			{
				trades.get(i).set("state", 1).update();
			}
		}else {
			c.redirect("/404/error");
			return;
		}
		
		c.renderHtml(Util.getJsonText("OK"));
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
