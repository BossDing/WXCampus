package com.wxcampus.shop;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.common.NoUrlPara;
import com.wxcampus.common.OpenidInterceptor;
import com.wxcampus.index.Areas;
import com.wxcampus.items.Areasales;
import com.wxcampus.items.Coupons;
import com.wxcampus.items.Coupons_use;
import com.wxcampus.items.Coupons_user;
import com.wxcampus.items.Incomes;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.items.Promotion;
import com.wxcampus.items.Trades;
import com.wxcampus.manage.Managers;
import com.wxcampus.user.User;
import com.wxcampus.user.UserInterceptor;
import com.wxcampus.util.Util;

/**
 * 微信端购物方面控制器类
 * @author Potato
 *
 */
@Before({OpenidInterceptor.class,UserInterceptor.class})
public class ShopController extends Controller{
	
	@Before({NoUrlPara.class,ShopInterceptor.class})
	public void index()
	{
		//String para=getPara("para");
		//if( para==null || para.equals(""))
		HashMap<Integer, Integer> map=getSessionAttr("Carts");
		if(map==null || map.isEmpty())
		{
			redirect("/404/error?Msg="+Util.getEncodeText("尚未选择商品"));
			return;
		}
		int areaID=getSessionAttr("areaID");
		Areas area=Areas.dao.findById(areaID);

		List<Record> itemList=new ArrayList<Record>();
		Set<Integer> items=map.keySet();
		Iterator<Integer> iterator=items.iterator();
		while(iterator.hasNext())
		{
			//String temp[]=items[i].split(":");
		//	if(temp.length==2)
			//{
			int iid=iterator.next();
			Record item;
			item=Db.findFirst("select a.iid,a.iname,a.icon,b.restNum,b.price from items as a,items_on_sale as b where a.iid=b.iid and b.location=? and a.iid=?",areaID,iid);
		    item.set("orderNum", map.get(iid));
			itemList.add(item);
		//	}else
		//		redirect("/404/error");
		}
		//String items[]=para.split(";");
		
//		for(int i=0;i<items.length;i++)
//		{

	//	}
		setAttr("itemList", itemList);
		setAttr("area", area);
		render("index.html");
	}
	public void incart()
	{
		int iid=getParaToInt("iid");
		int type=getParaToInt("type");  //0 添加 1减少
		
		HashMap<Integer, Integer> map=getSessionAttr("Carts");
		if(map==null)
		{
			setSessionAttr("Carts",new HashMap<Integer, Integer>() );
		}
		if(map.containsKey(iid))
		{
			if(type==0)
			   map.put(iid, map.get(iid)+1);
			else if(type==1)
			{
				if(map.get(iid)==1)
					map.remove(iid);
				else {
					map.put(iid, map.get(iid)-1);
				}
			}
		}else {
			if(type==0)
			   map.put(iid, 1);
			else if(type==1)
				{redirect("/404/error");
				return;}
		}
		setSessionAttr("Carts", map);
		renderHtml(Util.getJsonText("OK"));
	}
	public void confirm()
	{
		HashMap<Integer, Integer> map=getSessionAttr("Carts");
		if(map==null || map.isEmpty())
		{
			redirect("/404/error?Msg="+Util.getEncodeText("尚未选择商品"));
			return;
		}
		int areaID=getSessionAttr("areaID");
		Areas area=Areas.dao.findById(areaID);
		List<Record> itemList=new ArrayList<Record>();
		double totalMoney=0;
		Set<Integer> items=map.keySet();
		Iterator<Integer> iterator=items.iterator();
		while(iterator.hasNext())
		{
			Record record=new Record();
			int iid=iterator.next();
			int num=map.get(iid);
			Items_on_sale ios=Items_on_sale.dao.findFirst("select * from items_on_sale where location=? and iid=?",areaID,iid);
			if(ios.getInt("restNum")<num)
				{redirect("/404/error"); break;}
			Items item=Items.dao.findFirst("select * from items where iid=?",iid);
			record.set("iid", item.getInt("iid"));
			record.set("iname", item.getStr("iname"));
			record.set("icon", item.getStr("icon")); //+"-small"
			record.set("orderNum", num);
			record.set("price",new BigDecimal(ios.getBigDecimal("price").doubleValue()*num)); //item.getDouble("realPrice")*num
			totalMoney+=(ios.getBigDecimal("price").doubleValue()*num);
			itemList.add(record);
		}
		if(totalMoney<area.getBigDecimal("startPrice").doubleValue())
		{
			redirect("/404/error?Msg="+Util.getEncodeText("未满起送费，非法请求！"));
			return;
		}
		if(itemList.size()>3)
		    setAttr("itemList", itemList.subList(0, 3));
		else {
			setAttr("itemList", itemList);
		}
		setSessionAttr("itemList", itemList);
		setAttr("totalMoney", totalMoney);
		setSessionAttr("totalMoney", totalMoney);
		List<Promotion> proList=Promotion.dao.find("select * from promotion where isshow=true order by addedDT desc");
		setAttr("proList", proList);
		User user=getSessionAttr("sessionUser");
		setAttr("userTel", user.getStr("tel"));
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
		User user=getSessionAttr(GlobalVar.WXUSER);
		String date=Util.getDate();
		List<Record> cpList=Db.find("select a.money,b.cuid,b.endDate from coupons as a,coupons_user as b where b.owner=?  and b.used=0 and a.cid=b.cid and b.endDate>=?",user.getInt("uid"),date);
		setAttr("cpList", cpList);
		renderJson();
	}
	
