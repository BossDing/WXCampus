package com.wxcampus.user;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.common.NoUrlPara;
import com.wxcampus.common.OpenidInterceptor;
import com.wxcampus.index.Areas;
import com.wxcampus.items.Applyfor;
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
@Before({OpenidInterceptor.class,UserInterceptor.class})
public class UserController extends Controller{
	
	private Logger logger=Util.getLogger();
	@Before(NoUrlPara.class)
	public void index() {
		
		User user=getSessionAttr(GlobalVar.WXUSER);
		setAttr("headicon", user.getStr("headicon"));
		setAttr("usertel", user.getStr("tel"));
		render("index.html");
	}
	
//	public void login()
//	{
//		
//	}
	public void trades()    //查看订单                *****可能需要分页查询*****
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		int page=getParaToInt(0);
		List<Trades> ridList;
		if(getPara("state")==null)
			ridList=Trades.dao.paginate(page, 10, "select distinct rid,state,addedDate,addedTime","from trades where state!=2 and customer=? order by addedDate desc,addedTime desc",user.getInt("uid")).getList();
		else{
			int state=getParaToInt("state");
			ridList=Trades.dao.paginate(page, 10, "select distinct rid,state,addedDate,addedTime","from trades where customer=? and state=? order by addedDate desc,addedTime desc",user.getInt("uid"),state).getList();
		}
		
		//List<Trades> ridList=Trades.dao.find("select distinct rid,state,addedDate,addedTime from trades where customer=? order by addedDate desc,addedTime desc",user.getInt("uid"));
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
		//List<Trades> tradeList=Trades.dao.find("select * from trades where customer=?",user.getInt("uid"));
		setAttr("tradeList", records);
		setAttr("page", page);
		render("trades.html");
		
