package com.wxcampus.manage;

import java.lang.annotation.Retention;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
import com.wxcampus.items.Areasales;
import com.wxcampus.items.Incomes;
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
		int page=1;
		int flag=0;  // 0 全部 1未处理 2已完成
		if(c.getParaToInt(0)!=null){
			page=c.getParaToInt(0);
		}
		 String date=Util.getDate();
		if(c.getPara("date")!=null){
			date=c.getPara("date");	
		}
		 String location=c.getPara("location");	
		 List<Trades> ridList;
		 String state=c.getPara("state");
		 if(state==null)
			 ridList=Trades.dao.paginate(page,10,"select distinct rid,state,addedDate,addedTime","from trades where location=? and addedDate=? order by addedTime desc",location,date).getList();
		 else {
			if(state.equals("0"))
				{ridList=Trades.dao.paginate(page,10,"select distinct rid,state,addedDate,addedTime","from trades where state=0 and location=? and addedDate=? order by addedTime desc",location,date).getList();
			     flag=1;}   
				else if(state.equals("1"))
				{ridList=Trades.dao.paginate(page,10,"select distinct rid,state,addedDate,addedTime","from trades where state=1 and location=? and addedDate=? order by addedTime desc",location,date).getList();
			     flag=2; }
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
				temp.set("money", money);
				temp.set("items", itemsRecords);
				records.add(temp);
			}
		 c.setAttr("tradeList", records);
		 c.setAttr("flag", flag);
		 c.setAttr("date_info", date);
		 c.setAttr("page", page);
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
		if(city!=null)
		{
			Areas areas=null;
			if(college==null)
			{
				areas=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",city,"","");
				if(areas==null)
				{
					Areas area=new Areas();
					area.set("city", Util.filterUserInputContent(city)).set("college", "").set("building", "");
					area.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
					area.save();		
					ManageController.logger.info(manager.getStr("name")+"---添加了城市-"+city);
					c.renderHtml(Util.getJsonText("OK"));
				}else
					c.renderHtml(Util.getJsonText("当前添加城市已存在！"));
				return;
			}
			if(building==null)
			{
				areas=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",city,college,"");
				if(areas==null)
				{
					Areas area=new Areas();
					area.set("city", Util.filterUserInputContent(city)).set("college", Util.filterUserInputContent(college).replaceAll("（", "(").replaceAll("）", ")")).set("building", "");
					area.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
					area.save();		
					ManageController.logger.info(manager.getStr("name")+"---添加了学校-"+city+"-"+college);
					c.renderHtml(Util.getJsonText("OK"));
				}else
					c.renderHtml(Util.getJsonText("当前添加学校已存在！"));
				return;
			}
			areas=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",city,college,building);
			if(areas==null)
			{
				Areas area=new Areas();
				area.set("city", Util.filterUserInputContent(city)).set("college", Util.filterUserInputContent(college)).set("building", Util.filterUserInputContent(building));
				area.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
				area.save();		
				ManageController.logger.info(manager.getStr("name")+"---添加了地区-"+city+"-"+college+"-"+building);
				c.renderHtml(Util.getJsonText("OK"));
			}else {
				c.renderHtml(Util.getJsonText("当前添加地区已存在！"));
			}
		}else 
			c.redirect("/404/error");   //参数错误
	}
	
	public void setManager()
	{
		Managers manager=c.getModel(Managers.class);  //ring tel name password  ---location---
		if(manager.getStr("tel")==null || manager.getStr("tel").length()!=11)
		{
			String url="/mgradmin/areas?city="+Util.getEncodeText(c.getPara("city"))+"&college="+Util.getEncodeText(c.getPara("college"));
			c.renderHtml("<script>alert('手机号必须为11位!');window.location='"+url+"';</script>");
			return;
		}
		if(manager.getInt("ring")==1)
		{
			Areas area=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",c.getPara("city"),c.getPara("college"),"");
		    if(area==null)
		    {
		    	c.redirect("/mgradmin/error");
		    	return;
		    }
		Managers oldManager=Managers.dao.findFirst("select * from managers where location=?",area.getInt("aid"));
		if(oldManager!=null)
		{
			oldManager.set("name",Util.filterUserInputContent(manager.getStr("name")));
			oldManager.set("tel",Util.filterUserInputContent(manager.getStr("tel")));
			oldManager.set("password",manager.getStr("tel").substring(5)).set("say", "");
			oldManager.set("idcard",Util.filterUserInputContent(manager.getStr("idcard")));
			oldManager.set("grade",Util.filterUserInputContent(manager.getStr("grade")));
			oldManager.set("major",Util.filterUserInputContent(manager.getStr("major")));
			oldManager.set("stuid",Util.filterUserInputContent(manager.getStr("stuid")));
			oldManager.set("sex",manager.getBoolean("sex"));
			oldManager.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
			oldManager.update();
		}else {
			manager.set("name",Util.filterUserInputContent(manager.getStr("name")));
			manager.set("tel",Util.filterUserInputContent(manager.getStr("tel")));
			manager.set("idcard",Util.filterUserInputContent(manager.getStr("idcard")));
			manager.set("grade",Util.filterUserInputContent(manager.getStr("grade")));
			manager.set("major",Util.filterUserInputContent(manager.getStr("major")));
			manager.set("stuid",Util.filterUserInputContent(manager.getStr("stuid")));
			manager.set("sex",manager.getBoolean("sex"));
			manager.set("location", area.getInt("aid"));
			manager.set("password", manager.getStr("tel").substring(5));
			manager.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
			manager.save();
			new Incomes().set("mid", manager.getInt("mid")).set("sales", new BigDecimal(0)).set("addedDT", new Timestamp(System.currentTimeMillis())).save();
		}
		c.redirect("/mgradmin/areas?city="+Util.getEncodeText(c.getPara("city")));	
		}else if(manager.getInt("ring")==2)
		{
			Areas area=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",c.getPara("city"),c.getPara("college"),c.getPara("building"));
		    if(area==null)
		    {
		    	c.redirect("/mgradmin/error");
		    	return;
		    }
		Managers oldManager=Managers.dao.findFirst("select * from managers where location=?",area.getInt("aid"));
		if(oldManager!=null)
		{
			oldManager.set("name",Util.filterUserInputContent(manager.getStr("name")));
			oldManager.set("tel",Util.filterUserInputContent(manager.getStr("tel")));
			oldManager.set("password",manager.getStr("tel").substring(5)).set("say", "");
			oldManager.set("idcard",Util.filterUserInputContent(manager.getStr("idcard")));
			oldManager.set("grade",Util.filterUserInputContent(manager.getStr("grade")));
			oldManager.set("major",Util.filterUserInputContent(manager.getStr("major")));
			oldManager.set("stuid",Util.filterUserInputContent(manager.getStr("stuid")));
			oldManager.set("sex",manager.getBoolean("sex"));
			oldManager.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
			oldManager.update();
		}else {
			manager.set("name",Util.filterUserInputContent(manager.getStr("name")));
			manager.set("tel",Util.filterUserInputContent(manager.getStr("tel")));
			manager.set("idcard",Util.filterUserInputContent(manager.getStr("idcard")));
			manager.set("grade",Util.filterUserInputContent(manager.getStr("grade")));
			manager.set("major",Util.filterUserInputContent(manager.getStr("major")));
			manager.set("stuid",Util.filterUserInputContent(manager.getStr("stuid")));
			manager.set("sex",manager.getBoolean("sex"));
			manager.set("location", area.getInt("aid"));
			manager.set("password", manager.getStr("tel").substring(5));
			manager.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
			manager.save();
			new Incomes().set("mid", manager.getInt("mid")).set("sales", new BigDecimal(0)).set("addedDT", new Timestamp(System.currentTimeMillis())).save();
		}
		c.redirect("/mgradmin/areas?city="+Util.getEncodeText(c.getPara("city"))+"&college="+Util.getEncodeText(c.getPara("college")));	
		}
	}
	
}
