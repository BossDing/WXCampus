package com.wxcampus.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.items.Coupons_user;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.items.Trades;

/**
 * 微信端用户方面控制器类
 * @author Potato
 *
 */
@Before(UserInterceptor.class)
public class UserController extends Controller{
	
	public void index() {
		render("index.html");
	}
	
//	public void login()
//	{
//		
//	}
	public void trades()    //查看订单                *****可能需要分页查询*****
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		List<Trades> tradeList=Trades.dao.find("select * from trades where customer="+user.getInt("uid"));
		setAttr("tradeList", tradeList);
		render("trades.html");
	}
	
	public void coupons()   //查看优惠券
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		List<Record> cuList=Db.find("select a.money,b.endDate from coupons as a,coupons_user as b where b.owner="+user.getStr("uid")+" and a.cid=b.cid");
		setAttr("cuList", cuList);
		render("coupons.html");
	}
	public void addItemStar() //ajax添加收藏
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		int iid=getParaToInt("iid");
		String itemStar=user.getStr("itemsStar");
		itemStar+=(iid+";");
		user.set("itemsStar", itemStar).update();
        user=User.me.findById(user.get("uid"));
        removeSessionAttr(GlobalVar.WXUSER);
        setSessionAttr(GlobalVar.WXUSER, user);  //待测试是否需要更新session
        renderHtml("OK");
	}
	
	public void itemstar() //我的收藏
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		String items[]=user.getStr("itemsStar").split(";");
		int areaID=getSessionAttr("areaID");
		List<Items> itemList=new ArrayList<Items>();
		for(int i=0;i<items.length;i++)
		{
			if(items[i].equals("")) continue;
			Items item=Items.dao.findFirst("select * from items where iid="+items[i]);
			Items_on_sale items_on_sale=Items_on_sale.dao.findFirst("select * from items_on_sale where location="+areaID+" and iid="+items[i]);
			item.set("restNum", items_on_sale.getInt("restNum"));
			itemList.add(item);		
		}
		setAttr("itemList", itemList);
		render("itemStar.html");
	}
	public void advice()  //advice.html
	{
		
	}
	public void submitAdvice()  //提交投诉
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm:ss");
		getModel(Advices.class).set("uid", user.getInt("uid")).set("addedDate", sdf1.format(new Date().toString())).set("addedTime", sdf2.format(new Date().toString())).save();
		redirect("submit-success.html");
	}
	
	@Clear(UserInterceptor.class)
	public void registion()  //registion.html
	{		
	}
	
	@Clear(UserInterceptor.class)
	@Before(UserValidator.class)
	public void register()
	{
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm:ss");
		User form=getModel(User.class);
		//form.set("openid", openid);  //openid未加
		form.set("registerDate", sdf1.format(new Date().toString()));
		form.set("registerTime", sdf2.format(new Date().toString()));
		form.save();
		form=User.me.findFirst("select * from user where tel="+form.getStr("tel"));
	    setSessionAttr(GlobalVar.WXUSER, form);
		redirect("/index");	
	}

}
