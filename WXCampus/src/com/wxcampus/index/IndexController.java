package com.wxcampus.index;


import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.InvalidXPathException;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.QName;
import org.dom4j.Text;
import org.dom4j.Visitor;
import org.dom4j.XPath;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.common.NoUrlPara;
import com.wxcampus.common.OpenidInterceptor;
import com.wxcampus.items.Areasales;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.manage.Managers;
import com.wxcampus.user.User;
import com.wxcampus.util.GeneralGet;
import com.wxcampus.util.Util;

/**
 * 主页控制器
 */
@Before(OpenidInterceptor.class)
public class IndexController extends Controller {
	
	//IndexService isService=new IndexService();
	public static Logger logger = Util.getLogger();
	@Clear(OpenidInterceptor.class)
	//GetOpenidInterceptor.class,
	@Before({NoUrlPara.class,GetOpenidInterceptor.class,LoginInterceptor.class,LocationInterceptor.class})
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
			
			List<Advertisement> adList=Advertisement.dao.find("select * from advertisement order by addedDate desc,addedTime desc limit 0,4");
			setAttr("AdList", adList);   //广告图片信息
			
			Managers manager=Managers.dao.findFirst("select * from managers where location=?",areas.getInt("aid"));
			if(manager==null)
			{
				manager=new Managers();
				manager.set("say", "本店暂未开张,尚无商品");
			}
			
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
	
    @Clear
	public void location()   //ajax
	{
		String city=getPara("city");
		if(city!=null)
		{
			String college=getPara("college");
			if(college!=null)
			{
				List<Areas> areaList=Areas.dao.find("select * from areas where city=? and college=? and building<>? order by building",city,college,"");
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
				setAttr("colleges", Areas.dao.find("select distinct college from areas where city=? and college<>? order by college",city,""));
				renderJson();;
			}
		}else {
			setAttr("cities", Areas.dao.find("select distinct city from areas order by city"));
			renderJson();
		}
	}

	public void getLocation()
	{
		if(Util.ACCESSTOKEN==null || System.currentTimeMillis()>Util.ATEXPIRES_IN)
		{
			String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+Util.APPID+"&secret="+Util.APPSECRET;
			String jsonStr=GeneralGet.getResponse(url);
			JSONObject json=JSONObject.parseObject(jsonStr);
			Util.ACCESSTOKEN=json.getString("access_token");
			Util.ATEXPIRES_IN=System.currentTimeMillis()+Long.parseLong(json.getString("expires_in"))*1000;
		}
		if(Util.JSAPI_TICKET==null || System.currentTimeMillis()>Util.JTEXPIRES_IN)
		{
			String url="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+Util.ACCESSTOKEN+"&type=jsapi";
			String jsonStr=GeneralGet.getResponse(url);
			JSONObject json=JSONObject.parseObject(jsonStr);
			if(json.getIntValue("errcode")==0)
			{
			Util.JSAPI_TICKET=json.getString("ticket");
			Util.JTEXPIRES_IN=System.currentTimeMillis()+Long.parseLong(json.getString("expires_in"))*1000;
			}
		}
		String tempts=System.currentTimeMillis()/1000+"";
		String tempRs=Util.getRandomString();
		Document document4=DocumentHelper.createDocument();
		Element root4=document4.addElement("xml");
		root4.addElement("jsapi_ticket").setText(Util.JSAPI_TICKET);
		root4.addElement("noncestr").setText(tempRs);
		root4.addElement("url").setText("http://www.missjzp.cn/index/getLocation");
		root4.addElement("timestamp").setText(tempts);
		String sign3=Util.getJsSign(root4);
		System.out.println(sign3);
		setAttr("appid", Util.APPID);
		setAttr("timestamp", tempts);
		setAttr("noncestr", tempRs);
		setAttr("sign", sign3);
		render("getLocation.html");
	}
	@Clear
    public void getCity()
    {
		if(getPara("city")!=null)
		{
		String city=getPara("city");
		setAttr("city", city);
		setAttr("flag", 1);
		}else {
			setAttr("flag", 0);
		}
    	render("getCity.html");
    }
	public void area()
	{
//		if(getPara("latitude")==null || getPara("longitude")==null)
//		{
			if(getPara("city")!=null && getPara("college")!=null)
			{
				setAttr("college", getPara("college"));
				setAttr("city", getPara("city"));
				render("area.html");
			}
//		}else {
//		double laititude=Double.parseDouble(getPara("latitude"));
//		double longitude=Double.parseDouble(getPara("longitude"));
//		String ak="73EEtGNvP9eWPfDazNkGywfD";
//		String url="http://api.map.baidu.com/geocoder/v2/";
//		url=url+"?output=json&ak="+ak+"&coordtype=wgs84ll&location="+laititude+","+longitude+"&pois=1";
//		logger.error(url);
//		String jsonStr=GeneralGet.getResponse(url);
//		JSONObject json=JSONObject.parseObject(jsonStr);
//		if(json.getIntValue("status")==0)
//		{
//			JSONObject res=JSONObject.parseObject(json.getString("result"));
//			String college=res.getString("sematic_description");
//			JSONObject addressComponent=res.getJSONObject("addressComponent");
//			String city=addressComponent.getString("city").replace("市", "");
//			if(college.indexOf("校区")!=-1)
//			{
//				int index=college.indexOf("内");
//				if(index!=-1)
//				   college=college.substring(0,index);
//				else {
//				   college=college.substring(0,college.indexOf("校区")+2);
//				}
//			}else {
//				JSONArray pois=res.getJSONArray("pois");
//				for(int i=0;i<pois.size();i++)
//				{
//					JSONObject temp=pois.getJSONObject(i);
//					if(temp.getString("addr").endsWith("校区"))
//					{
//						college=temp.getString("addr");
//						List<Areas> colleges=Areas.dao.find("select * from areas where city=?",city);
//						for(int k=0;k<colleges.size();k++)
//						{
//							if(college.contains(colleges.get(k).getStr("college")))
//							{
//								college=colleges.get(k).getStr("college");
//								break;
//							}
//						}
//						break;
//					}
//				}
//			}
//			Areas areaCollege=Areas.dao.findFirst("select * from areas where college=?",college);
//			if(areaCollege!=null)
//			{
//				setAttr("city", city);
//				setAttr("college", college);
//				render("area.html");
//			}else
//			{
//				redirect("/index/getCity");
//			}
//			logger.error("Address:--------------"+res.getString("sematic_description"));
//			logger.error("College:--------------"+college);
//		}else {
//			logger.error("errorcode:--------------"+json.getIntValue("status"));
//			redirect("/index/getCity");
//		}
//	}
		
}
	public void getItems()  //ajax获取商品信息
	{
		int aid=getSessionAttr("areaID");
		String category=getPara("category");
		List<Record> itemList;
		Random random=new Random();
		if(category!=null)
		{
			// int day=Util.getDay();
			String month=Util.getMonth();
			itemList=Db.find("select a.iid,a.iname,a.icon,b.restNum,b.price from items as a,items_on_sale as b where b.isonsale=true and a.iid=b.iid and b.location=? and a.category=?",aid,category);
			for(int i=0;i<itemList.size();i++)
			{
				Areasales as=Areasales.dao.findFirst("select * from areasales where item=? and location=? and month=?",itemList.get(i).getInt("item"),aid,month);
				if(as!=null)
				   itemList.get(i).set("sales", as.getInt("num"));
				else {
					itemList.get(i).set("sales",0);
				}
			} 
		}else
		{
			 itemList=Db.find("select a.iid,a.iname,a.icon,a.category,b.restNum,b.price from items as a,items_on_sale as b where b.isonsale=true and a.iid=b.iid and b.location=?");
		}
		setAttr("itemList", itemList); 
		renderJson();
	}

