package com.wxcampus.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class HtmlSkipHandler extends Handler { 
	  public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
	    int index = target.lastIndexOf(".html");
	  //  target=target.replaceAll("\\.((?!((css)|(png))).)+", "");

	    if (index != -1)
	     // target = target.substring(0, index);
	    	target=target.replace(".html", "");
		    System.out.println("target:"+target);
	    nextHandler.handle(target, request, response, isHandled);
	  }
	}