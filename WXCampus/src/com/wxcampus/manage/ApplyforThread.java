package com.wxcampus.manage;

import java.util.List;

import com.wxcampus.items.Applyfor;

public class ApplyforThread implements Runnable{

	private List<Applyfor> afList;
	public ApplyforThread(List<Applyfor> afList)
	{
		this.afList=afList;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 for(int i=0;i<afList.size();i++)
		 {
			 if(afList.get(i).getInt("state")==0)
				 afList.get(i).set("state", 1).update();
		 }
	}

}
