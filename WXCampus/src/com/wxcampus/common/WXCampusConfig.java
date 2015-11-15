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
 * API引导式配置
 */
public class WXCampusConfig extends JFinalConfig{
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("a_little_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", IndexController.class, "/index");	// 第三个参数为该Controller的视图存放路径
		me.add("/usr", UserController.class);			// 第三个参数省略时默认与第一个参数值相同，在此即为 "/blog"
	    me.add("/shop",ShopController.class);
	    me.add("/mgradmin",ManageController.class);
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
		me.add(c3p0Plugin);
		
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
		arp.addMapping("user", "uid",User.class);	// 映射user 表到 User模型
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
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		//me.add(new OpenidInterceptor());    // openid拦截校验
		me.add(new SQLXSSPREInterceptor());  //过滤防SQL,XSS
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		
	}
	
	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("Webroot", 8080, "/", 5);
	}

}
