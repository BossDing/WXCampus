package com.wxcampus.index;


import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.manage.Managers;
import com.wxcampus.user.User;

/**
 * 微信端主页控制器类
 */
public class IndexController extends Controller {
	
	IndexService isService=new IndexService();
	
	@Before({LoginInterceptor.class,LocationInterceptor.class})
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
			areas=Areas.dao.findFirst("select * from areas where city="+city+" and college="+college+" and building="+building);
			}else {
			areas=Areas.dao.findById(1);
			}
		}else {
			//user=User.me.findById(user.get("uid"));
			areas=Areas.dao.findFirst("select * from areas where aid="+user.getInt("location"));
		}
		if(areas!=null)
		{
			isService.updateShopState(areas);
			
			setAttr("Area", areas);  //定位学校楼栋
			removeSessionAttr("areaID");
			setSessionAttr("areaID", areas.getInt("aid"));
			
			//List<Advertisement> adList=Advertisement.dao.find("select * from advertisement order by aid desc limit 0,4");
			//setAttr("AdList", adList);   //广告图片
			
			Managers manager=Managers.dao.findFirst("select * from managers where location="+areas.getInt("aid"));
			setAttr("Manager", manager); // 店长信息
			
//			List<Items_on_sale> iosList=Items_on_sale.dao.find("select * from items_on_sale where location="+areas.getStr("aid"));
//			List<Items> itemList=new ArrayList<Items>();
//			for(int i=0;i<iosList.size();i++)
//			{
//				Items item=Items.dao.findFirst("select * from items where iid="+iosList.get(i).getStr("iid"));
//				item.set("restNum", iosList.get(i).getInt("restNum"));
//				itemList.add(item);
//			}
			//setAttr("itemList", itemList); //商品信息
		}
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
				List<Areas> areaList=Areas.dao.find("select * from areas where city="+city+" and college="+college);
				List<Record> recordList=new ArrayList<Record>();
				for(int i=0;i<areaList.size();i++)
				{
					isService.updateShopState(areaList.get(i));
					Record record=new Record();
					record.set("building", areaList.get(i).getStr("building"));
					record.set("state", areaList.get(i).getBoolean("state"));
					recordList.add(record);
				}
				setAttr("buildings", recordList);
				renderJson();
			}else {
				setAttr("colleges", Areas.dao.find("select distinct college from areas where city="+city));
				renderJson();;
			}
		}else {
			setAttr("cities", Areas.dao.find("select distinct city from areas"));
			renderJson();
		}
	}
	public void getItems()  //ajax请求商品信息
	{
		int aid=getSessionAttr("areaID");
		String category=getPara("category");
		List<Items_on_sale> iosList=Items_on_sale.dao.find("select * from items_on_sale where location="+aid);
		List<Items> itemList=new ArrayList<Items>();
		for(int i=0;i<iosList.size();i++)
		{
			Items item;
			if(category!=null)
			    item=Items.dao.findFirst("select * from items where iid="+iosList.get(i).getInt("iid")+" and category="+category);
			else
				 item=Items.dao.findFirst("select * from items where iid="+iosList.get(i).getInt("iid"));
			item.set("restNum", iosList.get(i).getInt("restNum"));
			itemList.add(item);
		}
		setAttr("itemList", itemList); //商品信息
		renderJson();
	}

	public void searchArea()
	{
		String college=getPara("q");
		Areas areas=Areas.dao.findFirst("select * from areas where college="+college);
		if(areas!=null)
			redirect("area.html?city="+areas.getStr("city")+"&college="+areas.getStr("college"));
	}
	public void searchItems() //ajax
	{
		String itemName=getPara("q");
		int areaID=getSessionAttr("areaID");
		List<Record> itemList=Db.find("select a.iid,a.iname,a.icon,a.originPrice,a.realPrice,b.restNum from items as a,items_on_sale as b where b.location="+areaID+" and a.iid=b.iid and a.iname regexp '.*"+itemName+".*'");
		setAttr("itemList", itemList);
		renderJson();
	}
}





