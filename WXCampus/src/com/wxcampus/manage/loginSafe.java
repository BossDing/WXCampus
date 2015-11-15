package com.wxcampus.manage;


import com.jfinal.core.Controller;

public class loginSafe implements Runnable{
	String tel;
	Controller c;
	public loginSafe(String tel,Controller c)
	{
		this.tel=tel;
		this.c=c;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ManageLoginSafe.add(tel, this);
		
		try {
			Thread.sleep(600000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ManageLoginSafe.remove(tel, this);
		c.removeSessionAttr(tel);
	}

}