	@Before(Tx.class)
	public void pay()
	{
		String date=Util.getDate();
		String time=Util.getTime();
	//	String tel=getPara("userTel");
		String name=getPara("userName");
		String room=getPara("userRoom");
		User user=getSessionAttr("sessionUser");
		if(name==null || room==null)
		{
			redirect("/404/error");
			return;
		}
		user.set("name", name).set("room",room).update();
		List<Record> itemList=getSessionAttr("itemList");
		double totalMoney=getSessionAttr("totalMoney");
		if(itemList==null || getSessionAttr("totalMoney")==null)
		{
			redirect("/404/error");
			return;
		}
		int areaID=getSessionAttr("areaID");
		Trades temptrade=Trades.dao.findFirst("select * from trades order by rid desc");
		int rid;
		if(temptrade==null)
			rid=1;
		else
			rid=temptrade.getInt("rid")+1;
		Managers manager=Managers.dao.findFirst("select * from managers where location=?",areaID);
		String cuid=getPara("cuid");
		if(cuid!=null)
		{
			
			Record coupons=Db.findFirst("select a.money,a.cid from coupons as a,coupons_user as b where b.cuid=? and b.used=0 and b.endDate>=? and a.cid=b.cid",cuid,date);
			if(cuid!=null)
			{
			  totalMoney-=coupons.getDouble("money");
			  Coupons_user.dao.findById(cuid).set("used", 1).update();
			  Coupons_use cu=new Coupons_use();
			  cu.set("rid", rid);
			  cu.set("cid",coupons.getInt("cid"));
			  cu.set("realpay", totalMoney);
			  cu.set("addedDate", date);
			  cu.set("addedTime", time);
			  cu.save();
			}
		}
		
		Areas area=Areas.dao.findById(areaID);
		Areas college=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",area.getStr("city"),area.getStr("college"),"");
		for(int i=0;i<itemList.size();i++)
		{
		  Trades trades=new Trades();
		  trades.set("rid", rid);
		  trades.set("customer", user.getInt("uid"));
		  trades.set("seller", manager.getInt("mid"));
		  trades.set("location", areaID);
		  trades.set("addedDate", date);
		  trades.set("addedTime", time);
		  trades.set("item", itemList.get(i).getInt("iid"));
		  trades.set("price", itemList.get(i).getBigDecimal("price"));
		  trades.set("orderNum", itemList.get(i).getInt("orderNum"));
		  trades.set("room", room);
		  trades.set("state", 0);  //0:正在派送  1:交易完成
		  trades.save();
		  
		  String month=Util.getMonth();
		  Areasales as=Areasales.dao.findFirst("select * from areasales where item=? and location=? and month=?",itemList.get(i).getInt("iid"),areaID,month);
		  if(as==null)
		  {
			  as=new Areasales();
			  as.set("item", itemList.get(i).getInt("iid")).set("num", itemList.get(i).getInt("orderNum"));
			  as.set("money", itemList.get(i).getBigDecimal("price")).set("location", areaID);
			  as.set("addedDT", new Timestamp(System.currentTimeMillis())).set("month", month);
			  as.save();
		  }else
			  as.set("num", as.getInt("num")+itemList.get(i).getInt("orderNum")).set("money", as.getBigDecimal("money").add(itemList.get(i).getBigDecimal("price"))).update();
		  
		  as=null;
		  as=Areasales.dao.findFirst("select * from areasales where item=? and location=? and month=?",itemList.get(i).getInt("iid"),college.getInt("aid"),month);
		  if(as==null)
		  {
			  as=new Areasales();
			  as.set("item", itemList.get(i).getInt("iid")).set("num", itemList.get(i).getInt("orderNum"));
			  as.set("money", itemList.get(i).getBigDecimal("price")).set("location", college.getInt("aid"));
			  as.set("addedDT", new Timestamp(System.currentTimeMillis())).set("month", month);
			  as.save();
		  }else
			  as.set("num", as.getInt("num")+itemList.get(i).getInt("orderNum")).set("money", as.getBigDecimal("money").add(itemList.get(i).getBigDecimal("price"))).update();
		  
		 as=null;
		 as=Areasales.dao.findFirst("select * from areasales where item=? and location=? and month=?",itemList.get(i).getInt("iid"),0,month);
		 if(as==null)
		  {
			  as=new Areasales();
			  as.set("item", itemList.get(i).getInt("iid")).set("num", itemList.get(i).getInt("orderNum"));
			  as.set("money", itemList.get(i).getBigDecimal("price")).set("location", 0);
			  as.set("addedDT", new Timestamp(System.currentTimeMillis())).set("month", month);
			  as.save();
		  }else
			  as.set("num", as.getInt("num")+itemList.get(i).getInt("orderNum")).set("money", as.getBigDecimal("money").add(itemList.get(i).getBigDecimal("price"))).update();
		}
		Incomes income=Incomes.dao.findFirst("select * from incomes where mid=?",manager.getInt("mid"));
		if(income==null)
		{
			income=new Incomes();
			income.set("mid", manager.getInt("mid")).set("sales", new BigDecimal(totalMoney));
			income.set("addedDT", new Timestamp(System.currentTimeMillis())).save();
		}else {
			income.set("sales", income.getBigDecimal("sales").add(new BigDecimal(totalMoney))).update();
		}
		
		Managers colleger=Managers.dao.findFirst("select mid from managers where location=?",college.getInt("aid"));
		income=null;
		income=Incomes.dao.findFirst("select * from incomes where mid=?",colleger.getInt("mid"));
		if(income==null)
		{
			income=new Incomes();
			income.set("mid", manager.getInt("mid")).set("sales", new BigDecimal(totalMoney));
			income.set("addedDT", new Timestamp(System.currentTimeMillis())).save();
		}else {
			income.set("sales", income.getBigDecimal("sales").add(new BigDecimal(totalMoney))).update();
		}
		income=Incomes.dao.findFirst("select * from incomes where mid=?",1);
		income.set("sales", income.getBigDecimal("sales").add(new BigDecimal(totalMoney))).update();
		removeSessionAttr("itemList");
		removeSessionAttr("Carts");
		removeAttr("totalMoney");
		redirect("/404/error?Msg="+Util.getEncodeText("支付成功！"));
	}

}
