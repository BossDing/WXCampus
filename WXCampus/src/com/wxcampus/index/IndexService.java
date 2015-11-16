package com.wxcampus.index;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.wxcampus.util.Util;


public class IndexService {

	
	/**
	 * 更新营业状态̬
	 * @param areas 
	 */
	public void updateShopState(Areas areas)
	{
		String startTime=areas.get("startTime").toString();
		String endTime=areas.get("endTime").toString();
		String currentTime=Util.getTime();
		if(areas.getBoolean("state")==false)
		{
		    if(startTime.compareTo(currentTime)<0 && endTime.compareTo(currentTime)>0)
			     areas.set("state", true).update();
		}else {
			if(startTime.compareTo(currentTime)>0 || endTime.compareTo(currentTime)<0)
			     areas.set("state", false).update();
		}
	}
}
