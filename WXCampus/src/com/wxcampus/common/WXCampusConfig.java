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
import com.wxcampus.items.Areasales;
import com.wxcampus.items.Coupons;
import com.wxcampus.items.Coupons_use;
import com.wxcampus.items.Coupons_user;
import com.wxcampus.items.Incomes;
import com.wxcampus.items.Informs;
import com.wxcampus.items.Items;
import com.wxcampus.items.Items_on_sale;
import com.wxcampus.items.Promotion;
import com.wxcampus.items.Settings;
import com.wxcampus.items.Trades;
import com.wxcampus.manage.ManageController;
import com.wxcampus.manage.Managers;
import com.wxcampus.shop.ShopController;
import com.wxcampus.user.Advices;
import com.wxcampus.user.User;
import com.wxcampus.user.UserController;
/**
 * API引导设置
 */
public class WXCampusConfig extends JFinalConfig{
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		//PropKit获取属性ֵ
		PropKit.use("a_little_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setError404View("/404/index.html");
	}
	
	/**
	 * 路由映射
	 */
	public void configRoute(Routes me) {
		me.add("/index", IndexController.class, "/index");	// ���������Ϊ��Controller����ͼ���·��
		me.add("/usr", UserController.class);			// ���������ʡ��ʱĬ�����һ������ֵ��ͬ���ڴ˼�Ϊ "/blog"
	    me.add("/shop",ShopController.class);
	    me.add("/mgradmin",ManageController.class);
	    me.add("/404",Handle404Controller.class);
	}
	
	/**
	 * 配置数据库
	 */
	public void configPlugin(Plugins me) {
		// c3p0连接池
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
		me.add(c3p0Plugin);
		
		// 映射数据模型
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
		arp.addMapping("user", "uid",User.class);	
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
		arp.addMapping("settings", "sid",Settings.class);
		arp.addMapping("promotion", "pid",Promotion.class);
		arp.addMapping("incomes", "iid",Incomes.class);
		arp.addMapping("informs", "iid",Informs.class);
		arp.addMapping("areasales", "asid",Areasales.class);
	}

	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		//me.add(new OpenidInterceptor());    // openid拦截
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new HtmlSkipHandler());
	}
	
	public static void main(String[] args) {
		JFinal.start("Webroot", 8080, "/", 5);
	}

}
