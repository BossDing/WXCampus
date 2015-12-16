/**
 * Created by ASUS on 2015/11/11.
 */
//获取店铺简介
function getSimpleInfo(){
    var dataInfo=foodInfo;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'GET',
            data:dataInfo,
            success:backSimpleInfo,
            error: function () {
                alert("error");
            }
        }
    );
}
function backSimpleInfo(data){
    var info='<div class="simpleInfo">'+
        '<p>'+data+'</p>'+
        '</div>';
    $("#floor").append(info);
}


//点击下一步
function next(){
   window.location='/usr/wantosell';
}

//获取注册信息
function getSignInfo(){
    var building=document.getElementById("build").value;
    var loucen=document.getElementById("loucen").value;
    var username=document.getElementById("username").value;
    var tel=document.getElementById("tel").value;
    var confirm_id=document.getElementById("confirm_id").value;
   var sex= $('#sexSelect option:selected') .val();


    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'GET',
            data:dataInfo,
            success:backSignInfo,
            error: function () {
                alert("error");
            }
        }
    );
}
function backlatest_1(){
	window.location='/usr/wtsindex';
	
}
function backlatest_2(){
	window.location='/usr/wantosell';
	
}
function sendConfirm(){
	  
	    
	var tel='type=1&tel='+document.getElementById("tel").value;
	var url='/usr/vcode';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:tel,
            success:function(json){
            	alert(json.Msg);
            	 var step = 59;
            	    $('#btn').val('重新发送60');
            	    var _res = setInterval(function()
            	    {
            	        $("#btn").attr("disabled", true);//设置disabled属性
            	        $('#btn').val('重新发送'+step);
            	        step-=1;
            	        if(step <= 0){
            	            $("#btn").removeAttr("disabled"); //移除disabled属性
            	            $('#btn').val('获取验证码');
            	            clearInterval(_res);//清除setInterval
            	        }
            	  },1000);
            },
            error: function () {
                alert("error");
            }
        }
    );
   
}



