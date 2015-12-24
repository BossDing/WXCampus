package com.wxcampus.wxmsg;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.wxcampus.index.Areas;
import com.wxcampus.user.User;
import com.wxcampus.util.GeneralGet;
import com.wxcampus.util.Util;

public class WXController extends Controller{

	public static Logger logger = Util.getLogger();
	public void index()
	{
		if(!valid())
			return;
		renderHtml("");
		logger.error("enter");
		InputStream inputStream=null;
		Document document=null;
		try {
			inputStream = getRequest().getInputStream();
		    // 读取输入流
			SAXReader reader = new SAXReader();
			 document= reader.read(inputStream);
			    // 得到xml根元素
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element root = document.getRootElement();
		if(root.elementText("MsgType").equals("event"))
		{
			String me=root.elementText("ToUserName");
			switch (root.elementText("Event")) {
			case "LOCATION":	
				       String openid=root.elementText("FromUserName");
				       User user=User.me.findFirst("select * from user where openid=?",openid);
				       if(user!=null && user.getInt("location")!=1)
				    	   return;
				       if(user==null)
				       {
				    	   user=new User();
				    	   user.set("tel", "").set("password", "").set("headicon", "");
				    	   user.set("openid", openid).set("location", 1);
				    	   user.set("registerDate", Util.getDate()).set("registerTime", Util.getTime());
				    	   user.save();
				    	  // user=User.me.findFirst("select * from user where openid=?",openid);
				       }

				       double laititude=Double.parseDouble(root.elementText("Latitude"));
					   double longitude=Double.parseDouble(root.elementText("Longitude"));
						String ak="73EEtGNvP9eWPfDazNkGywfD";
						String url="http://api.map.baidu.com/geocoder/v2/";
						url=url+"?output=json&ak="+ak+"&coordtype=wgs84ll&location="+laititude+","+longitude+"&pois=1";
						logger.error(url);
						String jsonStr=GeneralGet.getResponse(url);
						JSONObject json=JSONObject.parseObject(jsonStr);
						if(json.getIntValue("status")==0)
						{
							JSONObject res=JSONObject.parseObject(json.getString("result"));
							String college=res.getString("sematic_description");
							JSONObject addressComponent=res.getJSONObject("addressComponent");
							String city=addressComponent.getString("city").replace("市", "");
							if(college.indexOf("校区")!=-1)
							{
								int index=college.indexOf("内");
								if(index!=-1)
								   college=college.substring(0,index);
								else {
								   college=college.substring(0,college.indexOf("校区")+2);
								}
							}else {
								JSONArray pois=res.getJSONArray("pois");
								for(int i=0;i<pois.size();i++)
								{
									JSONObject temp=pois.getJSONObject(i);
									if(temp.getString("addr").endsWith("校区"))
									{
										college=temp.getString("addr");
										List<Areas> colleges=Areas.dao.find("select * from areas where city=?",city);
										for(int k=0;k<colleges.size();k++)
										{
											if(college.contains(colleges.get(k).getStr("college")))
											{
												college=colleges.get(k).getStr("college");
												break;
											}
										}
										break;
									}
								}
							}
							Areas areaCollege=Areas.dao.findFirst("select * from areas where college=? and building=?",college,"");
							//Areas areaCollege=null;
							if(areaCollege!=null)
							{
								user.set("location", areaCollege.getInt("aid")).update();
							}else
							{
								areaCollege=Areas.dao.findFirst("select * from areas where city=? and college=?",city,"");
								if(areaCollege!=null)
								   user.set("location", areaCollege.getInt("aid")).update();
							}
							logger.error("Address:--------------"+res.getString("sematic_description"));
							logger.error("College:--------------"+college);
						}else {
							logger.error("errorcode:--------------"+json.getIntValue("status"));
						}	
						renderHtml("");
				break;
			case "subscribe":
				String openid2=root.elementText("FromUserName");
				String content="买零食就上橘子皮！\n"
+"MIss桔 是开在校园寝室的便利店，一个提供在线预订零食、饮料等服务的自助式购物平台。一秒下单，5分钟上床哦！~\n"
+"校园合伙人和楼栋店长火爆招募中，报名请发送：姓名+性别+城市+学校校区+楼栋号+手机号+邮箱+职位（店长）"
+"亲。Miss桔 欢迎您的加入~";
				Document document2=DocumentHelper.createDocument();
				Element root2=document2.addElement("xml");
				root2.addElement("ToUserName").setText(openid2);
				root2.addElement("FromUserName").setText(me);
				root2.addElement("CreateTime").setText(System.currentTimeMillis()+"");
				root2.addElement("MsgType").setText("text");
				root2.addElement("Content").setText(content);
				renderHtml(document2.asXML());
				 break;
			default:
				break;
			}
		}
		
	}
	private boolean valid()
	{
		String token="jzptokenMISS";
		String signnature=getPara("signature");
		String timestamp=getPara("timestamp");
		String nonce=getPara("nonce");
		String echostr=getPara("echostr");
		String strs[]=new String[3];
		strs[0]=token;
		strs[1]=timestamp;
		strs[2]=nonce;
		for (int i = 1; i < strs.length; i++)
		{
			for (int j = 0; j < strs.length - i; j++)
			{
				if (strs[j].compareTo(strs[j + 1])>0)
				{
					String temp=strs[j];
					strs[j]=strs[j+1];
					strs[j+1]=temp;
				}

			}
		}
		String sign=Util.SHA1((strs[0]+strs[1]+strs[2]));
		if(sign.equals(signnature))
		{
			return true;
		}
		else
			return false;
	}
}
