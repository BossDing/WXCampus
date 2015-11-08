package com.wxcampus.shop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.items.Coupons;
import com.wxcampus.items.Coupons_use;
import com.wxcampus.items.Coupons_user;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.items.Managers;
import com.wxcampus.items.Trades;
import com.wxcampus.user.User;

public class ShopController extends Controller{
	
	public void index()
	{
		String items[]=getPara().split("-");
		int areaID=getSessionAttr("areaID");
		List<Record> itemList=new ArrayList<Record>();
		for(int i=0;i<items.length;i++)
		{
			String temp[]=items[i].split(":");
			if(temp.length==2)
			{
			String iid=temp[0];
			Record item;
			item=Db.findFirst("select a.iid,a.iname,a.icon,a.originPrice,a.realPrice,b.restNum from items as a,items_on_sale as b where b.location="+areaID+" and a.iid="+iid+" and a.iid=b.iid");
		    item.set("orderNum", temp[1]);
			itemList.add(item);
			}else
				redirect("error.html");
			
		}
		setAttr("itemList", itemList);
		render("index.html");
	}
	public void confirm()
	{
		String items[]=getPara().split("-");
		int areaID=getSessionAttr("areaID");
		List<Record> itemList=new ArrayList<Record>();
		double totalMoney=0;
		for(int i=0;i<items.length;i++)
		{
			Record record=new Record();
			String temp[]=items[i].split(":");
			if(temp.length==2)
			{
			String iid=temp[0];
			int num=Integer.parseInt(temp[1]);
			Items_on_sale ios=Items_on_sale.dao.findFirst("select * from items_on_sale where location="+areaID+" and iid="+iid);
			if(ios.getInt("restNum")<num)
				{redirect("error.html"); break;}
			Items item=Items.dao.findFirst("select * from items where iid="+iid);
			record.set("iid", item.getStr("iid"));
			record.set("iname", item.getStr("iname"));
			record.set("icon", item.getStr("icon")+"-small");
			record.set("orderNum", num);
			record.set("price", item.getDouble("realPrice")*num);
			totalMoney+=(item.getDouble("realPrice")*num);
			itemList.add(record);
			}else
				redirect("error.html");
		}
		setAttr("itemList", itemList);
		setSessionAttr("itemList", itemList);
		setAttr("totalMoney", totalMoney);
		setSessionAttr("totalMoney", totalMoney);
		
		User user=getSessionAttr("sessionUser");
		setAttr("userTel", user.get("tel"));
		if(user.getStr("name")!=null)
			setAttr("userName", user.getStr("name"));
		else
			setAttr("userName", "");
		if(user.getStr("room")!=null)
			setAttr("userRoom", user.getStr("room"));
		else
			setAttr("userRoom", "");
		render("confirm.html");
	}
	
	public void coupons() //ajax
	{
		User user=getSessionAttr("sessionUser");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String date=sdf.format(new Date().toString());
		List<Record> cpList=Db.find("select a.money,b.cuid,b.endDate from coupons as a,coupons_user as b where b.owner="+user.getStr("uid")+" and b.used=0 and a.cid=b.cid and b.endDate>="+date);
		setAttr("cpList", cpList);
		renderJson();
	}
	
	public void pay()
	{
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		String date=sdf1.format(new Date().toString());
		SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm:ss");
		String time=sdf2.format(new Date().toString());
		String tel=getPara("userTel");
		String name=getPara("userName");
		String room=getPara("userRoom");
		User user=getSessionAttr("sessionUser");
		List<Record> itemList=getSessionAttr("itemList");
		double totalMoney=getSessionAttr("totalMoney");
		int areaID=getSessionAttr("areaID");
		int rid=Trades.dao.findFirst("select * from trades order by rid desc").getInt("rid")+1;
		Managers manager=Managers.dao.findFirst("select * from managers where location="+areaID);
		String cuid=getPara("cuid");
		if(cuid!=null)
		{
			
			Record coupons=Db.findFirst("select a.money,a.cid from coupons as a,coupons_user as b where b.cuid="+cuid+" and b.used=0 and b.endDate>="+date+" and a.cid=b.cid");
			if(cuid!=null)
			{
			  totalMoney-=coupons.getDouble("money");
			  Coupons_user.dao.findById(cuid).set("used", 1).update();
			  Coupons_use cu=new Coupons_use();
			  cu.set("rid", rid);
			  cu.set("cid",coupons.get("cid"));
			  cu.set("realpay", totalMoney);
			  cu.set("addedDate", date);
			  cu.set("addedTime", time);
			  cu.save();
			}
		}
		for(int i=0;i<itemList.size();i++)
		{
		  Trades trades=new Trades();
		  trades.set("rid", rid);
		  trades.set("customer", user.get("uid"));
		  trades.set("seller", manager.get("mid"));
		  trades.set("location", areaID);
		  trades.set("finishedDate", date);
		  trades.set("finishedTime", time);
		  trades.set("item", itemList.get(i).get("iid"));
		  trades.set("price", itemList.get(i).get("price"));
		  trades.set("orderNum", itemList.get(i).get("orderNum"));
		  trades.set("state", 0);  //0:正在派送  1:交易完成
		  trades.save();
		}
		removeSessionAttr("itemList");
		removeAttr("totalMoney");
		render("pay-success.html");
	}

}
