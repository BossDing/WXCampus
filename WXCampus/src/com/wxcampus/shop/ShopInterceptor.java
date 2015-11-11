package com.wxcampus.shop;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.wxcampus.index.Areas;

/**
 * ���������ڵ��̴���֮��ķǷ�����
 * @author Potato
 *
 */
public class ShopInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation arg0) {
		int areaID=arg0.getController().getSessionAttr("areaID");
		Areas areas=Areas.dao.findById(areaID);
		if(areas.getBoolean("state")==true)
			arg0.invoke();
		else
			arg0.getController().redirect("error.html");
		
	}

}
