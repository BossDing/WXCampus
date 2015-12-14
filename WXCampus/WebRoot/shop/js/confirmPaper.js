/**
 * Created by ASUS on 2015/11/12.
 */
function payFor(){
	var username=document.getElementById("username").value;
	var userroom=document.getElementById("userroom").value;
	var usertel=document.getElementById("usertel").value;
	var userInfo=document.getElementById("information").value;
	if(username==""||username==null){
		alert("请输入姓名");
		return;
	}	
	if(userroom==""||userroom==null){
		alert("请输入宿舍号");
		return;
	}
	if(usertel==null||usertel==""){
		alert("请输入手机号");
		return;
	}
	var datainfo='userName='+username+'&userRoom='+userroom+'&userTel='+usertel+'&inform='+userInfo;
	var url='/shop/pay';
	$.ajax(
	        {
	            url:url,
	            dataType: "json",
	            type: 'POST',
	            data:datainfo,
	            success:onbackCall,
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
function onbackCall(json)
{
	wx.chooseWXPay({
		'appId':     json.appid,
	    'timestamp': json.timestamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
	    'nonceStr': json.nonceStr, // 支付签名随机串，不长于 32 位
	    'package': json.packages, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
	    'signType': 'MD5', // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
	    'paySign': json.paySign, // 支付签名
	    'success': function (res) {
	        // 支付成功后的回调函数
	    	if(res.errMsg == "chooseWXPay:ok")
	    		isSussPay(true);
	    	else{
	    		alert("支付失败");
	    		isSussPay(false);
	    		window.location="/index";
	    	}
	    },
	    'cancel': function(res){
	    	isSussPay(false);
	    },
	    'fail': function(res){
	    	alert("支付失败");
	    	isSussPay(false);
	    	window.location="/index";
	    }
	});
//    if (typeof(WeixinJSBridge) == "undefined"){
//        alert('enter');
//       if( document.addEventListener ){
//           document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
//       }else if (document.attachEvent){
//           document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
//           document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
//       }else{
//    	   alert('payenter');
//    	   onBridgeReady(data);
//       }
//    }
}
//function onBridgeReady(data){
//	   WeixinJSBridge.invoke(
//	       'getBrandWCPayRequest', {
//	           "appId" ： data.appid,     //公众号名称，由商户传入     
//	           "timeStamp"：data.timpstamp,         //时间戳，自1970年以来的秒数     
//	           "nonceStr" ： data.nonceStr, //随机串     
//	           "package" ： data.packages,     
//	           "signType" ： "MD5",         //微信签名方式：     
//	           "paySign" ： data.paySign //微信签名 
//	       },
//	       function(res){     
//	           if(res.err_msg == "get_brand_wcpay_request：ok" ) {
//	        	   window.location="/usr/trades/1";
//	           }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
//	       }
//	   ); 
//	}

	function isSussPay(succ){
		
		var url='/shop/payinform';
		var datainfo='success='+succ;
		$.ajax(
		        {
		            url:url,
		            dataType: "json",
		            type: 'POST',
		            data:datainfo,
		            success:function(data){
		            	if(data.Msg=="OK"){
		            		window.location="/usr/trades/1";
		            	}else{
		            		
		            	}
		            },
		            error: function () {
		                alert("error");
		            }
		        }
		    );
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