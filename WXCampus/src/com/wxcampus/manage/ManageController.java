package com.wxcampus.manage;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.wxcampus.common.GlobalVar;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexService;
import com.wxcampus.items.Trades;

/**
 * ��̨�����������
 * @author Potato
 *
 */
@Before(ManageInterceptor.class)
public class ManageController extends Controller{

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
		 Managers manager=Managers.dao.findFirst("select * from managers where tel="+form.getStr("tel"));
		 if(form.getStr("password").equals(manager.getStr("password")))
		 {
			 setSessionAttr(GlobalVar.BEUSER, manager);
			 redirect("/admin");
		 }else {
			setAttr("errorMsg", "�û������������");
			keepModel(Managers.class);
			render("login.html");
		}
	 }
	 
	 /**
	  *          �޸�����
	  */
	 public void modifyPw()  //ajax
	 {
		 String oldPass=getPara("oldPass");
		 String newPass=getPara("newPass");
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 if(oldPass.equals(manager.getStr("password")))
		 {
			 manager.set("password", newPass).update();
			 renderHtml("OK");
		 }else {
			renderHtml("ԭʼ�����������");
		}
	 }
	 
	 /**
	  *  ��ѯ����
	  */
	 public void trades()  //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
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
	  *   �趨Ӫҵʱ��
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
	  *    ��ӵ���
	  */
	 public void addArea()    //ajax
	 {
		 Managers manager=getSessionAttr(GlobalVar.BEUSER);
		 if(manager.getInt("ring")==0)
		 {
		 Ring0Service ring0Service=new Ring0Service(this, manager);
		 ring0Service.addArea();
		 }else
			 redirect("error.html");   //��Ȩ����
	 }
	 
	 /**
	  *    ���õ곤
	  */
	 public void setManager()
	 {
		 
	 }
	 
	 /**
	  *    �༭��Ʒ      ������ӣ��޸ģ�ɾ��
	  */
	 public void modifyItem()
	 {
		 
	 }
	 
	 /**
	  *     ��������
	  */
	 public void addItemNum()
	 {
		 
	 }
}

