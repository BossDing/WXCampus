package com.wxcampus.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

public class Handle404Controller extends Controller{
	
	public void index()
	{
		render("index.html");
	}
	
	@Clear
	public void error()
	{
		String Msg=getPara("Msg");
		System.out.println(Msg);
		String backURL=getPara("backurl");
		if(Msg==null)
			Msg="未知错误！";
		if(backURL==null)
			backURL="/index";
		setAttr("Msg", Msg);
		setAttr("backurl", backURL);
		render("error.html");
	}

}
