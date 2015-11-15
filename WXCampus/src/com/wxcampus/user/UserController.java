package com.wxcampus.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.items.Coupons_user;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.items.Trades;
import com.wxcampus.util.Util;

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
		List<Record> cuList=Db.find("select a.money,b.endDate from coupons as a,coupons_user as b where b.owner="+user.getInt("uid")+" and a.cid=b.cid");
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
        user=User.me.findById(user.getInt("uid"));
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
			Items item=Items.dao.findFirst("select * from items where iid="+Integer.parseInt(items[i]));
			Items_on_sale items_on_sale=Items_on_sale.dao.findFirst("select * from items_on_sale where location="+areaID+" and iid="+Integer.parseInt(items[i]));
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
		getModel(Advices.class).set("uid", user.getInt("uid")).set("addedDate", Util.getDate()).set("addedTime", Util.getTime()).save();
		redirect("submit-success.html");
	}
	
	@Clear(UserInterceptor.class)
	public void registion()  //registion.html
	{
		if(getSessionAttr(GlobalVar.WXUSER)!=null)
			redirect("/index");
	}
	
	@Clear(UserInterceptor.class)
	@Before(UserValidator.class)
	public void register()
	{
		if(getSessionAttr(GlobalVar.WXUSER)!=null)
			redirect("/index");
		User form=getModel(User.class);
		if(form==null || form.getInt("tel")==null)
		{
			redirect("/index/error");
			return;
		}
		if(getSessionAttr(GlobalVar.VCODE)!=null)
		{
			String vCode=getPara("vcode");
			if (!vCode.equals(getSessionAttr(GlobalVar.VCODE))) {
				redirect("/index/error?Msg=验证码输入错误&backurl=/usr/registion");
				return;
			}
		}else {
			redirect("/index/error?Msg=验证码超时,请重新获取&backurl=/usr/registion");
			return;
		}
		//form.set("openid", openid);  //openid未加
		form.set("registerDate", Util.getDate());
		form.set("registerTime", Util.getTime());
		form.set("location", getSessionAttr("areaID"));
		form.save();
		form=User.me.findFirst("select * from user where tel="+form.getInt("tel"));
	    setSessionAttr(GlobalVar.WXUSER, form);
		redirect("/index");	
	}
	
	/**
	 *  ajax请求手机验证码
	 */
	@Clear(UserInterceptor.class)
	public void vcode()
	{
		String verifyStartTime=null;
		if(getSessionAttr(GlobalVar.VCODETIME)!=null)
		verifyStartTime=getSessionAttr(GlobalVar.VCODETIME).toString();
		Date startDate=null;
		if(verifyStartTime!=null)
		{
//			System.out.println(verifyStartTime);
		SimpleDateFormat sdf=new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy",Locale.US);
		
		try {
			startDate=sdf.parse(verifyStartTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		if(startDate.getMinutes()<59)
		    startDate.setMinutes(startDate.getMinutes()+1);
		else {
			startDate.setHours(startDate.getHours()+1);
			startDate.setMinutes(0);
		}
		}
		if(verifyStartTime==null || startDate.before(new Date()))
		{
		String veryfiCode=""+(new Random().nextInt(900000)+100000);
		if(getPara("tel")!=null)
		{
			if(SendMessageVcode.send(getPara("tel"), veryfiCode))
				{
				setSessionAttr(GlobalVar.VCODETIME, new Date().toString());
				System.out.println("VerifyCode:"+veryfiCode);	
				setSessionAttr(GlobalVar.VCODE, veryfiCode);
				VcodeWaitThread vwt=new VcodeWaitThread(this);
				Thread t=new Thread(vwt);
				t.start();
				renderHtml("验证码发送成功，请查收！");		
				}
			else
				renderHtml("验证码发送失败，请稍后再试！");
		}else
			redirect("/index/error");
		}else {
			renderHtml("两次验证码发送间隔须超过一分钟");
		}
	}
	

}
