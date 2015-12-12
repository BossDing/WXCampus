package com.wxcampus.wxmsg;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
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
			switch (root.elementText("Event")) {
			case "LOCATION":	
				       String openid=root.elementText("FromUserName");
				       User user=User.me.findFirst("select * from user where openid=?",openid);
				       if(user==null)
				       {
				    	   user=new User();
				    	   user.set("tel", "").set("password", "").set("headicon", "");
				    	   user.set("openid", openid);
				    	   user.set("registerDate", Util.getDate()).set("registerTime", Util.getTime());
				    	   user.save();
				       }
				       if(user.getInt("location")!=1)
				    	   return;
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
							Areas areaCollege=Areas.dao.findFirst("select * from areas where college=?",college);
							if(areaCollege!=null)
							{
								user.set("location", areaCollege.getInt("aid")).update();
							}
							logger.error("Address:--------------"+res.getString("sematic_description"));
							logger.error("College:--------------"+college);
						}else {
							logger.error("errorcode:--------------"+json.getIntValue("status"));
						}				
				break;

			default:
				break;
			}
		}
	}
}
