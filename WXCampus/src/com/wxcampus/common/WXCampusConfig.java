package com.wxcampus.common;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.wxcampus.index.Advertisement;
import com.wxcampus.index.Areas;
import com.wxcampus.index.IndexController;
import com.wxcampus.items.Coupons;
import com.wxcampus.items.Coupons_use;
import com.wxcampus.items.Coupons_user;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.items.Trades;
import com.wxcampus.manage.ManageController;
import com.wxcampus.manage.Managers;
import com.wxcampus.shop.ShopController;
import com.wxcampus.user.Advices;
import com.wxcampus.user.User;
import com.wxcampus.user.UserController;
/**
 * API��ʽ����
 */
public class WXCampusConfig extends JFinalConfig{
	
	/**
	 * ���ó���
	 */
	public void configConstant(Constants me) {
		// ����������Ҫ���ã�������PropKit.get(...)��ȡֵ
		PropKit.use("a_little_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
	}
	
	/**
	 * ����·��
	 */
	public void configRoute(Routes me) {
		me.add("/index", IndexController.class, "/index");	// ���������Ϊ��Controller����ͼ���·��
		me.add("/usr", UserController.class);			// ���������ʡ��ʱĬ�����һ������ֵ��ͬ���ڴ˼�Ϊ "/blog"
	    me.add("/shop",ShopController.class);
	    me.add("/mgradmin",ManageController.class);
	}
	
	/**
	 * ���ò��
	 */
	public void configPlugin(Plugins me) {
		// ����C3p0��ݿ����ӳز��
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
		me.add(c3p0Plugin);
		
		// ����ActiveRecord���
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
		arp.addMapping("user", "uid",User.class);	// ӳ��user �? Userģ��
		arp.addMapping("areas","aid", Areas.class);
		arp.addMapping("items","iid", Items.class);
		arp.addMapping("items_on_sale","iosid", Items_on_sale.class);
		arp.addMapping("advertisement","aid", Advertisement.class);
		arp.addMapping("managers", "mid",Managers.class);
		arp.addMapping("trades", "tid",Trades.class);
		arp.addMapping("coupons", "cid",Coupons.class);
		arp.addMapping("coupons_user", "cuid",Coupons_user.class);
		arp.addMapping("coupons_use", "cuid",Coupons_use.class);
		arp.addMapping("advices", "aid",Advices.class);
	}

	
	/**
	 * ����ȫ��������
	 */
	public void configInterceptor(Interceptors me) {
		//me.add(new OpenidInterceptor());    // openid����У��
		me.add(new SQLXSSPREInterceptor());  //���˷�SQL,XSS
	}
	
	/**
	 * ���ô�����
	 */
	public void configHandler(Handlers me) {
		
	}
	
	/**
	 * ����ʹ�� JFinal �ֲ��Ƽ��ķ�ʽ������Ŀ
	 * ���д� main ��������������Ŀ����main�������Է����������Class�ඨ���У���һ��Ҫ���ڴ�
	 */
	public static void main(String[] args) {
		JFinal.start("Webroot", 8080, "/", 5);
	}

}
