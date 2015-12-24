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

document.getElementById("btn_send").onclick=function(){
	if(document.getElementById("tel").value!=""){
		 sendConfirm();
		    time(this);
	}
	else{
		alert("手机号不能为空！");
	}
};

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
//            	var obj=document.getElementById("btn");
//            	    time(obj);
            },
            error: function () {
                alert("error");
            }
        }
    );
   
}

var wait=60;
function time(o) {
    if (wait == 0) {
    	 o.onclick=function(){
    		 sendConfirm();
 		};
 		  o.style.backgroundColor="#FD033E";
        o.innerHTML="";
        o.innerHTML="发送验证码";
        wait = 60;
    } else {
        o.onclick="";
        o.style.backgroundColor="#AAAAAB";
        o.innerHTML="";
        o.innerHTML="重新发送(" + wait + ")";
        wait--;
        setTimeout(function() {
                time(o);
            },
        1000);
    }
}

