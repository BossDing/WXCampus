package com.wxcampus.manage;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.index.Areas;
import com.wxcampus.items.Areasales;
import com.wxcampus.items.Trades;
import com.wxcampus.user.Advices;
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
		int page=1;
		int flag=0;  // 0 全部 1未处理 2已完成
		if(c.getParaToInt(0)!=null){
			page=c.getParaToInt(0);
		}
		 String date=Util.getDate();
		if(c.getPara("date")!=null){
			date=c.getPara("date");	
		}
		 Areas area=Areas.dao.findById(manager.getInt("location"));
		 List<Areas> areaList=Areas.dao.find("select * from areas where city=? and college=?",area.getStr("city"),area.getStr("college"));	
		 List<Trades> ridList=new ArrayList<Trades>();
		 String state=c.getPara("state");
		 for(int i=0;i<areaList.size();i++)
		 {
		 if(state==null)
			 ridList.addAll(Trades.dao.paginate(page,10,"select distinct rid,state,addedDate,addedTime","from trades where location=? and addedDate=? order by addedTime desc",areaList.get(i).getInt("aid"),date).getList());
		 else {
			if(state.equals("0"))
				{ridList.addAll(Trades.dao.paginate(page,10,"select distinct rid,state,addedDate,addedTime","from trades where state=0 and location=? and addedDate=? order by addedTime desc",areaList.get(i).getInt("aid"),date).getList());
				flag=1;
				}
				else if(state.equals("1"))
				{ridList.addAll(Trades.dao.paginate(page,10,"select distinct rid,state,addedDate,addedTime","from trades where state=1 and location=? and addedDate=? order by addedTime desc",areaList.get(i).getInt("aid"),date).getList());
			     flag=2;
				}
				else {
				c.redirect("/404/error");
				return;
			}
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
				temp.set("money", money);
				temp.set("items", itemsRecords);
				records.add(temp);
			}
		 c.setAttr("tradeList", records);
		 c.setAttr("flag", flag);
		 c.setAttr("date_info", date);
		 c.setAttr("page", page);
	}
	
}
