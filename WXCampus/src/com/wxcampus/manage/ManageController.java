package com.wxcampus.manage;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.items.Trades;
import com.wxcampus.util.Util;

/**
 * 后台管理控制器类
 * @author Potato
 *
 */
@Before(ManageInterceptor.class)
public class ManageController extends Controller{

	 public static Logger logger = Util.getLogger();
	 public void index()
	 {
		 
	 }
	 
	 @Clear(ManageInterceptor.class)
	 public void login()   //login.html
	 {
		 if(getSessionAttr(GlobalVar.BEUSER)!=null)
			 redirect("/admin");
		 
		 render("login.html");
	 }
	 
	 @Clear(ManageInterceptor.class)
	 public void loginCheck()
	 {
		 if(getSessionAttr(GlobalVar.BEUSER)!=null)
			 redirect("/mgradmin");   //已登录就跳转
		 
		 //防暴力检测
		 if(ManageLoginSafe.isExist(getPara("Managers.tel")))
	        {
			   redirect("/mgradmin/error?Msg=密码输入错误次数过多，请十分钟后再试！");
	        }else{      
		 Managers form=getModel(Managers.class);
		 Managers manager=Managers.dao.findFirst("select * from managers where tel="+form.getInt("tel"));
		 if(form.getStr("password").equals(manager.getStr("password")))
		 {
			 setSessionAttr(GlobalVar.BEUSER, manager);
			 logger.info(manager.getStr("name")+"---登录后台");
			 redirect("/mgradmin");
		 }else {
				if(getSessionAttr(manager.getInt("tel")+"")!=null)
				{
				int left=getSessionAttr(manager.getInt("tel")+"");
				left--;
				if(left==0)
				{
					loginSafe ls=new loginSafe(manager.getInt("tel")+"",this);
					Thread t=new Thread(ls);
					t.start();
				}else
				setSessionAttr(manager.getInt("tel")+"", left);
				}
				else
				setSessionAttr(manager.getInt("tel")+"",5);
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
			 manager.set("password", newPass).update();
			 logger.info(manager.getStr("name")+"---修改密码");
			 renderHtml("OK");
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
			     
			break;
			
		case 1:   Ring1Service ring1Service=new Ring1Service(this,manager);
		          ring1Service.trades();
		default:
			break;
		}		
	 }
	 
	 /**
	  *   设定营业时间
	  */
	 public void setSellingTime()  //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 switch (manager.getInt("ring")) {
		case 0:     Ring0Service ring0Service=new Ring0Service(this, manager);
			     
			break;
			
		case 1:   Ring1Service ring1Service=new Ring1Service(this,manager);
		          ring1Service.setSellingTime();
		default:
			break;
		}	
	 }
	 /**
	  *  店长查看商品存量
	  */
	 public void itemnum()
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 List<Record> iosList=Db.find("select a.iname,a.icon,a.realPrice,a.category,b.iosid,b.restNum from items as a,items_on_sale as b where a.iid=b.iid and b.location="+manager.getInt("location"));
		 setAttr("iosList", iosList);
		 render("itemnum.html");
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
	 @Before(Ring0Interceptor.class)
	 public void areas()
	 {
		 String area=getPara();  //  areas?6  
		 if(area==null)
		 {
		 List<Areas> areasList=Areas.dao.find("select * from areas order by city,college,building asc");
		 setAttr("areasList", areasList);   //aid,city,college,building
		 render("areas.html");
		 }else
		 {
			 int areaID=Integer.parseInt(area); 
			 //显示某个地区的具体页面。内容待定
			 Managers manager=Managers.dao.findFirst("select * from managers where location="+areaID);
			 setAttr("Manager", manager);
			 
			 List<Record> iosList=Db.find("select a.iname,a.icon,a.realPrice,a.category,b.iosid,b.restNum from items as a,items_on_sale as b where a.iid=b.iid and b.location="+areaID);
			 setAttr("iosList", iosList);
			 render("spearea.html");
		 }
	 }
	 
	 //可不可以修改某地区商品存货数量的问题。
	 
	 
	 
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
			 renderHtml("OK");
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
	  *     进货管理  待定
	  */
	 public void addItemNum()
	 {
		 
	 }
	 
	 public void error()
	 {
		 
	 }
}