		/******************************************/  //定时清楚状态2订单
		String time=Util.getTime();
		if(!time.startsWith("23") && !time.startsWith("00"))
		{
		List<Trades> delTrades=Trades.dao.find("select * from trades where state=2 and customer=?",user.getInt("uid"));
		String date=Util.getDate();
		for(int i=0;i<delTrades.size();i++)
		{
			
			if(delTrades.get(i).get("addedDate").toString().compareTo(date)<0)
				delTrades.get(i).delete();
		}
		}
	}
	
	public void spetrade()  //订单详情页
	{
		int rid=getParaToInt("rid");
		User user=getSessionAttr(GlobalVar.WXUSER);
		Trades trade=Trades.dao.findFirst("select * from trades where rid=?", rid);
		int areaID=trade.getInt("location");
		Areas area=Areas.dao.findById(areaID);
		String location=area.getStr("college")+area.getStr("building")+user.getStr("room");
		List<Record> itemsRecords=Db.find("select b.iname,b.icon,a.price,a.orderNum from trades as a,items as b where a.item=b.iid and a.rid=?",rid);
		//Record [] items=itemsRecords.toArray(new Record[itemsRecords.size()]);
		double money=0;
		int num=0;
		for(int k=0;k<itemsRecords.size();k++)
		{
			num+=itemsRecords.get(k).getInt("orderNum");
			money+=itemsRecords.get(k).getBigDecimal("price").doubleValue();
		}
		Record record=new Record();
		record.set("rid", rid);
		record.set("state", trade.getInt("state"));
		record.set("addedDate", trade.get("addedDate"));
		record.set("addedTime", trade.get("addedTime"));
		record.set("num", num).set("money", money);
		record.set("userTel", user.getStr("tel"));
		record.set("location",location);
		record.set("items", itemsRecords);
		setAttr("trades", record);
		render("trades_info.html");
	}
	
	public void coupons()   //查看优惠券
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		List<Record> cuList=Db.find("select a.money,b.endDate from coupons as a,coupons_user as b where b.owner=? and a.cid=b.cid",user.getInt("uid"));
		setAttr("cuList", cuList);
		render("coupons.html");
	}
	public void addItemStar() //ajax添加收藏
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		int iid=getParaToInt("iid");
		String itemStar=user.getStr("itemsStar");
		if(itemStar.contains(iid+";"))
		{
			renderHtml(Util.getJsonText("您已收藏该商品,不能重复收藏！"));
			return;
		}else {
		itemStar+=(iid+";");
		user.set("itemsStar", itemStar).update();
        //user=User.me.findById(user.getInt("uid"));
        //removeSessionAttr(GlobalVar.WXUSER);
        setSessionAttr(GlobalVar.WXUSER, user);  //待测试是否需要更新session
        renderHtml(Util.getJsonText("OK"));
		}
	}
	//ajax取消收藏
	public void removeItemStar()
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		int iid=getParaToInt("iid");
		String itemStar=user.getStr("itemsStar");
		if(!itemStar.contains(iid+";"))
		{
			renderHtml(Util.getJsonText("您未收藏该商品"));
			return;
		}else {
		itemStar=itemStar.replaceAll(iid+";", "");
		user.set("itemsStar", itemStar).update();
//        user=User.me.findById(user.getInt("uid"));
//        removeSessionAttr(GlobalVar.WXUSER);
        setSessionAttr(GlobalVar.WXUSER, user);  //待测试是否需要更新session
        renderHtml(Util.getJsonText("OK"));
		}
	}
	
	public void mySave()
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
		render("mySave.html");
	}
	public void itemstar() //我的收藏
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		String items[]=user.getStr("itemsStar").split(";");
		int areaID=getSessionAttr("areaID");
		List<Record> itemList=new ArrayList<Record>();
		for(int i=0;i<items.length;i++)
		{
			if(items[i].equals("")) continue;
			Record item=Db.findFirst("select a.iid,a.iname,a.icon,b.restNum,b.price from items as a,items_on_sale as b where b.isonsale=true and  a.iid=b.iid and b.location=? and a.iid=?",areaID,Integer.parseInt(items[i]));
			//Items_on_sale items_on_sale=Items_on_sale.dao.findFirst("select * from items_on_sale where location=? and iid=?",areaID,Integer.parseInt(items[i]));
			//item.set("restNum", items_on_sale.getInt("restNum"));
			itemList.add(item);		
		}
		setAttr("itemList", itemList);
		renderJson();
		//render("itemStar.html");
	}
	public void advice()  //advice.html
	{
		render("advice.html");
	}
	public void submitAdvice()  //提交投诉
	{
		User user=getSessionAttr(GlobalVar.WXUSER);
		int areaID=getSessionAttr("areaID");
		Advices advice=getModel(Advices.class);
		advice.set("type", Util.filterUserInputContent(advice.getStr("type")));
		advice.set("content", Util.filterUserInputContent(advice.getStr("content")));
		advice.set("location", areaID);
		advice.set("uid", user.getInt("uid")).set("addedDate", Util.getDate()).set("addedTime", Util.getTime()).save();
		this.renderHtml("<script>alert('提交成功');this.location.href='/usr';</script>");
	}
	
//	@Clear(UserInterceptor.class)
//	public void registion()  //registion.html
//	{
//		if(getSessionAttr(GlobalVar.WXUSER)!=null)
//			{redirect("/index");
//			return;
//			}
//		
//		render("registion.html");
//	}
	