	public void searchArea()  //ajax
	{
		String college=getPara("q");
		List<Areas> areaList=Areas.dao.find("select distinct college,city from areas where college regexp ?",".*"+college+".*");
		if(areaList!=null)
			{
			  setAttr("colleges", areaList);
			  renderJson();
			}
		else
			renderHtml(Util.getJsonText("您要找的学校暂不存在"));
	}
	public void searchCity()  //ajax
	{
		String city=getPara("q");
		List<Areas> areaList=Areas.dao.find("select distinct city from areas where city regexp ?",".*"+city+".*");
		if(areaList!=null)
			{
			  setAttr("cities", areaList);
			  renderJson();
			}
		else
			renderHtml(Util.getJsonText("您要找的城市暂不存在"));
	}
	public void searchItems() //ajax
	{
		String itemName=getPara("q");
		int areaID=getSessionAttr("areaID");
		if(getSessionAttr("Carts")==null)
		    setSessionAttr("Carts", new HashMap<Integer,Integer>());
		List<Record> itemList=Db.find("select a.iid,a.iname,a.icon,b.restNum,b.price from items as a,items_on_sale as b where b.isonsale=true and b.location=? and a.iid=b.iid and a.iname regexp ?",areaID,".*"+itemName+".*");
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
		//logger.error(jsonStr);
		String openid=json.getString("openid");
		setSessionAttr(GlobalVar.OPENID, openid);
		User user=User.me.findFirst("select uid from user where openid=?", openid);
		if (user == null) {
			user = new User();
			user.set("tel", "").set("password", "").set("headicon", "");
			user.set("openid", openid);
			user.set("registerDate", Util.getDate()).set("registerTime",
					Util.getTime());
			user.save();
			//logger.error("enter");
		}
		//logger.error("headicon---------"+user.getStr("headicon"));
		//user=User.me.findFirst("select uid from user where openid=?", openid);
		if(user.getStr("headicon")==null || user.getStr("headicon").equals(""))
		{
			String accesstoken = json.getString("access_token");
			JSONObject json2 = JSONObject
					.parseObject(GeneralGet
							.getResponse("https://api.weixin.qq.com/sns/userinfo?access_token="
									+ accesstoken
									+ "&openid="
									+ openid
									+ "&lang=zh_CN"));
			user.set("headicon",json2.getString("headimgurl")).update();
		}
		//setSessionAttr("headicon", json2.getString("headimgurl"));	
		redirect("/index");
	}
}





