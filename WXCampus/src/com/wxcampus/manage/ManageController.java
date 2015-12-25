package com.wxcampus.manage;

import java.awt.geom.Area;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.sound.midi.MidiDevice.Info;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxConfig;
import com.jfinal.upload.UploadFile;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.common.NoUrlPara;
import com.wxcampus.common.OpenidInterceptor;
import com.wxcampus.index.Advertisement;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
import com.wxcampus.items.Applyfor;
import com.wxcampus.items.Applyincome;
import com.wxcampus.items.Incomes;
import com.wxcampus.items.Informs;
import com.wxcampus.items.Ingoods;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.items.Promotion;
import com.wxcampus.items.Settings;
import com.wxcampus.items.Trades;
import com.wxcampus.user.Advices;
import com.wxcampus.util.Util;

/**
 * 后台管理控制器类
 * @author Potato
 *
 */
@Before(ManageInterceptor.class)
public class ManageController extends Controller{

	 public static Logger logger = Util.getLogger();
	 private final double lowLimit=1.2;
	 private final double highLimit=2.0;
	 //@Before(NoUrlPara.class)
	 public void index()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 if(manager.getInt("ring")==2)
		 {
			 Ring2Service ring2Service=new Ring2Service(this,manager);
	         ring2Service.trades();
	         setAttr("ring", 2);
	         setAttr("state", Areas.dao.findById(manager.getInt("location")).getBoolean("state"));
		     setAttr("alipayNo", manager.getStr("alipayNo"));
		 }else if(manager.getInt("ring")==1)
		 {
			 Ring1Service ring1Service=new Ring1Service(this,manager);
	         ring1Service.trades();
	         setAttr("ring", 1);
	         setAttr("alipayNo", manager.getStr("alipayNo"));
		 }else if(manager.getInt("ring")==0)
		 {
			 tradesALL();
	         setAttr("ring", 0);
		 }
		 Incomes income=Incomes.dao.findFirst("select * from incomes where mid=?",manager.getInt("mid"));
		 setAttr("Sales", income.getBigDecimal("sales").doubleValue());
		 switch (manager.getInt("ring")) {
		case 1:
			setAttr("Income", income.getBigDecimal("sales").doubleValue()*0.03);
			break;
		case 2:
			setAttr("Income", income.getBigDecimal("sales").doubleValue()*0.2);
			break;
		default:
			break;
		}
		 render("index.html");
	 }
	 
	 @Clear(ManageInterceptor.class)
	 public void login()   //login.html
	 {
		 if(getSessionAttr(GlobalVar.BEUSER)!=null)
			 {redirect("/mgradmin");
			 return;}
		 
		 render("login.html");
	 }
	 
	 @Clear(ManageInterceptor.class)
	 public void loginCheck()
	 {
		 if(getSessionAttr(GlobalVar.BEUSER)!=null)
		 {redirect("/mgradmin");
		 return;}   //已登录就跳转
		 
		 //防暴力检测
		 if(ManageLoginSafe.isExist(""+getPara("managers.tel")))
	        {
			   redirect("/mgradmin/error?Msg="+Util.getEncodeText("密码输入错误次数过多，请十分钟后再试！"));
			   return;
	        }else{      
		 Managers form=getModel(Managers.class);
		 Managers manager=Managers.dao.findFirst("select * from managers where tel=?",form.getStr("tel"));
		 if(manager==null)
		 {
			setAttr("errorMsg", "用户名或密码错误！");
			keepModel(Managers.class);
			render("login.html");
			return;
		 }
		 if(form.getStr("password").equals(manager.getStr("password")))
		 {
			 setSessionAttr(GlobalVar.BEUSER, manager);
			 logger.info(manager.getStr("name")+"---登录后台");
			 redirect("/mgradmin");
			 return;
		 }else {
				if(getSessionAttr(manager.getStr("tel"))!=null)
				{
				int left=getSessionAttr(manager.getStr("tel"));
				left--;
				if(left==0)
				{
					loginSafe ls=new loginSafe(manager.getStr("tel"),this);
					Thread t=new Thread(ls);
					t.start();
				}else
				setSessionAttr(manager.getStr("tel"), left);
				}
				else
				setSessionAttr(manager.getStr("tel"),5);
			setAttr("errorMsg", "用户名或密码错误！");
			keepModel(Managers.class);
			render("login.html");
		}
		 
	        }
	 }
	 /**
	  *  退出登录
	  */
	 public void quit()
	 {
		 removeSessionAttr(GlobalVar.BEUSER);
		 redirect("/mgradmin/login");
	 }
	 /**
	  *          修改密码
	  */
	 public void modifypw()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 switch (manager.getInt("ring")) {
		case 1:  setAttr("ring", 1);
			break;
		case 2:  setAttr("ring", 2);
			break;
		case 0:  setAttr("ring", 0);
		break;
		}
		 render("change_password.html");
	 }
	 public void modifyPwA()  //ajax
	 {
		 String oldPass=getPara("oldPass");
		 String newPass1=getPara("newPass1");
		 String newPass2=getPara("newPass2");
		 if(!newPass1.equals(newPass2))
		 {
			 renderHtml("<script>alert('两次密码输入不一致!');window.location='/mgradmin/modifypw';</script>");
			 return;
		 }
		 if(oldPass.equals(newPass1))
		 {
			 renderHtml("<script>alert('原始密码与新密码不能相同');window.location='/mgradmin/modifypw';</script>");
			 return;
		 }
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 if(oldPass.equals(manager.getStr("password")))
		 {
			 manager.set("password", Util.filterUserInputContent(newPass1)).update();
			 logger.info(manager.getStr("name")+"---修改密码");
			 renderHtml("<script>alert('修改成功!');window.location='/mgradmin/quit';</script>");
		 }else {
			renderHtml("<script>alert('原始密码输入错误!');window.location='/mgradmin/modifypw';</script>");
		}
	 }
	 
	 /**
	  *  查询订单
	  */
	 public void trades()  //ajax
	 {
		 
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		// logger.info(manager.getStr("name")+"---查看订单");
		 switch (manager.getInt("ring")) {
		case 0:     Ring0Service ring0Service=new Ring0Service(this, manager);
			        ring0Service.trades();
			break;
		case 1:    Ring1Service ring1Service=new Ring1Service(this, manager);
                   ring1Service.trades();
			break;
		case 2:   Ring2Service ring2Service=new Ring2Service(this,manager);
		          ring2Service.trades();
		          break;
		default:
			break;
		}		
	 }
	 /**
	  *  店长设置支付宝帐号
	  */
	 public void setAlipayCard()
	 {
		 String alipayNo=getPara("alipayNo");
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 manager.set("alipayNo", alipayNo).update();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *   申请提现
	  */
	 public void applyIncome()
	 {
		 String weekday=Util.getWeekday();
		 if(!weekday.equals("Tue"))
		 {
			 renderHtml(Util.getJsonText("每周二才能申请提现哦"));
			 return;
		 }
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 if(manager.getStr("alipayNo")==null)
		 {
			 renderHtml(Util.getJsonText("尚未设置提现支付宝帐号，请先设置"));
			 return;
		 }
		 Applyincome api=Applyincome.dao.findFirst("select * from applyincome where tel=? order by addedDT desc",manager.getStr("tel"));
		 if(api!=null && api.getInt("state")==0)
		 {
			 renderHtml(Util.getJsonText("您已提交过提现申请,请不要重复提交"));
			 return;
		 }
		 Incomes income=Incomes.dao.findFirst("select * from incomes where mid=?",manager.getInt("mid"));
		 if(income.getBigDecimal("sales").doubleValue()==0)
		 {
			 renderHtml(Util.getJsonText("您当前收入为0,无法提交"));
			 return;
		 }
		 Applyincome ai=new Applyincome();
		 ai.set("name", manager.getStr("name")).set("tel", manager.getStr("tel"));
		 ai.set("cardNo", manager.getStr("alipayNo"));
		 ai.set("state", 0).set("addedDT", new Timestamp(System.currentTimeMillis()));
		 ai.save();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  数据统计
	  */
	 public void datainfo()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 switch (manager.getInt("ring")) {
		case 1:  setAttr("ring", 1);
			break;
		case 2:  setAttr("ring", 2);
			break;
		 case 0:  setAttr("ring", 0);
			break;
		}
	
		String startDate = getPara("sdate");
		String endDate = getPara("edate");
		 if(startDate==null || endDate==null)
		 {
			 startDate=Util.getDate();
			 endDate=Util.getDate();
		 }
		List<Record> toshow = new ArrayList<Record>();
		List<Items> items = Items.dao.find("select iid from items");
		double totalsales=0;
	    if(manager.getInt("ring")==2)
	    {
	    	
		for (int i = 0; i < items.size(); i++) {
			int iid = items.get(i).getInt("iid");

			Record temp = Db
					.findFirst(
							"select sum(orderNum) as sum_orderNum,sum(price) as sum_price from trades where location=? and addedDate>=? and addedDate<=? and item=? and state=?",
							manager.getInt("location"), startDate, endDate, iid,1);
            
			if (temp.getBigDecimal("sum_orderNum")!=null) {
				//logger.error(Items.dao.findById(iid).getStr("iname")+"---"+temp.getInt("sum_orderNum")+"---"+temp.getBigDecimal("sum_price"));
				Record record = new Record();
				record.set("iname", Items.dao.findById(iid).getStr("iname"));
				record.set("num", temp.getBigDecimal("sum_orderNum"));
				record.set("money", temp.getBigDecimal("sum_price"));
				totalsales+=temp.getBigDecimal("sum_price").doubleValue();
				toshow.add(record);
			}
		}
	    }else if (manager.getInt("ring")==1) {
			Areas college=Areas.dao.findById(manager.getInt("location"));
			for (int i = 0; i < items.size(); i++) {
				int iid = items.get(i).getInt("iid");

				Record temp = Db
						.findFirst(
								"select sum(orderNum) as sum_orderNum,sum(price) as sum_price from trades where state=? and addedDate>=? and addedDate<=? and item=? and location in (select aid from areas where city='"+college.getStr("city")+"' and college='"+college.getStr("college")+"' and building!='')",
								 1,startDate, endDate, iid);

				if (temp.getBigDecimal("sum_orderNum")!=null) {
					Record record = new Record();
					record.set("iname", Items.dao.findById(iid).getStr("iname"));
					record.set("num", temp.getBigDecimal("sum_orderNum"));
					record.set("money", temp.getBigDecimal("sum_price"));
					totalsales+=temp.getBigDecimal("sum_price").doubleValue();
					toshow.add(record);
				}
			}
	    
		}else if (manager.getInt("ring")==0) {
			
			for (int i = 0; i < items.size(); i++) {
				int iid = items.get(i).getInt("iid");

				Record temp = Db
						.findFirst(
								"select sum(orderNum) as sum_orderNum,sum(price) as sum_price from trades where addedDate>=? and addedDate<=? and item=? and state=?",
								 startDate, endDate, iid,1);

				if (temp.getBigDecimal("sum_orderNum")!=null) {
					Record record = new Record();
					record.set("iname", Items.dao.findById(iid).getStr("iname"));
					record.set("num", temp.getBigDecimal("sum_orderNum"));
					record.set("money", temp.getBigDecimal("sum_price"));
					totalsales+=temp.getBigDecimal("sum_price").doubleValue();
					toshow.add(record);
				}
			}
		}
	   // logger.error("Size-----"+toshow.size());
	    setAttr("dataList", toshow);
	    setAttr("sdate", startDate);
	    setAttr("edate", endDate);
	    setAttr("TotalSales", totalsales);
	    render("datainfo.html");
	    
	    
//		 String month=getPara("month");
//		 if(month==null)
//			 month=Util.getMonth();
//		 List<Record> records=Db.find("select a.iname,b.num,b.money from items as a,areasales as b where a.iid=b.item and b.location=? and b.month=?",manager.getInt("location"),month);
//		 setAttr("dataList", records);
//		 setAttr("date_info", month);
//		 render("datainfo.html");	
	 }
	 /**
	  *  修改店铺状态
	  */
	 public void setShopState()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 boolean state=getParaToBoolean("state");  // 0关闭  1开启
		 Areas area;
		 if(getPara("aid")!=null)
		 {
			 System.out.println(111111);
			 area=Areas.dao.findById(getParaToInt("aid"));
		 }else {
			 area=Areas.dao.findById(manager.getInt("location"));
		}	
		 area.set("state", state).update();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *   上下架商品
	  */
	 public void setisonsale()
	 {
		 int iosid=getParaToInt("iosid");
		 int type=getParaToInt("type");  // 0 下架 1 上架
		 if(type==0)
		 Items_on_sale.dao.findById(iosid).set("isonsale", false).update();
		 else if(type==1)
			 Items_on_sale.dao.findById(iosid).set("isonsale", true).update();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *   设定营业时间
	  */
	 public void setSellingTime()  //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 switch (manager.getInt("ring")) {
		case 0:     Ring0Service ring0Service=new Ring0Service(this, manager);
			        ring0Service.setSellingTime();
			break;
			
		case 2:   Ring2Service ring2Service=new Ring2Service(this,manager);
		          ring2Service.setSellingTime();
			break;
		}	
	 }
	 /**
	  *  店长设置公告
	  */
	 public void setSay()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 String say=getPara("say");
		 if(say==null)
		 {
			 return;
		 }
		 manager.set("say", say).update();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  店长查看商品存量
	  */
	 public void itemnum()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 List<Record> iosList=null;
		 switch (manager.getInt("ring")) {
		case 1:  setAttr("ring", 1);
		  if(getParaToInt("mid")==null)
		  {
			  redirect("/mgradmin/error");
			  return;
		  }
		    int mid=getParaToInt("mid");
		    Areas building=Areas.dao.findById(Managers.dao.findById(mid).getInt("location"));
		    Areas college=Areas.dao.findById(manager.getInt("location"));
		    if(!building.getStr("city").equals(college.getStr("city")) || !building.getStr("college").equals(college.getStr("college")))
		    {
		    	redirect("/mgradmin/error?Msg="+Util.getEncodeText("无权访问"));
				  return;
		    }
		    iosList=Db.find("select a.iname,a.icon,a.category,b.iosid,b.restNum,b.price,b.minPrice,b.maxPrice,b.isonsale from items as a,items_on_sale as b where a.iid=b.iid and b.location=?",building.getInt("aid"));
			setAttr("iosList", iosList);
			setAttr("startPrice",building.getBigDecimal("startPrice").doubleValue());
			break;
		case 2:  setAttr("ring", 2);
		 iosList=Db.find("select a.iname,a.icon,a.category,b.iosid,b.restNum,b.price,b.minPrice,b.maxPrice,b.isonsale from items as a,items_on_sale as b where a.iid=b.iid and b.location=?",manager.getInt("location"));
		 setAttr("iosList", iosList);
		 Areas area=Areas.dao.findById(manager.getInt("location"));
		 setAttr("startPrice",area.getBigDecimal("startPrice").doubleValue());
		 setAttr("say", manager.getStr("say"));
		 setAttr("stime", area.get("startTime").toString().substring(0, 5));
		 setAttr("etime", area.get("endTime").toString().substring(0, 5));
			break;
		case 0:
			setAttr("ring", 0);
			 iosList=Db.find("select a.iname,a.icon,a.category,b.iosid,b.restNum,b.price,b.minPrice,b.maxPrice,b.isonsale from items as a,items_on_sale as b where a.iid=b.iid and b.location=?",manager.getInt("location"));
			 setAttr("iosList", iosList);
			break;
		}
		 render("itemnum.html");
		
	 }
	 /**
	  *  店长一定范围内设置售价
	  */
	 public void setPrice()
	 {
		 int iosid=getParaToInt("iosid");
		 double price=Double.parseDouble(getPara("price"));
		 Items_on_sale ios=Items_on_sale.dao.findById(iosid);
		 if(price>=ios.getBigDecimal("minPrice").doubleValue() && price<=ios.getBigDecimal("maxPrice").doubleValue())
		 {
			 ios.set("price", new BigDecimal(price)).update();
			 renderHtml(Util.getJsonText("OK"));
		 }else {
			renderHtml(Util.getJsonText("所设置价格越界"));
		}
	 }
	 /**
	  *  店长设置起送费
	  */
	 public void setStartPrice() //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 double price=Double.parseDouble(getPara("price"));
		 Areas area=Areas.dao.findById(manager.getInt("location"));
		 area.set("startPrice", new BigDecimal(price)).update();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  店长确认订单
	  */
	 public void confirmTrade()
		{
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 Ring2Service ring2Service=new Ring2Service(this,manager);
         ring2Service.confirmTrade();
		}
	 /**
	  *  店长，校区负责人确认收到进货
	  */
	 @Before(Tx.class)
	 public void confirmIngoods()   //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 int rid=getParaToInt("rid");
		 List<Ingoods> igList=Ingoods.dao.find("select * from ingoods where rid=?",rid);
		 int t=igList.get(0).getInt("froms");
		 if(igList!=null && t!=manager.getInt("mid"))
		 {
			 redirect("/mgradmin/error");
			 return;
		 }
		 if(igList!=null && igList.get(0).getInt("state")!=2)
		 {
			 redirect("/mgradmin/error");
			 return;
		 }
		 //Managers to=Managers.dao.findById(igList.get(0).getInt("tos"));
		 for(int i=0;i<igList.size();i++)
		 {
			 igList.get(i).set("state", 3).update();  //3 已完成	 
//			 Items_on_sale iosto=Items_on_sale.dao.findFirst("select * from items_on_sale where location=? and iid=?",to.getInt("location"),igList.get(i).getInt("item"));
//             if(iosto==null)
//             {
//    			 redirect("/mgradmin/error");
//    			 return;
//             }
//             if(iosto.getInt("restNum")<igList.get(i).getInt("num"))
//             {
//            	 return;
//             }else
//             iosto.set("restNum", iosto.getInt("restNum")-igList.get(i).getInt("num")).update();
             
             
			 Items_on_sale ios=Items_on_sale.dao.findFirst("select * from items_on_sale where location=? and iid=?",manager.getInt("location"),igList.get(i).getInt("item"));
			 if(ios==null)
			 {
				 Items item=Items.dao.findById(igList.get(i).getInt("item"));
				 BigDecimal cost=item.getBigDecimal("cost");
				 BigDecimal price=item.getBigDecimal("realPrice");
				 ios=new Items_on_sale();
				 ios.set("iid", igList.get(i).getInt("item"));
				 ios.set("minPrice", new BigDecimal(cost.doubleValue()*lowLimit)).set("maxPrice", new BigDecimal(cost.doubleValue()*highLimit));
				 ios.set("price", price).set("isonsale", true);
				 ios.set("restNum", igList.get(i).getInt("num")).set("location", manager.getInt("location"));
				 ios.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
				 ios.save();
			 }else {
				ios.set("restNum", ios.getInt("restNum")+igList.get(i).getInt("num")).update();
			}
		 }
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  店长,校区负责人查看提交的进货订单
	  */
	 public void seeInGoods()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 switch (manager.getInt("ring")) {
		case 1:  setAttr("ring", 1);
			break;
		case 2:  setAttr("ring", 2);
			break;
		}
		 int page=1;
		 if(getParaToInt(0)!=null)
			 page=getParaToInt(0);
		// System.out.println(page);
		 Page<Ingoods> pages=Ingoods.dao.paginate(page,10,"select distinct rid,addedDT,state","from ingoods where froms=? order by addedDT desc",manager.getInt("mid"));
		 List<Ingoods> igList=null;
		 if(pages!=null)
			 igList=pages.getList();
		 setAttr("igList", igList);
		 List<Informs> informs=Informs.dao.find("select * from informs where tos=?",manager.getInt("mid"));
		 for(int i=0;i<informs.size();i++)
			 informs.get(i).delete();
		 setAttr("page", page);
		 render("ingoods.html");
	 }
	 /**
	  *  管理,校区负责人处理提交的进货订单
	  */
	 public void dealInGoods()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 switch (manager.getInt("ring")) {
			case 1:  setAttr("ring", 1);
				break;
			case 0:  setAttr("ring", 0);
				break;
			}
		 if(manager.getInt("ring")!=0 &&manager.getInt("ring")!=1)
		 {
			 redirect("/mgradmin/error?Msg="+Util.getEncodeText("无权操作"));
			 return;
		 }
		 int page=1;
		 if(getParaToInt(0)!=null)
			 page=getParaToInt(0);
         List<Record> igList=Db.paginate(page,10,"select distinct rid,addedDT,state,froms","from ingoods where tos=? order by addedDT desc",manager.getInt("mid")).getList();
         for(int i=0;i<igList.size();i++)
         {
        	 igList.get(i).set("building", Areas.dao.findById(Managers.dao.findById(igList.get(i).getInt("froms")).getInt("location")).getStr("college"));
         }
		 setAttr("igList", igList);
		 List<Informs> informs=Informs.dao.find("select * from informs where tos=?",manager.getInt("mid"));
		 for(int i=0;i<informs.size();i++)
			 informs.get(i).delete();
		 setAttr("page", page);
		 render("dealIngoods.html");
	 }
	 /**
	  *   进货订单详情
	  */
	 public void igdetails()
	 {
		 int type=0;
		 if(getPara("type")!=null)
			type=getParaToInt("type"); 
		 int rid=getParaToInt("rid");
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 switch (manager.getInt("ring")) {
		case 1:  setAttr("ring", 1);
			break;
		case 2:  setAttr("ring", 2);
			break;
		case 0:  setAttr("ring", 0);
		break;
		}
		 List<Record> records=Db.find("select a.num,b.iname,b.icon,b.realPrice,b.category,a.froms,a.tos from ingoods as a,items as b where a.item=b.iid and a.rid=? order by b.category",rid);
	     if(manager.getInt("ring")==2)
	     {
	    	 if(records!=null)
	    	 {  
	    	 int t=records.get(0).getInt("froms");
	    	 if(t!=manager.getInt("mid"))
	    	 {
	    		 redirect("/mgradmin/error");
//	    		 System.out.println(records.get(0).getInt("froms"));
//	    		 System.out.println(manager.getInt("mid"));
//	    		 System.out.println(2!=2);
//	    		 System.out.println((records.get(0).getInt("froms"))==(manager.getInt("mid")));
	    		 return;
	    	 }
	    	 }
	     }else if(manager.getInt("ring")==1 || manager.getInt("ring")==0)
	     {
	    	 int t=manager.getInt("mid");
	    	 if(records!=null && (records.get(0).getInt("froms")!=t && records.get(0).getInt("tos")!=t))
	    	 {
	    		 redirect("/mgradmin/error");
	    		 return;
	    	 }
	     }
	     setAttr("igList", records);
	     setAttr("backType", type);
	     render("tradeInfo.html");
	 }
	 /**
	  * ajax返回商品详情
	  */
	 public void seeItems()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 List<Record> itemList=null;
		 if(manager.getInt("ring")==2)
		 {
			 Areas building=Areas.dao.findById(manager.getInt("location"));
	         Areas area=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",building.getStr("city"),building.getStr("college"),"");
			itemList=Db.find("select a.iid,a.iname,a.icon,a.realPrice,a.category,b.restNum from items as a,items_on_sale as b where b.location=? and a.iid=b.iid and b.restNum>0 order by category",area.getInt("aid"));  
		 }else if(manager.getInt("ring")==1){
			 itemList=Db.find("select a.iid,a.iname,a.icon,a.realPrice,a.category,b.restNum from items as a,items_on_sale as b where b.location=? and a.iid=b.iid and b.restNum>0 order by category",0);  
		}
		 setAttr("itemList", itemList);
		 renderJson();
	 }
	 /**
	  * 提交进货订单
	 * @throws SQLException 
	  */
	 @Before(Tx.class)
	 public void ingoods() throws SQLException //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 int to=1;
		 int tolocation=0;
		 if(manager.getInt("ring")==2)
		 {
			 Areas areas=Areas.dao.findById(manager.getInt("location"));
			 Areas area=Areas.dao.findFirst("select aid from areas where city=? and college=? and building=?",areas.getStr("city"),areas.getStr("college"),"");
			 tolocation=area.getInt("aid");
			 to=Managers.dao.findFirst("select * from managers where location=?",area.getInt("aid")).getInt("mid");
		 }else if(manager.getInt("ring")==1)
			 {to=1;  //总管理员mid
			  tolocation=0;
			 }
		 String content[]=getPara("content").split(";");
		 Ingoods tempig=Ingoods.dao.findFirst("select distinct rid from ingoods order by rid desc");
		 int rid=1;
		 if(tempig!=null)
			 rid=tempig.getInt("rid")+1;
		 for(int i=0;i<content.length;i++)
		 {
			 String temp[]=content[i].split(":");
			 if(Integer.parseInt(temp[1])<0)
			 {
				 renderHtml(Util.getJsonText("商品数量不能为负"));
				 throw new SQLException();
			 }
			 Items_on_sale ios=Items_on_sale.dao.findFirst("select * from items_on_sale where location=? and iid=?",tolocation,Integer.parseInt(temp[0]));
			 if(ios.getInt("restNum")<Integer.parseInt(temp[1]))
			 {
				 renderHtml(Util.getJsonText("货物不足"));
				 throw new SQLException();
			 }
			 Ingoods ig=new Ingoods();
			 ig.set("rid", rid).set("froms", manager.getInt("mid")).set("tos", to);
			 ig.set("item", Integer.parseInt(temp[0])).set("num", Integer.parseInt(temp[1]));
			 ig.set("state", 1);  //1 已提交
			 ig.set("addedDT", new Timestamp(System.currentTimeMillis()));
			 ig.save();
		 }
		 new Informs().set("type", "ingoods").set("tos", to).set("addedDT", new Timestamp(System.currentTimeMillis())).save();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  管理，校区负责人确认处理进货订单
	 * @throws SQLException 
	  */
	 @Before(Tx.class)
	 public void confirmDealIg() throws SQLException //ajax
	 {
		 int rid=getParaToInt("rid");
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 List<Ingoods> igList=Ingoods.dao.find("select iid,state,tos,froms,item,num from ingoods where rid=?",rid);
		 int t=igList.get(0).getInt("tos");
		 if(igList!=null && t!=manager.getInt("mid"))
		 {
			 redirect("/mgradmin/error");
			 return;
		 }
		 if(igList!=null && igList.get(0).getInt("state")!=1)
		 {
			 redirect("/mgradmin/error");
			 return;
		 }
		 for(int i=0;i<igList.size();i++)
		 { 
			 Items_on_sale ios=Items_on_sale.dao.findFirst("select * from items_on_sale where location=? and iid=?",manager.getInt("location"),igList.get(i).getInt("item"));
			 if(ios==null || ios.getInt("restNum")<igList.get(i).getInt("num"))
			 {
				 renderHtml(Util.getJsonText("货物不足"));
				 throw new SQLException();
			 }
			 ios.set("restNum", ios.getInt("restNum")-igList.get(i).getInt("num")).update();
			 igList.get(i).set("state", 2).update();  // 2 处理中
		 }
		 new Informs().set("type", "ingoods").set("tos", igList.get(0).getInt("froms")).set("addedDT", new Timestamp(System.currentTimeMillis())).save();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  校区负责人查看店长列表
	  */
	 @Before(Ring1Interceptor.class)
	 public void getSellers()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 switch (manager.getInt("ring")) {
			case 1:  setAttr("ring", 1);
				break;
			case 2:  setAttr("ring", 2);
				break;
			}
		 List<Record> managers=new ArrayList<Record>();
		 if(manager.getInt("ring")==1)
		 {
			 Areas area=Areas.dao.findById(manager.getInt("location"));
			 List<Areas> areaList=new ArrayList<Areas>();
			 areaList.add(Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",area.getStr("city"),area.getStr("college"),"").set("building", "本校区"));
			 areaList.addAll(Areas.dao.find("select * from areas where city=? and college=? and building!=?",area.getStr("city"),area.getStr("college"),""));
			 for(int i=0;i<areaList.size();i++)
			 {
				 Record temp=Db.findFirst("select * from managers where location=?",areaList.get(i).getInt("aid"));
				 
				 if(temp!=null)
				 { 
					 temp.set("building", areaList.get(i).getStr("building"));
					 managers.add(temp);
				 }
			 }
		 }
		 setAttr("Managers", managers);
		 render("getSeller.html");	
	 }
	 /**
	  *    添加地区
	  */
	 @Before(Ring0Interceptor.class)
	 public void addArea()    //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 Ring0Service ring0Service=new Ring0Service(this, manager);
		 ring0Service.addArea();
	 }
	 /**
	  *    删除地区
	  */
	 @Before(Ring0Interceptor.class)
	 public void delArea()    //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 if(getPara("aid")==null)
		 {
			 redirect("/mgradmin/error");
			 return;
		 }
		 int aid=getParaToInt("aid");
		 Areas area=Areas.dao.findById(aid);
		 if(area==null)
		 {
			 renderHtml(Util.getJsonText("参数错误"));
			 return;
		 }
		 if(area.getStr("college").equals(""))
		 {
			 List<Areas> areaList=Areas.dao.find("select * from areas where city=?",area.getStr("city"));
			 for(int i=0;i<areaList.size();i++)
			 {
				 areaList.get(i).delete();
			 }
		 }else {
			 if(area.getStr("building").equals(""))
			 {
			 List<Areas> areaList=Areas.dao.find("select * from areas where city=? and college=?",area.getStr("city"),area.getStr("college"));
			 for(int i=0;i<areaList.size();i++)
			 {
				 areaList.get(i).delete();
			 }
			 }else {
				area.delete();
			}
		}
		
		 area.delete();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  查看地区
	  */
	 @Before(Ring0Interceptor.class)
	 public void areas()
	 {
		 Managers login=getSessionAttr(GlobalVar.BEUSER);
		 Areas areas=Areas.dao.findById(login.getInt("location"));
		 String city=getPara("city");  //  areas?6  
		 if(city==null)
		 {
			 if(login.getInt("ring")==0)
			 {
		 List<Areas> areasList=Areas.dao.find("select distinct city,aid from areas where city!=? and college=? order by convert(city using gbk) asc","","");
		 setAttr("areaList", areasList);   //aid,city,college,building
		 setAttr("type", 1);  //1 城市  2校区 3楼栋
		 render("areas.html");
			 }else {
				List<Areas> areasList=Areas.dao.find("select * from areas where city=? and college=? order by building asc",areas.getStr("city"),areas.getStr("college"));
				setAttr("areaList", areasList);   //aid,city,college,building
				render("areas.html");
			}
		 }else
		 {
			 //显示某个地区的具体页面。内容待定
             String college=getPara("college");
             if(college==null)
             {
            	 List<Record> areasList=Db.find("select distinct college,aid from areas where college!=? and building=?  and city=? order by college asc","","",city);
            	 System.out.println(areasList.size());
            	 for(int i=0;i<areasList.size();i++)
            	 {
            		 Managers manager=Managers.dao.findFirst("select name,tel from managers where location=?",areasList.get(i).getInt("aid"));
            		 if(manager==null)
            		 {
            			 areasList.get(i).set("name", "").set("tel", "");
            		 }else {
            			 areasList.get(i).set("name", manager.getStr("name")).set("tel", manager.getStr("tel"));
					}
            	 }
        		 setAttr("areaList", areasList);
        		 setAttr("city", city);
        		 setAttr("type", 2); 
        		 render("areas.html");
             }else {
            	 List<Record> areasList=Db.find("select distinct building,aid from areas where city=? and  college=? and building!=? order by building asc",city,college,"");
            	 for(int i=0;i<areasList.size();i++)
            	 {
            		 Managers manager=Managers.dao.findFirst("select name,tel from managers where location=?",areasList.get(i).getInt("aid"));
            		 if(manager==null)
            		 {
            			 areasList.get(i).set("name", "").set("tel", "");
            		 }else {
            			 areasList.get(i).set("name", manager.getStr("name")).set("tel", manager.getStr("tel"));
					}
            	 }
            	 setAttr("areaList", areasList);
        		 setAttr("city", city);
        		 setAttr("college", college);
        		 setAttr("type", 3); 
        		 render("areas.html");
			}
//			 Managers manager=Managers.dao.findFirst("select * from managers where location=?",areaID);
//			 setAttr("Manager", manager);
//			 
//			 List<Record> iosList=Db.find("select a.iname,a.icon,a.category,b.iosid,b.restNum,b.price from items as a,items_on_sale as b where a.iid=b.iid and b.location=?",areaID);
//			 setAttr("iosList", iosList);
//			 render("spearea.html");
		 }
	 }
	 /**
	  *  搜索地区
	  */
	 public void searchArea()
	 {
		 int type=getParaToInt("type");
		 String q=getPara("q");
		 if(q!=null)
		 {
			 List<Record> areaList=null;
			 if(type==1)
			 {
				 areaList=Db.find("select * from areas where city=? and college=?",q,"");
				 setAttr("type", 1);  //1 城市  2校区 3楼栋
			 }else if(type==2)
			 {
				 String city=getPara("city");
				 areaList=Db.find("select * from areas where city=? and college regexp ? and building=?",city,".*"+q+".*","");
				 for(int i=0;i<areaList.size();i++)
            	 {
            		 Managers manager=Managers.dao.findFirst("select name,tel from managers where location=?",areaList.get(i).getInt("aid"));
            		 if(manager==null)
            		 {
            			 areaList.get(i).set("name", "").set("tel", "");
            		 }else {
            			 areaList.get(i).set("name", manager.getStr("name")).set("tel", manager.getStr("tel"));
					}
            	 }
				 setAttr("type", 2);  //1 城市  2校区 3楼栋
				 setAttr("city", city);
			 }
			 setAttr("areaList", areaList);   //aid,city,college,building
			 render("areas.html");
		 }
	 }
	 //可不可以修改某地区商品存货数量的问题。
	 /**
	  *  ajax提交修改货物存量
	  */
	 @Before(Ring1Interceptor.class)
	 public void modifyRestNum()
	 {
		 Managers login=getSessionAttr(GlobalVar.BEUSER);
		 if(login.getInt("ring")==666)
		 {
			 JSONArray jsonarr=JSONObject.parseArray(getPara("json"));
			 for(int i=0;i<jsonarr.size();i++)
			 {
				 JSONObject json=jsonarr.getJSONObject(i);
				 int iosid=json.getIntValue("iosid");
				 int restNum=json.getIntValue("restNum");
				 Items_on_sale.dao.findById(iosid).set("restNum", restNum).update();
			 }
			 renderHtml(Util.getJsonText("OK"));
			 return;
		 }else if(login.getInt("ring")==1 || login.getInt("ring")==0)
		 {
			 if(getPara("iosid")==null || getPara("restNum")==null)
			 {
				 redirect("/mgradmin/error");
				 return;
			 }
			 int iosid=getParaToInt("iosid");
			 int restNum=getParaToInt("restNum");
			 if(restNum<0)
			 {
				 renderHtml(Util.getJsonText("商品货存不能为负!"));
				 return;
			 }
			 Items_on_sale ios=Items_on_sale.dao.findById(iosid);
			 Areas area1=Areas.dao.findById(login.getInt("location"));
			 Areas area2=Areas.dao.findById(ios.getInt("location"));
			 if(area1.getStr("city").equals(area2.getStr("city")) && area1.getStr("college").equals(area2.getStr("college")))
		     {ios.set("restNum", restNum).update();
			 renderHtml(Util.getJsonText("OK"));
			 return;}
			 else {
				 renderHtml(Util.getJsonText("无权修改"));
				 return;
			}
		 }	 
	 }
	 /**
	  *    设置编辑促销活动
	  */
	 @Before(Ring0Interceptor.class)
	 public void promotion()
	 {
		 List<Promotion> proList=Promotion.dao.find("select * from promotion order by addedDT desc");
		 setAttr("proList", proList);
		 
		 render("promotion.html");
	 }
	 @Before(Ring0Interceptor.class)
	 public void delPromotion()  //ajax
	 {
		 int pid=getParaToInt("pid");
		 Promotion.dao.findById(pid).delete();
         renderHtml(Util.getJsonText("OK"));
	 }
	 @Before(Ring0Interceptor.class)
	 public void editPromotion()  //ajax
	 {
		 int pid=getParaToInt("pid");
		 String content=getPara("content");
		 Promotion.dao.findById(pid).set("content", content).update();
         renderHtml(Util.getJsonText("OK"));
	 }
	 @Before(Ring0Interceptor.class)
	 public void addPromotion()   //ajax
	 {
		 String content=getPara("content");
		 new Promotion().set("content", content).set("isshow", false).set("addedDT", new Timestamp(System.currentTimeMillis())).save();
         renderHtml(Util.getJsonText("OK"));
	 }
	 @Before(Ring0Interceptor.class)
	 public void adjustIsshow()   //ajax
	 {
		 int type=getParaToInt("type");  // 0隐藏 1显示
         int pid=getParaToInt("pid");
         Promotion pro=Promotion.dao.findById(pid);
         if(pro==null)
         {
        	 redirect("/mgradmin/error");
        	 return;
         }
         if(type==0)
        	 pro.set("isshow", false).update();
         else if(type==1)
             pro.set("isshow", true).update();
         renderHtml(Util.getJsonText("OK"));
	 }
	 
	 
	 @Before(Ring0Interceptor.class)
	 public void addmgr()    //addmgr.html
	 {
		 String areaID=getPara("aid");
		 setAttr("location", areaID);  //隐藏表单域managers.location
		 render("addmgr.html");
	 }
	 
	 /**
	  *    设置店长
	  */
	 @Before(Ring0Interceptor.class)
	 public void setManager()    //表单
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 Ring0Service ring0Service=new Ring0Service(this, manager);
		 ring0Service.setManager();
	 }
	 /**
	  *    查看店长信息
	  */
	 @Before(Ring0Interceptor.class)
	 public void seeManagerInfo()    
	 {
		 int aid=getParaToInt("aid");
		 Managers manager=Managers.dao.findFirst("select * from managers where location=?",aid);
		 if(manager==null)
		 {
			 manager=new Managers();
			 manager.set("name", "");
		 }
		 setAttr("manager", manager);
		 renderJson();
	 }
	 /**
	  *   查看提现申请
	  */
	 @Before(Ring0Interceptor.class)
	 public void seeApplyIncomes()
	 {
		 List<Applyincome> aiList=Applyincome.dao.find("select * from applyincome order by addedDT desc");
		 
		 for(int i=0;i<aiList.size();i++)
		 {
			 Applyincome ai=aiList.get(i);
			 if(ai.getInt("state")==0)
			 {
				 Managers manager=Managers.dao.findFirst("select * from managers where tel=?",ai.getStr("tel"));
				 Incomes income=Incomes.dao.findFirst("select * from incomes where mid=?",manager.getInt("mid"));
				 ai.set("sales", income.getBigDecimal("sales"));
				 if(manager.getInt("ring")==2)
					 ai.set("income", new BigDecimal(income.getBigDecimal("sales").doubleValue()*0.2));
				 else if(manager.getInt("ring")==1)
					 ai.set("income", new BigDecimal(income.getBigDecimal("sales").doubleValue()*0.03));
			 }
		 }
		 setAttr("aiList", aiList);
		 render("applyCashList.html");
	 }
	 @Before(Ring0Interceptor.class)
	 public void dealApplyIncomes()
	 {
		 if(getPara("aid")==null)
			 return;
		 int aid=getParaToInt("aid");
		 Applyincome ai=Applyincome.dao.findById(aid);
		 Managers manager=Managers.dao.findFirst("select * from managers where tel=?",ai.getStr("tel"));
		 Incomes income=Incomes.dao.findFirst("select * from incomes where mid=?",manager.getInt("mid"));
		 ai.set("sales", income.getBigDecimal("sales"));
		 if(manager.getInt("ring")==2)
			 ai.set("income", new BigDecimal(income.getBigDecimal("sales").doubleValue()*0.2));
		 else if(manager.getInt("ring")==1)
			 ai.set("income", new BigDecimal(income.getBigDecimal("sales").doubleValue()*0.03));
		 ai.set("state", 1).update();
		 manager.set("totalsales", new BigDecimal(manager.getBigDecimal("totalsales").doubleValue()+income.getBigDecimal("sales").doubleValue())).update();
		 income.set("sales", 0).update();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  查看地区详情
	  */
	 public void seeAreaDetails()
	 {
		 int type=getParaToInt("type"); // 1 订单 2库存 3 数据统计
		 int aid=getParaToInt("aid");
		 Areas areas=Areas.dao.findById(aid);
		 Managers manager=Managers.dao.findFirst("select * from managers where location=?",aid);
		 if(manager==null)
		 {
			 redirect("/mgradmin/error?Msg="+Util.getEncodeText("该地区尚无信息"));
			 return;
		 }
		 if(type==1)
		 {
			 int page=1;
			if(getParaToInt(0)!=null){
				page=getParaToInt(0);
				}
			 if(manager.getInt("ring")==1)
			 {
				 Areas area=Areas.dao.findById(aid);
				 List<Areas> areaList=Areas.dao.find("select * from areas where city=? and college=?",area.getStr("city"),area.getStr("college"));	
				 List<Trades> ridList=new ArrayList<Trades>();
				 String state=getPara("state");
				 for(int i=0;i<areaList.size();i++)
				 {
				 ridList.addAll(Trades.dao.paginate(page,10,"select distinct rid,location,state,room,addedDate,addedTime","from trades where state!=2 and location=? order by addedDate desc,addedTime desc",areaList.get(i).getInt("aid")).getList());
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
						Areas t=Areas.dao.findById(ridList.get(i).getInt("location"));
						temp.set("room", t.getStr("building")+ridList.get(i).get("room"));
						temp.set("items", itemsRecords);
						records.add(temp);
					}
				 setAttr("tradeList", records);
				 setAttr("page", page);
			 }else if(manager.getInt("ring")==2)
			 {
					 List<Trades> ridList;
					  ridList=Trades.dao.paginate(page, 10, "select distinct rid,room,state,addedDate,addedTime", "from trades where state!=2 and seller=? order by addedDate desc,addedTime desc",manager.getInt("mid")).getList();
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
							temp.set("items", itemsRecords);
							temp.set("money", money);
							temp.set("room", ridList.get(i).get("room"));
							records.add(temp);
						}
					 setAttr("tradeList", records);
					 setAttr("page", page);
			 }
			 setAttr("type", 1);
		 }else if(type==2)
		 {
			 List<Record> iosList=null;
			 switch (manager.getInt("ring")) {
			case 1:  setAttr("ring", 1);
			    Areas college=Areas.dao.findById(aid);
			    iosList=Db.find("select a.iname,a.icon,a.category,b.iosid,b.restNum,b.price,b.minPrice,b.maxPrice from items as a,items_on_sale as b where a.iid=b.iid and b.location=?",aid);
				setAttr("iosList", iosList);
				setAttr("startPrice",college.getBigDecimal("startPrice").doubleValue());
				break;
			case 2:  setAttr("ring", 2);
			 iosList=Db.find("select a.iname,a.icon,a.category,b.iosid,b.restNum,b.price,b.minPrice,b.maxPrice from items as a,items_on_sale as b where a.iid=b.iid and b.location=?",manager.getInt("location"));
			 setAttr("iosList", iosList);
			 Areas area=Areas.dao.findById(manager.getInt("location"));
			 setAttr("startPrice",area.getBigDecimal("startPrice").doubleValue());
				break;
			 }
			 setAttr("type", 2);
		 }else if(type==3)
		 {
			 switch (manager.getInt("ring")) {
				case 1:  setAttr("ring", 1);
					break;
				case 2:  setAttr("ring", 2);
					break;
				}
				 String month=getPara("month");
				 if(month==null)
					 month=Util.getMonth();
				 List<Record> records=Db.find("select a.iname,b.num,b.money from items as a,areasales as b where a.iid=b.item and b.location=? and b.month=?",manager.getInt("location"),month);
				 double sum=0;
				 for(int i=0;i<records.size();i++)
				 {
					 sum+=records.get(i).getBigDecimal("money").doubleValue();
				 }
				 setAttr("dataList", records);
				 setAttr("MonthSales", sum);
				 setAttr("date_info", month);
				 setAttr("type", 3);
		 }
		 setAttr("aid", aid);
		 setAttr("state",areas.getBoolean("state"));
		 setAttr("ring", manager.getInt("ring"));
		 if(manager.getInt("ring")==1)
			 setAttr("backurl", "/mgradmin/areas?city="+areas.getStr("city"));
		 else {
			 setAttr("backurl", "/mgradmin/areas?city="+areas.getStr("city")+"&college="+areas.getStr("college"));
		}
		 Incomes income=Incomes.dao.findFirst("select * from incomes where mid=?",manager.getInt("mid"));
		 setAttr("TotalSales", manager.getBigDecimal("totalsales").doubleValue()+income.getBigDecimal("sales").doubleValue());
		 setAttr("CurrentSales", income.getBigDecimal("sales").doubleValue());
		 render("seeMoreInfo.html");
	 }
	 /**
	  *  商品页
	  */
	 @Before(Ring0Interceptor.class)
	 public void items()   //items?666     //items?del=666    //items
	 {
		 String para=getPara("del");
		 if(para==null)
		 { 
			 if(getParaToInt(0)!=null) {
			//某个商品详情页
			 int iid=getParaToInt(0);
			 Items item=Items.dao.findById(iid);
			 setAttr("Item", item);			 
			 render("speitem.html");
			 return;
		}
			 //所有商品
			 List<Items> itemList=Items.dao.find("select * from items");
			 setAttr("itemList", itemList);
			 render("items.html");
			 return;
		 }else{ 
			//删除商品     ajax
			 int iid=getParaToInt("del");
			 Items.dao.deleteById(iid);
			 renderHtml(Util.getJsonText("OK"));
			 return;
		}
		
	 }	 
	 /**
	  *    编辑商品      包括添加，修改
	  */
	 @Before(Ring0Interceptor.class)
	 public void addItem()
	 {
		 if(getParaToInt(0)!=null)
		 {
			 int iid=getParaToInt(0);
			 Items items=Items.dao.findById(iid);
			 setAttr("items", items);
			 setAttr("type", 1);   //1 编辑  0 添加
		 }else {
			 setAttr("type", 0);
		}
		 render("speitem.html");
	 }
	 @Before(Ring0Interceptor.class)
	 public void modifyItem()     //表单提交
	 {
		
		 String type=getPara("type");
		 if(type.equals("0"))
		 {
			 String src=Util.getRandomString()+"-items.jpg";
			 File tofile=new File(Util.getImgPath()+src);
			 while(tofile.exists())
			 {
				 src=Util.getRandomString()+"-items.jpg";
				 tofile=new File(Util.getImgPath()+src);
			 }
			 UploadFile file=getFile("icon", Util.getImgPath());
			 if(file==null)
				 {
					 setAttr("errorMsg", "图片未选择");
					 keepModel(Items.class);
					 render("speitem.html");
					 return;
				 }
			 File f=file.getFile();
			 f.renameTo(tofile);
			 //System.out.println(src);
			 Items item=getModel(Items.class);
			 item.set("icon", "/imgs/"+src);
			 item.set("iname", Util.filterUserInputContent(item.getStr("iname")));
			 item.set("category", Util.filterUserInputContent(item.getStr("category")));
			item.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
			item.save();
			Items_on_sale ios=new Items_on_sale();
			ios.set("iid", item.getInt("iid"));   //能否获取到？
			ios.set("minPrice",new BigDecimal(0)).set("maxPrice", new BigDecimal(0)).set("price", item.getBigDecimal("realPrice"));
			ios.set("restNum", 0).set("location", 0).set("isonsale", true);
			ios.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
			ios.save();
			redirect("/mgradmin/items");
			return;
		 }else if(type.equals("1"))  //须设隐藏表单域iid
		 {
			 UploadFile file=getFile("icon", Util.getImgPath(), 2*1024*1024);
			 Items item=getModel(Items.class);
			 Items origin=Items.dao.findById(item.getInt("iid"));
			 if(file!=null)
			 {
				 String src=Util.getRandomString()+"-items.jpg";
				 File tofile=new File(Util.getImgPath()+src);
				 while(tofile.exists())
				 {
					 src=Util.getRandomString()+"-items.jpg";
					 tofile=new File(Util.getImgPath()+src);
				 }
				 File f=file.getFile();
				 f.renameTo(tofile);
				 
				 File file2=new File(Util.getImgPath()+origin.getStr("icon"));
				 if (file2.exists()) {
					file2.delete();
				      }
				 item.set("icon", "/imgs/"+src);
			 }	
			 double cost=item.getBigDecimal("cost").doubleValue();
			 if(cost!=origin.getBigDecimal("cost").doubleValue())
			 {
				 List<Items_on_sale> iosList=Items_on_sale.dao.find("select * from items_on_sale where iid=?",item.getInt("iid"));
				 for(int i=0;i<iosList.size();i++)
				 {
					 iosList.get(i).set("minPrice", new BigDecimal(cost*lowLimit)).set("maxPrice", new BigDecimal(cost*highLimit)).set("price", item.getBigDecimal("realPrice")).update();
				 }
			 }
			 item.set("iname", Util.filterUserInputContent(item.getStr("iname")));
			 item.set("category", Util.filterUserInputContent(item.getStr("category")));
			 item.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());				
			 item.update();
			 redirect("/mgradmin/items");
			 return;
		 }
	 }
	 
	 /**
	  *   查看申请当店长
	  */
	 @Before(Ring0Interceptor.class)
	 public void seeApplyfor()
	 {
		 int page=1;
		 if(getParaToInt(0)!=null){
				page=getParaToInt(0);
			}
		 List<Applyfor> afList=Applyfor.dao.paginate(page, 15, "select *","from applyfor order by addedDT desc").getList();
		 setAttr("afList", afList);
		 setAttr("page", page);
		 render("seeApplyfor.html");
//		 ApplyforThread aft=new ApplyforThread(afList);
//		 Thread t=new Thread(aft);
//		 t.start();
	 }
	 /**
	  *  处理店长审核
	  */
	 @Before(Ring0Interceptor.class)
	 public void dealApplyfor()
	 {
		int aid=getParaToInt("aid");
		Applyfor.dao.findById(aid).set("state", 1).update();
		renderHtml(Util.getJsonText("OK"));
	 }
	 /**
	  *  查看投诉建议
	  */
	 @Before(Ring1Interceptor.class)
	 public void seeAdvices()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 int page=1;
		 if(getParaToInt(0)!=null){
				page=getParaToInt(0);
			}
		 if(manager.getInt("ring")==1)
		 {
			 Areas area=Areas.dao.findById(manager.getInt("location"));
			 List<Advices> advices=new ArrayList<Advices>();
			 List<Record> records=new ArrayList<Record>();
			 List<Areas> areaList=Areas.dao.find("select * from areas where city=? and college=?",area.getStr("city"),area.getStr("college"));
			 for(int i=0;i<areaList.size();i++)
			 {
				 List<Advices> ads=Advices.dao.find("select * from advices where location=?",areaList.get(i).getInt("aid"));
				 advices.addAll(ads);
			 }
			 for(int i=0;i<advices.size();i++)
			 {
				 Record record=Db.findFirst("select a.content,a.addedDate,a.addedTime,b.building from advices as a,areas as b where a.location=b.aid and a.aid=?",advices.get(i).getInt("aid"));
				 records.add(record);
			 }
			 setAttr("Advices", records);
		 }else {
			 List<Record> records=Db.paginate(page,10,"select a.type,a.content,a.addedDate,a.addedTime,b.city,b.college,b.building","from advices as a,areas as b where a.location=b.aid order by addedDate desc,addedTime desc").getList();
			 setAttr("Advices", records);
		}
		 setAttr("page", page);
		 render("seeAdvice.html");
	 }
	 /**
	  *  订单提醒
	  */
	 public void inform()  //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 Trades trades=Trades.dao.findFirst("select rid,state from trades where location=? order by addedDate desc,addedTime desc",manager.getInt("location"));
		 int rid=0;
		 if(getSessionAttr("LatestTradeNo")!=null)
		   rid=getSessionAttr("LatestTradeNo");
		 if(rid==trades.getInt("rid"))
		 {
			 renderHtml(Util.getJsonText("NO"));
			 return;
		 }
		 if(trades.getInt("state")==0)
		 {
			 setSessionAttr("LatestTradeNo", trades.getInt("rid"));
			 renderHtml(Util.getJsonText("YES"));
		 }
		 else {
			renderHtml(Util.getJsonText("NO"));
		}
	 }
	 /**
	  *  获取通知
	  */
	 public void getinforms()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 List<Informs> informs=Informs.dao.find("select * from informs where tos=?",manager.getInt("mid"));
		 if(!informs.isEmpty())
			 renderHtml(Util.getJsonText(informs.size()+""));
		 else
			 renderHtml(Util.getJsonText("NONE"));
	 }
	 /**
	  * 查看整体订单
	  */
	 @Before(Ring0Interceptor.class)
	 public void tradesALL()
	 {	
		int page=1;
		int flag=0;  // 0 全部 1未处理 2已完成
		if(getParaToInt(0)!=null){
			page=getParaToInt(0);
		}
		String date=Util.getDate();
		if(getPara("date")!=null){
		    date=getPara("date");	
		}
		 List<Trades> ridList;
		 String state=getPara("state");
		 if(state==null)
			 ridList=Trades.dao.paginate(page,15,"select distinct a.rid,a.state,a.location,a.room,a.addedDate,a.addedTime,a.finishedTimeStamp,b.tel,b.name","from trades as a,user as b where a.customer=b.uid and a.addedDate=? and a.state!=2 order by a.addedDate desc,a.addedTime desc",date).getList();
		 else {
			if(state.equals("0"))
				{ridList=Trades.dao.paginate(page,15,"select distinct a.rid,a.state,a.location,a.room,a.addedDate,a.addedTime,a.finishedTimeStamp,b.tel,b.name","from trades as a,user as b where a.customer=b.uid and a.state=0 and a.addedDate=? order by a.addedDate desc,a.addedTime desc",date).getList();
			     flag=1;}
				else if(state.equals("1"))
				{ridList=Trades.dao.paginate(page,15,"select distinct a.rid,a.state,a.location,a.room,a.addedDate,a.addedTime,a.finishedTimeStamp,b.tel,b.name","from trades as a,user as b where a.customer=b.uid and a.state=1  and a.addedDate=? order by a.addedDate desc,a.addedTime desc",date).getList();
			     flag=2; }
				else {
				redirect("/mgradmin/error");
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
				Areas t=Areas.dao.findById(ridList.get(i).getInt("location"));
				temp.set("room", t.getStr("college")+t.getStr("building")+ridList.get(i).getStr("room"));
				temp.set("tel", ridList.get(i).getStr("tel"));
				temp.set("name", ridList.get(i).getStr("name"));
				String time=ridList.get(i).get("finishedTimeStamp").toString();
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//				//随便怎么转都可以的
//				Date tdate;
//				String dateString="";
//				try {
//					tdate = formatter.parse(time);
//					formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					dateString = formatter.format(tdate);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				 
				temp.set("finishTime",time);
				temp.set("items", itemsRecords);
				records.add(temp);
			}
		 setAttr("tradeList", records);
		 setAttr("flag", flag);
		 setAttr("date_info", date);
		 setAttr("page", page);
	 }
	 /**
	  *    公告图片
	  */
	 @Before(Ring0Interceptor.class)
	 public void annoncement()
	 {
		 List<Advertisement> adList=Advertisement.dao.find("select * from advertisement order by addedDate desc,addedTime desc");
	     setAttr("adList", adList);
	     render("annoncement.html");
	 }
	 /**
	  *  上传公告图片
	  */
	 @Before(Ring0Interceptor.class)
	 public void uploadAd()
	 {
		 String src=Util.getRandomString()+"-ads.jpg";
		 File tofile=new File(Util.getImgPath()+src);
		 while(tofile.exists())
		 {
			 src=Util.getRandomString()+"-ads.jpg";
			 tofile=new File(Util.getImgPath()+src);
		 }
		 UploadFile file=getFile("adimg", Util.getImgPath());
		 if(file==null)
			 {
				 renderHtml("<script>alert('图片未选择'); window.location='/mgradmin/annoncement';</script>");
				 return;
			 }
		 File f=file.getFile();
		 f.renameTo(tofile);
		 new Advertisement().set("img","/imgs/"+src).set("addedDate", Util.getDate()).set("addedTime", Util.getTime()).save();
		 renderHtml("<script>alert('上传成功'); window.location='/mgradmin/annoncement';</script>");
	 }
	 /**
	  *  删除公告图片
	  */
	 @Before(Ring0Interceptor.class)
	 public void delAd()
	 {
		 int astid=getParaToInt("astid");
		 Advertisement ad=Advertisement.dao.findById(astid);
		 ad.delete();
		 renderHtml(Util.getJsonText("OK"));
	 }
	 @Clear
	 public void error()
	 {
			String Msg=getPara("Msg");
			String backURL=getPara("backurl");
			if(Msg==null)
				Msg="未知错误！";
			if(backURL==null)
				backURL="/mgradmin";
			setAttr("Msg", Msg);
			setAttr("backurl", backURL);
			render("/404/error.html");
	 }
//	 @Clear
//	 public void importData()
//	 {
//		 Province province=Province.dao.findFirst("select * from province_info where pr_province=?","黑龙江");
//		 List<City> cities=City.dao.find("select * from city_info where ci_province=?",province.getInt("pr_id"));
//		 for(int i=0;i<cities.size();i++)
//		 {
//			 String city=cities.get(i).getStr("ci_city").replace("市", "");
//			 Areas areaCity=Areas.dao.findFirst("select * from areas where city=? and college=?",city,"");
//			 if(areaCity==null)
//			 {
//				 new Areas().set("city", city).set("college", "").set("building", "").set("addedDate", Util.getDate()).set("addedTime",Util.getTime()).save();
//			 }
//			 List<College> colleges=College.dao.find("select * from shool_info where sh_city=?",cities.get(i).getInt("ci_id"));
//			 for(int k=0;k<colleges.size();k++)
//			 {
//				 String college=colleges.get(k).getStr("sh_shool");
//				 Areas areaCollege=Areas.dao.findFirst("select * from areas where city=? and college=? and building=?",city,college,"");
//				 if(areaCollege==null)
//				 {
//					 new Areas().set("city", city).set("college", college).set("building", "").set("addedDate", Util.getDate()).set("addedTime",Util.getTime()).save();
//				 }
//			 }
//		 }
//		 renderHtml(Util.getJsonText("导入完毕"));
//	 }
//	 
//	 @Clear
//	 public void modData()
//	 {
//		 int n=415;
//		 List<Areas> areas=Areas.dao.find("select * from areas where aid>=? and aid<=?",5,43);
//		 for(int i=0;i<areas.size();i++)
//		 {
//			 new Areas().set("city", areas.get(i).getStr("city")).set("college", areas.get(i).getStr("college")).set("building", "").set("addedDate", Util.getDate()).set("addedTime",Util.getTime()).save();	 
//			 areas.get(i).delete();
//		 }
//		 renderHtml(Util.getJsonText("校正完毕"+n));
//	 }
}

