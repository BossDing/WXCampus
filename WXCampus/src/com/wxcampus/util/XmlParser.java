package com.wxcampus.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.InvalidXPathException;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.QName;
import org.dom4j.Text;
import org.dom4j.Visitor;
import org.dom4j.XPath;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class XmlParser {
	
	public static void main(String args[])
	{
//		Document document4=DocumentHelper.createDocument();
//		Element root4=document4.addElement("nonceStr");
//		root4.setText(Util.getRandomString());
//		String tempts=System.currentTimeMillis()+"";
//		String tempRs=Util.getRandomString();
//		Document document3=DocumentHelper.createDocument();
//		Element root3=document3.addElement("test");
//		root3.addElement("appId").setText(Util.APPID);
//		root3.addElement("timestamp").setText(tempts);
//		root3.add((Element)root4.clone());
//		System.out.println(document3.asXML());
//		Document document=DocumentHelper.createDocument();
//		Element root=document.addElement("xml");
//		root.addElement("appid").setText(Util.APPID);
//		root.addElement("mch_id").setText("");
//		root.addElement("device_info").setText("WEB");
//		root.addElement("nonce_str").setText(Util.getRandomString());
//		root.addElement("body").setText("零食");   //商品概述
//		//root.addElement("detail").setText("");  //选填
//		//root.addElement("attach").setText("");  //选填
//		root.addElement("out_trade_no").setText(Util.getTradeNo());  //订单号
//		root.addElement("fee_type").setText("CNY");
//		root.addElement("total_fee").setText("16.00");  //订单总金额
//		//root.addElement("spbill_create_ip").setText("");  //终端IP
//		//root.addElement("time_start").setText("");     //订单起始时间
//		//root.addElement("time_expire").setText("");    //订单结束时间
//		//root.addElement("goods_tag").setText("");
//		root.addElement("notify_url").setText("http://www.missjzp.cn/shop/paysuccess");
//		root.addElement("trade_type").setText("JSAPI");
//	//	root.addElement("product_id").setText("");
//	//	root.addElement("limit_pay").setText("");
//		//root.addElement("openid").setText("");
//		List<Element> elements=root.elements();
//	    String sign=Util.getSign(root);
//		root.addElement("sign").setText(sign); ////****************
//		System.out.println(document.asXML());
//		String text="<xml><appid><![CDATA[wxd930ea5d5a258f4f]]></appid><mch_id><![CDATA[10000100]]></mch_id><device_info>1000</device_info><body>test</body><nonce_str><![CDATA[ibuaiVcKdpRxkhJA]]></nonce_str><sign><![CDATA[9A0A8659F005D6984697E2CA0A9CF3B7]]></sign></xml>";
//		try {
//			Document document2=DocumentHelper.parseText(text);
//			Element root2 = document2.getRootElement();
//			List<Element> elements2=root2.elements();
//			String sign2=Util.getSign(root2);
//			System.out.println(sign2);
//			Iterator<Element> iterator=root2.elementIterator();
//			System.out.println(root2.elements().size());
//			while(iterator.hasNext())
//			{
//				Element ele=iterator.next();
//				System.out.println(ele.getName()+" : "+ele.getText());
//			}
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		String url="http://api.map.baidu.com/geocoder/v2/?output=json&ak=73EEtGNvP9eWPfDazNkGywfD&coordtype=wgs84ll&location=30.553795,103.99054&pois=1";
//		String jsonStr=GeneralGet.getResponse(url);
//		JSONObject json=JSONObject.parseObject(jsonStr);
//		JSONObject res=json.getJSONObject("result");
//		String college=res.getString("sematic_description");
//		if(college.indexOf("校区")!=-1)
//		{
//			int index=college.indexOf("内");
//			if(index!=-1)
//			   college=college.substring(0,index);
//			else {
//			   college=college.substring(0,college.indexOf("校区")+2);
//			}
//			System.out.println(college);
//		}else {
//			JSONArray pois=res.getJSONArray("pois");
//			for(int i=0;i<pois.size();i++)
//			{
//				JSONObject temp=pois.getJSONObject(i);
//				if(temp.getString("addr").endsWith("校区"))
//				{
//					System.out.println(temp.getString("addr"));
//					break;
//				}
//			}
//		}
//		  String tel="17865169783";
//		  String vcode="666666";
//	       String url="http://apis.baidu.com/kingtto_media/106sms/106sms?";
//			String para="mobile="+tel+"&content=【验证码】：您的验证码是"+vcode+"，5分钟内有效。";
//	        String result=getResponse(url+para);
//	        System.out.println(result);
//		//	String result="Success";
	     
		double totalmoney=3.6666;
		System.out.println((int)(totalmoney*100)+"");
		

		
	}
	public static String getResponse(String url)
	   {
		   HttpClient hc=new DefaultHttpClient();
			HttpGet hGet=new HttpGet(url);
			hGet.addHeader("apikey", "a697c899c902c714f19f216e8fa6e060");
	   	HttpResponse hResponse;
	    try
	  		{
	  			hResponse=hc.execute(hGet);
	  			 hGet.abort();
	  			 //return EntityUtils.toString(hResponse.getEntity());
	  			return EntityUtils.toString(hResponse.getEntity(),"utf-8");
	  		} catch (Exception e)
	  		{
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	    return null;
	   }
}