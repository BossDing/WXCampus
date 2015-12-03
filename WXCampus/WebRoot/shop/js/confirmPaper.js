/**
 * Created by ASUS on 2015/11/12.
 */
function payFor(){
	var username=document.getElementById("username").value;
	var userroom=document.getElementById("userroom").value;
	var datainfo='userName='+username+'&userRoom='+userroom;
	var url='/shop/pay';
	$.ajax(
	        {
	            url:url,
	            dataType: "json",
	            type: 'POST',
	            data:datainfo,
	            success:onBridgeReady,
	            error: function () {
	                alert("error");
	            }
	        }
	    );
}
function backGouwuche(){
	window.location='/shop';
}
function gotoCar(){
	window.location='/shop';
}
//function newSign(){
//	var timestamp = Date.parse(new Date());
//	var nonceStr=randomString(32);
//	var stringA="appid=wxd930ea5d5a258f4f&nonceStr="+nonceStr+"&package="+package_1+"&timeStamp="+timeStamp;
//	var stringSignTemp="stringA&key="+key;
//	var sign=MD5(stringSignTemp).toUpperCase();
//}
//


function onBridgeReady(data){
	   WeixinJSBridge.invoke(
	       'getBrandWCPayRequest', {
	           "appId" ： data.appid,     //公众号名称，由商户传入     
	           "timeStamp"：data.timpstamp,         //时间戳，自1970年以来的秒数     
	           "nonceStr" ： data.nonceStr, //随机串     
	           "package" ： data.packages,     
	           "signType" ： "MD5",         //微信签名方式：     
	           "paySign" ： data.paySign //微信签名 
	       },
	       function(res){     
	           if(res.err_msg == "get_brand_wcpay_request：ok" ) {
	        	   window.location="/usr/trades/1";
	           }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
	       }
	   ); 
	}
	if (typeof WeixinJSBridge == "undefined"){
	   if( document.addEventListener ){
	       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
	   }else if (document.attachEvent){
	       document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
	       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
	   }
	}else{
	   onBridgeReady();
	}
	
	
//function randomString(len) {
//		　　len = len || 32;
//		　　var $chars = 'ABCDEFGHIJKLMNOPQRSTUWXYZabcdefghijklmnopqrstuwxyz012345678';  
//		　　var maxPos = $chars.length;
//		　　var pwd = '';
//		　　for (i = 0; i < len; i++) {
//		　　　　pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
//		　　}
//		　　return pwd;
//}