//	@Clear(UserInterceptor.class)
//	@Before(UserValidator.class)
//	public void register()
//	{
//		if(getSessionAttr(GlobalVar.WXUSER)!=null)
//			redirect("/index");
//		User form=getModel(User.class);
//		//form.set("tel", Long.parseLong(getPara("user.tel"))).set("password", getPara("password"));
//		if(form==null)
//		{
//			redirect("/404/error");
//			return;
//		}
//		if(getSessionAttr(form.getStr("tel"))!=null)
//		{
//			String vCode=getPara("vcode");
//			if (!vCode.equals(getSessionAttr(form.getStr("tel")))) {
//				keepModel(User.class);
//				setAttr("TelMsg", "验证码输入错误");
//				return;
////				redirect("/404/error?Msg="+Util.getEncodeText("验证码输入错误")+"&backurl=/usr/registion");
////				return;
//			}
//		}else {
//			keepModel(User.class);
//			setAttr("TelMsg", "验证码超时,请重新获取");
//			return;
////			redirect("/404/error?Msg="+Util.getEncodeText("验证码超时,请重新获取")+"&backurl=/usr/registion");
////			return;
//		}
//		String openid=getSessionAttr(GlobalVar.OPENID);
//		form.set("openid", openid);  //openid未加
//		form.set("password", Util.filterUserInputContent(form.getStr("password")));
//		form.set("registerDate", Util.getDate());
//		form.set("registerTime", Util.getTime());
//		if(getSessionAttr("areaID")==null)
//			form.set("location", 1);
//		else
//		    form.set("location", getSessionAttr("areaID"));
//		form.set("headicon", getSessionAttr("headicon"));
//		//form.set("headicon", "/imgs/aaa.png");
//		form.save();
//		form=User.me.findFirst("select * from user where tel=?",form.getStr("tel"));
//	    setSessionAttr(GlobalVar.WXUSER, form);
//		redirect("/index");	
//	}
	
	/**
	 *  ajax请求手机验证码
	 */
	@Clear({UserInterceptor.class,OpenidInterceptor.class})
	public void vcode()
	{
		int type=0;
		if(getPara("type")!=null)
		    type=getParaToInt("type");  // 0 注册  1申请店长
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
		if(getPara("tel")!=null && !getPara("tel").equals(""))
		{
			if(type==0)
			{
			User user=User.me.findFirst("select * from user where tel=?",getPara("tel"));
			if(user!=null)
			{
				renderHtml(Util.getJsonText("当前手机号已被注册"));	
				return;
			}
			}
			if(SendMessageVcode.send(getPara("tel"), veryfiCode))
				{
				setSessionAttr(GlobalVar.VCODETIME, new Date().toString());
				System.out.println("VerifyCode:"+veryfiCode);	
				setSessionAttr(getPara("tel"), veryfiCode);
				VcodeWaitThread vwt=new VcodeWaitThread(this);
				Thread t=new Thread(vwt);
				t.start();
				renderHtml(Util.getJsonText("验证码发送成功，请查收！"));	
				return;
				}
			else
				{renderHtml(Util.getJsonText("验证码发送失败，请稍后再试！"));
				return;}
		}else
		{
			renderHtml(Util.getJsonText("手机号不能为空！"));	
			return;
		}
		}else {
			renderHtml(Util.getJsonText("两次验证码发送间隔须超过一分钟"));
			return;
		}
	}
	
	/**
	 *   申请当店长
	 */
	@Clear({UserInterceptor.class,OpenidInterceptor.class})
	public void wtsindex()
	{
		render("KTstore.html");
	}
	@Clear({UserInterceptor.class,OpenidInterceptor.class})
	public void wantosell()
	{
		//验证码防bot待加
		render("wantosell.html");
	}
	@Clear({UserInterceptor.class,OpenidInterceptor.class})
	public void wantosellAction()
	{
		Applyfor af=getModel(Applyfor.class);
		String vcode=getSessionAttr(af.getStr("tel"));
		if(vcode!=null)
		{
			String vCode=getPara("vcode");
			if (!vCode.equals(vcode)) {
				this.keepModel(Applyfor.class);
				setAttr("ErrorMsg", "验证码输入错误");
				render("wantosell.html");
//				redirect("/404/error?Msg="+Util.getEncodeText("验证码输入错误")+"&backurl=/usr/wantosell");
				return;
			}
		}else {
			this.keepModel(Applyfor.class);
			setAttr("ErrorMsg", "验证码超时,请重新获取");
			render("wantosell.html");
			//redirect("/404/error?Msg="+Util.getEncodeText("验证码超时,请重新获取")+"&backurl=/usr/wantosell");
			return;
		}
		af.set("addedDT", new Timestamp(System.currentTimeMillis()));
		af.set("state", 0);   //0 未处理 1已处理
		af.save();
		redirect("/usr/applysuccess");
		//renderHtml("<script>alert('提交成功');this.location.href='/usr/wantosell';</script>");
	}
	@Clear({UserInterceptor.class,OpenidInterceptor.class})
	public void applysuccess()
	{
		render("applysuccess.html");
	}

}
