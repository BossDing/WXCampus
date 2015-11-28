package com.wxcampus.index;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.common.NoUrlPara;
import com.wxcampus.common.OpenidInterceptor;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.manage.Managers;
import com.wxcampus.user.User;
import com.wxcampus.util.GeneralGet;
import com.wxcampus.util.Util;

/**
 * 主页控制器
 */
public class IndexController extends Controller {
	
	//IndexService isService=new IndexService();
	
	@Clear(OpenidInterceptor.class)
	//GetOpenidInterceptor.class,
	@Before({NoUrlPara.class,LoginInterceptor.class,LocationInterceptor.class})
	public void index() {
		User user=getSessionAttr(GlobalVar.WXUSER);
		Areas areas;
		if(user==null)
		{
			String city=getPara("city");
			String college=getPara("college");
			String building=getPara("building");
			
			if(city!=null && college!=null && building!=null)
			{
			areas=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",city,college,building);
			}else {
			areas=Areas.dao.findById(1);
			}
		}else {
			//user=User.me.findById(user.get("uid"))
			areas=Areas.dao.findFirst("select * from areas where aid=?",user.getInt("location"));
		}
		if(areas!=null)
		{
			//isService.updateShopState(areas);
			
			setAttr("Area", areas);  //地区信息
			//removeSessionAttr("areaID");
			setSessionAttr("areaID", areas.getInt("aid"));
			
			List<Advertisement> adList=Advertisement.dao.find("select * from advertisement order by astid desc limit 0,4");
			setAttr("AdList", adList);   //广告图片信息
			
			Managers manager=Managers.dao.findFirst("select * from managers where location=?",areas.getInt("aid"));
			setAttr("Manager", manager); // 店长信息
			
			List<Items> category=Items.dao.find("select distinct category from items");
			setAttr("Category",category);
			
			HashMap<Integer, Integer> map=getSessionAttr("Carts");
			int totalnum=0;
			if(map!=null && !map.isEmpty())
			{
				Set<Integer> items=map.keySet();
				Iterator<Integer> iterator=items.iterator();
				while(iterator.hasNext())
				{
					int iid=iterator.next();
					totalnum+=map.get(iid);
				}
			}
			setAttr("TotalNum", totalnum);
//			List<Items_on_sale> iosList=Items_on_sale.dao.find("select * from items_on_sale where location=?",areas.getStr("aid"));
//			List<Items> itemList=new ArrayList<Items>();
//			for(int i=0;i<iosList.size();i++)
//			{
//				Items item=Items.dao.findFirst("select * from items where iid=?",iosList.get(i).getInt("iid"));
//				item.set("restNum", iosList.get(i).getInt("restNum"));
//				itemList.add(item);
//			}
			//setAttr("itemList", itemList); //商品信息
		}else {
			redirect("/404/error");
			return;
		}
		if(getSessionAttr("Carts")==null)
		    setSessionAttr("Carts", new HashMap<Integer,Integer>());
		render("index.html");
	}
	
	public void location()   //ajax
	{
		String city=getPara("city");
		if(city!=null)
		{
			String college=getPara("college");
			if(college!=null)
			{
				List<Areas> areaList=Areas.dao.find("select * from areas where city=? and college=? order by building",city,college);
				List<Record> recordList=new ArrayList<Record>();
				for(int i=0;i<areaList.size();i++)
				{
					//isService.updateShopState(areaList.get(i));
					Record record=new Record();
					record.set("building", areaList.get(i).getStr("building"));
					record.set("state", areaList.get(i).getBoolean("state"));
					recordList.add(record);
				}
				setAttr("buildings", recordList);
				renderJson();
			}else {
				setAttr("colleges", Areas.dao.find("select distinct college from areas where city=? order by college",city));
				renderJson();;
			}
		}else {
			setAttr("cities", Areas.dao.find("select distinct city from areas order by city"));
			renderJson();
		}
	}
	
