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
function backSignInfo(){
	
}
function sendConfirm(){
	var tel='tel='+document.getElementById("tel").value;
	var url='/usr/vcode';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:tel,
            success:function(json){
            	alert(json.Msg);
            },
            error: function () {
                alert("error");
            }
        }
    );
}