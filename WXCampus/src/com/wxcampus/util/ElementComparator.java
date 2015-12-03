package com.wxcampus.util;

import java.util.Comparator;

import org.dom4j.Element;

public class ElementComparator implements Comparator<Element>{

	@Override
	public int compare(Element arg0, Element arg1) {
		// TODO Auto-generated method stub
		String s1=arg0.getName();
		String s2=arg1.getName();
		return s1.compareTo(s2);
	}

}
