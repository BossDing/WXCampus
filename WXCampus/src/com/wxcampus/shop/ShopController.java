package com.wxcampus.shop;

import java.io.IOException;
import java.io.InputStream;
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

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
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
import com.wxcampus.util.GeneralPost;
import com.wxcampus.util.Util;

/**
 * 微信端购物方面控制器类
 * @author Potato
 *
 */
@Before({OpenidInterceptor.class,UserInterceptor.class})
public class ShopController extends Controller{
	
	private Logger logger=Util.getLogger();
	
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
			item=Db.findFirst("select a.iid,a.iname,a.icon,b.restNum,b.price from items as a,items_on_sale as b where b.isonsale=true and a.iid=b.iid and b.location=? and a.iid=?",areaID,iid);
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
	@Before(ShopInterceptor.class)
	public void confirm()
	{
		boolean yn=getSessionAttr("ConfirmPayOnce");
		if(getSessionAttr("ConfirmPayOnce")!=null && yn==true)
		{
			redirect("/404/error?Msg="+Util.getEncodeText("网络繁忙,请稍后再试"));
			return;
		}
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
			Items_on_sale ios=Items_on_sale.dao.findFirst("select * from items_on_sale where isonsale=true and location=? and iid=?",areaID,iid);
			if(ios==null || ios.getInt("restNum")<num)
				{redirect("/404/error"); return;}
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
		setSessionAttr("ConfirmPayOnce", false);
		render("confirm.html");
	}
	
