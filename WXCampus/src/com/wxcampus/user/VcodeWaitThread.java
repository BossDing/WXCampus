package com.wxcampus.user;

import com.jfinal.core.Controller;
import com.wxcampus.common.GlobalVar;

public class VcodeWaitThread implements Runnable{

	/**
	 *  手机验证码有效期5分钟
	 */
	private Controller c;
	public VcodeWaitThread(Controller c)
	{
		this.c=c;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i=0;i<300;i++)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		c.removeSessionAttr(GlobalVar.VCODE);
	}

}
