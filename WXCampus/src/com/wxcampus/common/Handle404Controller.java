package com.wxcampus.common;

import com.jfinal.core.Controller;

public class Handle404Controller extends Controller{
	
	public void index()
	{
		render("index.html");
	}

}