	@Before(ShopInterceptor.class)
	public void coupons() //ajax
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		String date=Util.getDate();
		List<Record> cpList=Db.find("select a.money,b.cuid,b.endDate from coupons as a,coupons_user as b where b.owner=?  and b.used=0 and a.cid=b.cid and b.endDate>=?",user.getInt("uid"),date);
		setAttr("cpList", cpList);
		renderJson();
	}
	@Before({ShopInterceptor.class,Tx.class})
	public void pay()
	{
		if(getSessionAttr("ConfirmPayOnce")==null)
			return;
		boolean yn=getSessionAttr("ConfirmPayOnce");
		if(yn)
			return;
		setSessionAttr("ConfirmPayOnce", true);
		String date=Util.getDate();
		String time=Util.getTime();
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
		String tn=Util.getTradeNo();
		Managers manager=Managers.dao.findFirst("select * from managers where location=?",areaID);
//		String cuid=getPara("cuid");
//		if(cuid!=null)
//		{
//			
//			Record coupons=Db.findFirst("select a.money,a.cid from coupons as a,coupons_user as b where b.cuid=? and b.used=0 and b.endDate>=? and a.cid=b.cid",cuid,date);
//			if(cuid!=null)
//			{
//			  totalMoney-=coupons.getDouble("money");
//			  Coupons_user.dao.findById(cuid).set("used", 1).update();
//			  Coupons_use cu=new Coupons_use();
//			  cu.set("rid", rid);
//			  cu.set("cid",coupons.getInt("cid"));
//			  cu.set("realpay", totalMoney);
//			  cu.set("addedDate", date);
//			  cu.set("addedTime", time);
//			  cu.save();
//			}
//		}	
	
		for(int i=0;i<itemList.size();i++)
		{
		  Trades trades=new Trades();
		  trades.set("rid", rid);
		  trades.set("tradeNo", tn);
		  trades.set("totalmoney", new BigDecimal(totalMoney));
		  trades.set("customer", user.getInt("uid"));
		  trades.set("seller", manager.getInt("mid"));
		  trades.set("location", areaID);
		  trades.set("addedDate", date);
		  trades.set("addedTime", time);
		  trades.set("item", itemList.get(i).getInt("iid"));
		  trades.set("price", itemList.get(i).getBigDecimal("price"));
		  trades.set("orderNum", itemList.get(i).getInt("orderNum"));
		  trades.set("room", room);
		  trades.set("state", 2);  //0:正在派送  1:交易完成  2: 等待支付
		  trades.save();
		}
		
		
		
		Document document=DocumentHelper.createDocument();
		Element root=document.addElement("xml");
		root.addElement("appid").setText(Util.APPID);
		root.addElement("mch_id").setText(Util.MCH_ID);
		root.addElement("device_info").setText("WEB");
		root.addElement("nonce_str").setText(Util.getRandomString());
		root.addElement("body").setText("零食");   //商品概述
		//root.addElement("detail").setText("");  //选填
		//root.addElement("attach").setText("");  //选填
		root.addElement("out_trade_no").setText(tn);  //订单号
		root.addElement("fee_type").setText("CNY");
		root.addElement("total_fee").setText(totalMoney+"");  //订单总金额
		root.addElement("spbill_create_ip").setText(getRequest().getRemoteAddr());  //终端IP
		root.addElement("time_start").setText(Util.getTimeStamp());     //订单起始时间
		root.addElement("time_expire").setText(Util.getEndTimeStamp());    //订单结束时间
		//root.addElement("goods_tag").setText("");
		root.addElement("notify_url").setText("http://www.missjzp.cn/shop/paysuccess");
		root.addElement("trade_type").setText("JSAPI");
	//	root.addElement("product_id").setText("");
	//	root.addElement("limit_pay").setText("");
		root.addElement("openid").setText(user.getStr("openid"));
		List<Element> elements=root.elements();
	    String sign=Util.getSign(root);
		root.addElement("sign").setText(sign); ////****************
		String resXML=GeneralPost.getResponseXML(document.asXML(),"https://api.mch.weixin.qq.com/pay/unifiedorder");
		
		String prepay_id="";
		Document document2;
		try {
			document2 = DocumentHelper.parseText(resXML);
			Element root2 = document2.getRootElement();
			List<Element> elements2=root2.elements();
			String sign2=Util.getSign(root2);
			if(!sign2.equals(root2.elementText("sign")))
			{
				redirect("/404/error");
				return;
			}
		    if(!root2.elementText("return_code").equals("SUCCESS") ||  !root2.elementText("result_code").equals("SUCCESS"))
		    {
		    	logger.error("订单号："+tn+" 错误信息："+root2.elementText("return_msg")+" 错误代码："+root2.elementText("err_code")+" 描述："+root2.elementText("err_code_des"));
		    	redirect("/404/error?Msg="+Util.getEncodeText("支付错误,请稍后再试"));
				return;
		    }
		    prepay_id=root2.elementText("prepay_id");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		removeSessionAttr("Carts");
		removeSessionAttr("itemList");
		removeSessionAttr("totalMoney");
		removeSessionAttr("ConfirmPayOnce");
		String tempts=System.currentTimeMillis()+"";
		String tempRs=Util.getRandomString();
		Document document3=DocumentHelper.createDocument();
		Element root3=document3.addElement("xml");
		root3.addElement("appId").setText(Util.APPID);
		root3.addElement("timeStamp").setText(tempts);
		root3.addElement("package").setText("prepay_id="+prepay_id);
		root3.addElement("nonceStr").setText(tempRs);
		root3.addElement("signType").setText("MD5");
		List<Element> elements3=root3.elements();
		String sign3=Util.getSign(root3);
		setAttr("appid", Util.APPID);
		setAttr("timestamp", tempts);
		setAttr("packages", "prepay_id="+prepay_id);
		setAttr("nonceStr", tempRs);
		setAttr("paySign", sign3);
		renderJson();
	}
	@Before(Tx.class)
	public void paysuccess()
	{
		InputStream inputStream=null;
		Document document=null;
		try {
			inputStream = getRequest().getInputStream();
		    // 读取输入流
			SAXReader reader = new SAXReader();
			 document= reader.read(inputStream);
			    // 得到xml根元素
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element root = document.getRootElement();
		List<Element> elements=root.elements();
		String sign=Util.getSign(root);
		if(!sign.equals(root.elementText("sign")))
		{
			logger.error(" 错误信息：非法签名");
			return;
		}
		 if(!root.elementText("return_code").equals("SUCCESS"))
		    {
		    	logger.error(" 错误信息："+root.elementText("return_msg"));
				return;
		    }
		 String tn=root.elementText("out_trade_no");
		 List<Trades> trades=Trades.dao.find("select * from trades where tradeNo=?",tn);
		 if(trades.get(0).getInt("state")!=2)
			 return;
		 int areaID=trades.get(0).getInt("location");
		 double totalMoney=trades.get(0).getBigDecimal("totalmoney").doubleValue();
		 if(totalMoney!=Double.parseDouble(root.elementText("total_fee")))
		 {
			 logger.error(" 错误信息： 金额与实际订单不符");
			 return;
		 }
		 Managers manager=Managers.dao.findFirst("select * from managers where location=?",areaID);
		 Areas area=Areas.dao.findById(areaID);
		 Areas college=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",area.getStr("city"),area.getStr("college"),"");
		 String month=Util.getMonth();
		 for(int i=0;i<trades.size();i++)
         {
			 trades.get(i).set("state", 0).set("wxtradeNo", root.elementText("transaction_id")).set("finishedTimeStamp", root.elementText("time_end")).update();  //支付成功
			 
			 Areasales as=Areasales.dao.findFirst("select * from areasales where item=? and location=? and month=?",trades.get(i).getInt("item"),areaID,month);
			  if(as==null)
			  {
				  as=new Areasales();
				  as.set("item", trades.get(i).getInt("item")).set("num", trades.get(i).getInt("orderNum"));
				  as.set("money", trades.get(i).getBigDecimal("price")).set("location", areaID);
				  as.set("addedDT", new Timestamp(System.currentTimeMillis())).set("month", month);
				  as.save();
			  }else
				  as.set("num", as.getInt("num")+trades.get(i).getInt("orderNum")).set("money", as.getBigDecimal("money").add(trades.get(i).getBigDecimal("price"))).update();
			  
			  as=null;
			  as=Areasales.dao.findFirst("select * from areasales where item=? and location=? and month=?",trades.get(i).getInt("item"),college.getInt("aid"),month);
			  if(as==null)
			  {
				  as=new Areasales();
				  as.set("item", trades.get(i).getInt("item")).set("num", trades.get(i).getInt("orderNum"));
				  as.set("money", trades.get(i).getBigDecimal("price")).set("location", college.getInt("aid"));
				  as.set("addedDT", new Timestamp(System.currentTimeMillis())).set("month", month);
				  as.save();
			  }else
				  as.set("num", as.getInt("num")+trades.get(i).getInt("orderNum")).set("money", as.getBigDecimal("money").add(trades.get(i).getBigDecimal("price"))).update();
			  
			 as=null;
			 as=Areasales.dao.findFirst("select * from areasales where item=? and location=? and month=?",trades.get(i).getInt("item"),0,month);
			 if(as==null)
			  {
				  as=new Areasales();
				  as.set("item", trades.get(i).getInt("item")).set("num", trades.get(i).getInt("orderNum"));
				  as.set("money", trades.get(i).getBigDecimal("price")).set("location", 0);
				  as.set("addedDT", new Timestamp(System.currentTimeMillis())).set("month", month);
				  as.save();
			  }else
				  as.set("num", as.getInt("num")+trades.get(i).getInt("orderNum")).set("money", as.getBigDecimal("money").add(trades.get(i).getBigDecimal("price"))).update();
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
			income.set("mid", colleger.getInt("mid")).set("sales", new BigDecimal(totalMoney));
			income.set("addedDT", new Timestamp(System.currentTimeMillis())).save();
		}else {
			income.set("sales", income.getBigDecimal("sales").add(new BigDecimal(totalMoney))).update();
		}
		income=Incomes.dao.findFirst("select * from incomes where mid=?",1);
		income.set("sales", income.getBigDecimal("sales").add(new BigDecimal(totalMoney))).update();
		
		
		Document resdoc=DocumentHelper.createDocument();
		Element resroot=resdoc.addElement("xml");
		resroot.addElement("return_code").setText("<![CDATA[SUCCESS]]>");
		resroot.addElement("return_msg").setText("<![CDATA[OK]]>");
		renderText(resdoc.asXML());
		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
