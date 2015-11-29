package com.wxcampus.common;

import com.jfinal.core.Controller;

public class RediectIndexController extends Controller{

	public void index()
	{
		redirect("/index");
	}
}
