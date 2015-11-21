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
    document.getElementById("floor").innerHTML="";
    var data='<div class="floor_content">'+
        '<p><span class="span_name">楼栋</span><input type="text" class="span_input" id="build"></p>'+
        '<p><span class="span_name">楼层</span><input type="text" class="span_input" id="loucen"></p>'+
        '<p><span class="span_name">姓名</span><input type="text" class="span_input" id="username"></p>'+
        '<p><span class="span_name">性别</span><span>'+
            '<select class="span_select" id="sexSelect">'+
            '<option style="width:200px;height: 40px">男</option>'+
            '<option style="width:200px;height: 40px">女</option>'+
            '</select>'+
            '</span></p>'+
        '<p><span class="span_name">手机</span><input type="text" class="span_input" id="tel"></p>'+
        '<p><span class="span_name">验证码</span><input type="text" class="span_confirm" id="confirm_id"><button class="button_send">发送验证码</button></p>'+
        '</div>';
    $("#floor").append(data);
    document.getElementById("apply_img").src="image_KTstore/apply_curr.png";
    document.getElementById("know_img").src="image_KTstore/know_old.png";
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
