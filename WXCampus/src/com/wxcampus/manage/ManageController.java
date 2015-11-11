package com.wxcampus.manage;

import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
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
			 redirect("/admin");
		 
		 Managers form=getModel(Managers.class);
		 Managers manager=Managers.dao.findFirst("select * from managers where tel="+form.getInt("tel"));
		 if(form.getStr("password").equals(manager.getStr("password")))
		 {
			 setSessionAttr(GlobalVar.BEUSER, manager);
			 logger.info(manager.getStr("name")+"---登录后台");
			 redirect("/admin");
		 }else {
			setAttr("errorMsg", "用户名或密码错误！");
			keepModel(Managers.class);
			render("login.html");
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
	  *    添加地区
	  */
	 @Before(RingInterceptor.class)
	 public void addArea()    //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 Ring0Service ring0Service=new Ring0Service(this, manager);
		 ring0Service.addArea();
	 }
	 
	 /**
	  *    设置店长
	  */
	 public void setManager()
	 {
		 
	 }
	 
	 /**
	  *    编辑商品      包括添加，修改，删除
	  */
	 public void modifyItem()
	 {
		 
	 }
	 
	 /**
	  *     进货管理
	  */
	 public void addItemNum()
	 {
		 
	 }
}

