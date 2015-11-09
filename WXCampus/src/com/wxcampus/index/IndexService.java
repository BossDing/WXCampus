package com.wxcampus.index;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IndexService {

	
	/**
	 * 更新店铺营业状态
	 * @param areas  店铺所在地区
	 */
	public void updateShopState(Areas areas)
	{
		String startTime=areas.getStr("startTime");
		String endTime=areas.getStr("endTime");
		String currentTime=new SimpleDateFormat("HH:mm:ss").format(new Date().toString());
		if(areas.getInt("state")==0)
		{
		    if(startTime.compareTo(currentTime)<0 && endTime.compareTo(currentTime)>0)
			     areas.set("state", 1).update();
		}else {
			if(startTime.compareTo(currentTime)>0 || endTime.compareTo(currentTime)<0)
			     areas.set("state", 0).update();
		}
	}
}
