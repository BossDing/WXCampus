package com.wxcampus.index;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IndexService {

	
	/**
	 * ���µ���Ӫҵ״̬
	 * @param areas  �������ڵ���
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
