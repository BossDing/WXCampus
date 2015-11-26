package com.wxcampus.manage;

import java.awt.geom.Area;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.common.NoUrlPara;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
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
	 
	 @Before(NoUrlPara.class)
	 public void index()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
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
			 redirect("/mgradmin");
		 
		 render("login.html");
	 }
	 
	 @Clear(ManageInterceptor.class)
	 public void loginCheck()
	 {
		 if(getSessionAttr(GlobalVar.BEUSER)!=null)
			 redirect("/mgradmin");   //已登录就跳转
		 
		 //防暴力检测
		 if(ManageLoginSafe.isExist(""+getParaToInt("Managers.tel")))
	        {
			   redirect("/404/error?Msg="+Util.getEncodeText("密码输入错误次数过多，请十分钟后再试！"));
	        }else{      
		 Managers form=getModel(Managers.class);
		 Managers manager=Managers.dao.findFirst("select * from managers where tel=?",form.getStr("tel"));
		 if(form.getStr("password").equals(manager.getStr("password")))
		 {
			 setSessionAttr(GlobalVar.BEUSER, manager);
			 logger.info(manager.getStr("name")+"---登录后台");
			 redirect("/mgradmin");
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
	  *          修改密码
	  */
	 public void modifyPw()  //ajax
	 {
		 String oldPass=getPara("oldPass");
		 String newPass=getPara("newPass");
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 if(oldPass.equals(manager.getStr("password")))
		 {
			 manager.set("password", Util.filterUserInputContent(newPass)).update();
			 logger.info(manager.getStr("name")+"---修改密码");
			 renderHtml(Util.getJsonText("OK"));
		 }else {
			renderHtml("原始密码输入错误");
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
	  *  数据统计
	  */
	 public void datainfo()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 String month=getPara("month");
		 int year=new Date().getYear();
		 month=year+"-"+month;	
		 List<Record> records=Db.find("select a.iname,b.num,b.money from items as a,areasales as b where a.iid=b.item and b.location=? and b.month=?",manager.getInt("location"),month);
		 setAttr("dataList", records);
		 render("datainfo.html");	
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
	  *  店长查看商品存量
	  */
	 public void itemnum()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 List<Record> iosList=Db.find("select a.iname,a.icon,a.category,b.iosid,b.restNum,b.price,b.minPrice,b.maxPrice from items as a,items_on_sale as b where a.iid=b.iid and b.location=?",manager.getInt("location"));
		 setAttr("iosList", iosList);
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
		 if(igList!=null && igList.get(0).getInt("from")!=manager.getInt("mid"))
		 {
			 redirect("/404/error");
			 return;
		 }
		 //Managers to=Managers.dao.findById(igList.get(0).getInt("to"));
		 for(int i=0;i<igList.size();i++)
		 {
			 igList.get(i).set("state", 3).update();  //3 已完成	 
//			 Items_on_sale iosto=Items_on_sale.dao.findFirst("select * from items_on_sale where location=? and iid=?",to.getInt("location"),igList.get(i).getInt("item"));
//             if(iosto==null)
//             {
//    			 redirect("/404/error");
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
				 ios.set("minPrice", new BigDecimal(cost.doubleValue()*1.2)).set("maxPrice", new BigDecimal(cost.doubleValue()*2));
				 ios.set("price", price);
				 ios.set("restNum", igList.get(i).getInt("num")).set("location", manager.getInt("location"));
				 ios.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
				 ios.save();
			 }else {
				ios.set("restNum", ios.getInt("restNum")+igList.get(i).getInt("num")).update();
			}
		 }
	 }
	 /**
	  *  店长,校区负责人查看提交的进货订单
	  */
	 public void seeInGoods()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
         List<Ingoods> igList=Ingoods.dao.find("select distinct rid,addedDT,state from ingoods where from=?",manager.getInt("mid"));
		 setAttr("igList", igList);
		 List<Informs> informs=Informs.dao.find("select * from informs where to=?",manager.getInt("mid"));
		 for(int i=0;i<informs.size();i++)
			 informs.get(i).delete();
	 }
	 /**
	  *  管理,校区负责人处理提交的进货订单
	  */
	 public void dealInGoods()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 if(manager.getInt("ring")!=0 &&manager.getInt("ring")!=1)
		 {
			 redirect("/404/error?Msg="+Util.getEncodeText("无权操作"));
			 return;
		 }
         List<Ingoods> igList=Ingoods.dao.find("select distinct rid,addedDT,state from ingoods where to=?",manager.getInt("mid"));
		 setAttr("igList", igList);
		 List<Informs> informs=Informs.dao.find("select * from informs where to=?",manager.getInt("mid"));
		 for(int i=0;i<informs.size();i++)
			 informs.get(i).delete();
	 }
	 /**
	  *   进货订单详情
	  */
	 public void igdetails()
	 {
		 int rid=getParaToInt("rid");
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 List<Record> records=Db.find("select a.num,b.iname,b.icon,b.realPrice,b.category,a.from,a.to from ingoods as a,items as b where a.item=b.iid and a.rid=?",rid);
	     if(manager.getInt("ring")==2)
	     {
	    	 if(records!=null && records.get(0).getInt("from")!=manager.getInt("mid"))
	    	 {
	    		 redirect("/404/error");
	    		 return;
	    	 }
	     }else if(manager.getInt("ring")==1)
	     {
	    	 if(records!=null && (records.get(0).getInt("from")!=manager.getInt("mid") && records.get(0).getInt("to")!=manager.getInt("mid")))
	    	 {
	    		 redirect("/404/error");
	    		 return;
	    	 }
	     }
	     setAttr("igList", records);
	 }
	 /**
	  * ajax返回商品详情
	  */
	 public void seeItems()
	 {
		 List<Items> itemList=Items.dao.find("select iid,iname,icon,realPrice,category from items order by category");
		 setAttr("itemList", itemList);
		 renderJson();
	 }
	 /**
	  * 提交进货订单
	  */
	 public void ingoods() //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 int to=1;
		 if(manager.getInt("ring")==2)
		 {
			 Areas areas=Areas.dao.findById(manager.getInt("location"));
			 Areas area=Areas.dao.findFirst("select aid from areas where city=? and college=? and building=?",areas.getStr("city"),areas.getStr("college"),"");
			 to=area.getInt("aid");
		 }else if(manager.getInt("ring")==1)
			 to=1;  //总管理员mid
		 String content[]=getPara("content").split("-");
		 int rid=Ingoods.dao.findFirst("select distinct rid from ingoods order by rid desc").getInt("rid")+1;
		 for(int i=0;i<content.length;i++)
		 {
			 String temp[]=content[i].split(":");
			 Ingoods ig=new Ingoods();
			 ig.set("rid", rid).set("from", manager.getInt("mid")).set("to", to);
			 ig.set("item", Integer.parseInt(temp[0])).set("num", Integer.parseInt(temp[1]));
			 ig.set("state", 1);  //1 已提交
			 ig.set("addedDT", new Timestamp(System.currentTimeMillis()));
			 ig.save();
		 }
		 new Informs().set("type", "ingoods").set("to", to).set("addedDT", new Timestamp(System.currentTimeMillis())).save();
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
		 List<Ingoods> igList=Ingoods.dao.find("select state,to,item,num from ingoods where rid=?",rid);
		 if(igList!=null && igList.get(0).getInt("to")!=manager.getInt("mid"))
		 {
			 redirect("/404/error");
			 return;
		 }
		 for(int i=0;i<igList.size();i++)
		 { 
			 Items_on_sale ios=Items_on_sale.dao.findFirst("select * from items_on_sale where location=? and iid=?",manager.getInt("location"),igList.get(i).getInt("item"));
			 if(ios.getInt("restNum")<igList.get(i).getInt("num"))
			 {
				 renderHtml(Util.getJsonText("货物不足"));
				 throw new SQLException();
			 }
			 ios.set("restNum", ios.getInt("restNum")-igList.get(i).getInt("num")).update();
			 igList.get(i).set("state", 2).update();  // 2 处理中
		 }
		 new Informs().set("type", "ingoods").set("to", igList.get(0).getInt("from")).set("addedDT", new Timestamp(System.currentTimeMillis())).save();
		 renderHtml(Util.getJsonText("OK"));
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
	  *  查看地区
	  */
	 @Before(Ring1Interceptor.class)
	 public void areas()
	 {
		 Managers login=getSessionAttr(GlobalVar.BEUSER);
		 Areas areas=Areas.dao.findById(login.getInt("location"));
		 String area=getPara(0);  //  areas?6  
		 if(area==null)
		 {
			 if(login.getInt("ring")==0)
			 {
		 List<Areas> areasList=Areas.dao.find("select * from areas order by city,college,building asc");
		 setAttr("areasList", areasList);   //aid,city,college,building
		 render("areas.html");
			 }else {
				List<Areas> areasList=Areas.dao.find("select * from areas where city=? and college=? order by building asc",areas.getStr("city"),areas.getStr("college"));
				setAttr("areasList", areasList);   //aid,city,college,building
				render("areas.html");
			}
		 }else
		 {
			 int areaID=Integer.parseInt(area);
			 //显示某个地区的具体页面。内容待定
			 if(login.getInt("ring")==1)
			 {
				 Areas building=Areas.dao.findById(areaID);
				 if(!areas.getStr("city").equals(building.getStr("city")) ||  !areas.getStr("college").equals(building.getStr("college")))
				 {
					 redirect("/404/error?Msg="+Util.getJsonText("无权访问"));
					 return;
				 }
			 }
			 Managers manager=Managers.dao.findFirst("select * from managers where location=?",areaID);
			 setAttr("Manager", manager);
			 
			 List<Record> iosList=Db.find("select a.iname,a.icon,a.category,b.iosid,b.restNum,b.price from items as a,items_on_sale as b where a.iid=b.iid and b.location=?",areaID);
			 setAttr("iosList", iosList);
			 render("spearea.html");
		 }
	 }
	 
	 //可不可以修改某地区商品存货数量的问题。
	 /**
	  *  ajax提交修改货物存量
	  */
	 @Before(Ring1Interceptor.class)
	 public void modifyRestNum()
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
	 }
	 /**
	  *    设置编辑促销活动
	  */
	 @Before(Ring0Interceptor.class)
	 public void promotion()
	 {
		 int showNum=0;
		 Settings set=Settings.dao.findFirst("select value from settings where key=?","promotionShowNum");
		 if(set!=null)
		 {
			 showNum=set.getInt("value");
		 }
		 setAttr("showNum", showNum);
		 
		 List<Promotion> proList=Promotion.dao.find("select * from promotion where isshow=true order by addedDT desc");
		 proList.addAll(Promotion.dao.find("select * from promotion where isshow=false order by addedDT desc"));
		 setAttr("proList", proList);
		 
		 render("promotion.html");
	 }
	 @Before(Ring0Interceptor.class)
	 public void modifyShowNum()  //ajax
	 {
		 int showNum=getParaToInt("showNum");
		 Settings set=Settings.dao.findFirst("select * from settings where key=?","promotionShowNum");
         set.set("value", showNum).update();
         renderHtml(Util.getJsonText("OK"));
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
		 String para=getPara("para");
		 String []temp=para.split("-");
		 int showNum=0;
		 Settings set=Settings.dao.findFirst("select value from settings where key=?","promotionShowNum");
		 if(set!=null)
		 {
			 showNum=set.getInt("value");
		 }
		 if(showNum!=temp.length)
		 {
			 renderHtml(Util.getJsonText("配置错误"));
			 return;
		 }
		 List<Promotion> proList=Promotion.dao.find("select isshow from promotion where isshow=true");
		 for(int i=0;i<proList.size();i++)
		 {
			 proList.get(i).set("isshow", false).update();
		 }
		 for(int i=0;i<showNum;i++)
		 {
			 Promotion.dao.findById(Integer.parseInt(temp[i])).set("isshow", true);
		 }
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
	  *  商品页
	  */
	 @Before(Ring0Interceptor.class)
	 public void items()   //items?666     //items?del=666    //items
	 {
		 String para=getPara();
		 if(para==null)
		 {//所有商品
			 List<Items> itemList=Items.dao.find("select * from items");
			 setAttr("itemList", itemList);
			 render("items.html");
		 }else if(para.startsWith("del=")){ 
			//删除商品     ajax
			 int iid=getParaToInt("del");
			 Items.dao.deleteById(iid);
			 renderHtml(Util.getJsonText("OK"));
		}else {
			//某个商品详情页
			 int iid=getParaToInt();
			 Items item=Items.dao.findById(iid);
			 setAttr("Item", item);			 
			 render("speitem.html");
		}
	 }	 
	 /**
	  *    编辑商品      包括添加，修改
	  */
	 @Before(Ring0Interceptor.class)
	 public void modifyItem()     //表单提交
	 {
		 Items item=getModel(Items.class);
		 item.set("iname", Util.filterUserInputContent(item.getStr("iname")));
		 item.set("category", Util.filterUserInputContent(item.getStr("category")));
		 String type=getPara("submit");
		 if(type.equals("添加"))
		 {
			UploadFile file=getFile("icon", Util.getImgPath(), 2*1024*1024);
			item.set("icon", file.getFileName()).set("addedDate", Util.getDate()).set("addedTime", Util.getTime());
			item.save();
			redirect("/mgradmin/items");
		 }else if(type.equals("修改"))  //须设隐藏表单域iid
		 {
			 UploadFile file=getFile("icon", Util.getImgPath(), 2*1024*1024);
			 if(file!=null)
			 {
				 Items origin=Items.dao.findById(item.getInt("iid"));
				 File file2=new File(Util.getImgPath()+origin.getStr("icon"));
				 if (file2.exists()) {
					file2.delete();
				      }
				 item.set("icon", file.getFileName());
			 }
		    item.set("addedDate", Util.getDate()).set("addedTime", Util.getTime());				
		    item.update();
			
		 }
	 }
	 /**
	  *  查看投诉建议
	  */
	 @Before(Ring1Interceptor.class)
	 public void seeAdvices()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 
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
			 List<Record> records=Db.find("select a.content,a.addedDate,a.addedTime,b.city,b.college,b.building from advices as a,areas as b where a.location=b.aid");
			 setAttr("Advices", records);
		}
		 
		 render("advice.html");
	 }
	 /**
	  *  订单提醒
	  */
	 public void inform()  //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 Trades trades=Trades.dao.findFirst("select state from trades where location=? order by addedDate,addedTime desc",manager.getInt("location"));
		 if(trades.getInt("state")==0)
			 renderHtml(Util.getJsonText("YES"));
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
		 List<Informs> informs=Informs.dao.find("select * from informs where to=?",manager.getInt("mid"));
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
		 List<Trades> ridList;
		 String state=getPara("state");
		 if(state==null)
			 ridList=Trades.dao.find("select distinct rid,state,addedDate,addedTime from trades order by addedDate,addedTime desc");
		 else {
			if(state.equals("0"))
				ridList=Trades.dao.find("select distinct rid,state,addedDate,addedTime from trades where state=0  order by addedDate,addedTime desc");
			else if(state.equals("1"))
				ridList=Trades.dao.find("select distinct rid,state,addedDate,addedTime from trades where state=1  order by addedDate,addedTime desc");
			else {
				redirect("/404/error");
				return;
			}
		}
			List<Record> records=new ArrayList<Record>();
			for(int i=0;i<ridList.size();i++)
			{
				int rid=ridList.get(i).getInt("rid");
				List<Record> itemsRecords=Db.find("select b.iname,b.icon,a.price,a.orderNum from trades as a,items as b where a.item=b.iid and a.rid=?",rid);
				//Record [] items=itemsRecords.toArray(new Record[itemsRecords.size()]);
				Record temp=new Record();
				temp.set("rid", rid);
				temp.set("state", ridList.get(i).getInt("state"));
				temp.set("addedDate", ridList.get(i).get("addedDate"));
				temp.set("addedTime", ridList.get(i).get("addedTime"));
				temp.set("items", itemsRecords);
				records.add(temp);
			}
		 setAttr("tradeList", records);
		 renderJson();
	 }
	 /**
	  *     进货管理  待定
	  */
	 public void addItemNum()
	 {
		 
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
			render("error.html");
	 }
}