	public void area()
	{
		render("area.html");
	}
	public void getItems()  //ajax获取商品信息
	{
		int aid=getSessionAttr("areaID");
		String category=getPara("category");
		List<Record> itemList;
		if(category!=null)
		{
			 itemList=Db.find("select a.iid,a.iname,a.icon,b.restNum,b.price from items as a,items_on_sale as b where a.iid=b.iid and b.location=? and a.category=?",aid,category);
		}else
		{
			 itemList=Db.find("select a.iid,a.iname,a.icon,a.category,b.restNum,b.price from items as a,items_on_sale as b where a.iid=b.iid and b.location=?");
		}
		setAttr("itemList", itemList); 
		renderJson();
	}

	public void searchArea()  //ajax
	{
		String college=getPara("q");
		List<Areas> areaList=Areas.dao.find("select distinct college from areas where college regexp ?",".*"+college+".*");
		if(areaList!=null)
			{
			  setAttr("colleges", areaList);
			  renderJson();
			}
		else
			renderHtml(Util.getJsonText("您要找的学校暂不存在"));
	}
	public void searchItems() //ajax
	{
		String itemName=getPara("q");
		int areaID=getSessionAttr("areaID");
		if(getSessionAttr("Carts")==null)
		    setSessionAttr("Carts", new HashMap<Integer,Integer>());
		List<Record> itemList=Db.find("select a.iid,a.iname,a.icon,b.restNum,b.price from items as a,items_on_sale as b where b.location=? and a.iid=b.iid and a.iname regexp ?",areaID,".*"+itemName+".*");
		setAttr("itemList", itemList);
		renderJson();
	}
	public void find()
	{
		if(getSessionAttr("Carts")==null)
		    setSessionAttr("Carts", new HashMap<Integer,Integer>());
		HashMap<Integer, Integer> map=getSessionAttr("Carts");
		int totalnum=0;
		if(map!=null && !map.isEmpty())
		{
			Set<Integer> items=map.keySet();
			Iterator<Integer> iterator=items.iterator();
			while(iterator.hasNext())
			{
				int iid=iterator.next();
				totalnum+=map.get(iid);
			}
		}
		setAttr("TotalNum", totalnum);
		render("find.html");
	}
	
	@Clear
	public void authorize()
	{
		//检测useragent
		String agent=getRequest().getHeader("User-Agent");
		if(!agent.contains("MicroMessenger"))
		{
			redirect("/404/error?Msg="+Util.getEncodeText("请使用微信客户端访问")+"&backurl=http://www.baidu.com");
			return;
		}
//		String referer=getRequest().getHeader("Referer");
//		if(referer==null || !referer.contains("www.domain.com"))
//		{
//			redirect("/404/error?Msg="+Util.getEncodeText("非法请求")+"&backurl=http://www.baidu.com");
//			return;
//		}
		String code=getPara("code");
		String state=getPara("state");
		if(code==null || state==null || !state.equals("6666"))
			{redirect("/404/error");
			return;}
		
		//请求openid        
		String jsonStr=GeneralGet.getResponse("https://api.weixin.qq.com/sns/oauth2/access_token?appid="+GetOpenidInterceptor.APPID+"&secret="+GetOpenidInterceptor.APPSECRET+"&code="+code+"&grant_type=authorization_code");
		JSONObject json=JSONObject.parseObject(jsonStr);
		String openid=json.getString("openid");
		setSessionAttr(GlobalVar.OPENID, openid);
		if(User.me.findFirst("select uid from user where openid=?", openid)==null)
		{
		String accesstoken=json.getString("access_token");
		JSONObject json2=JSONObject.parseObject(GeneralGet.getResponse("https://api.weixin.qq.com/sns/userinfo?access_token="+accesstoken+"&openid="+openid+"&lang=zh_CN"));
		setSessionAttr("headicon", json2.getString("headimgurl"));
		}
		
		redirect("/index");
	}
}





