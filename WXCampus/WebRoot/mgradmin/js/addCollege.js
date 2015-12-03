
function seeCollege(city){
	window.location="/mgradmin/areas?city="+city;
}
function seeNext(city,college){
	window.location="/mgradmin/areas?city="+city+"&college="+college;
}
function seeMore(aid){
	window.location="/mgradmin/seeAreaDetails/1?aid="+aid+"&type=1";
}
function backCity(){
	window.location="/mgradmin/areas";
}
function backCollege(city){
	window.location="/mgradmin/areas?city="+city;
}
function submitCity(){
	var url='/mgradmin/addArea';
	var city=document.getElementById("city").value;
	var dataInfo='city='+city;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("添加成功");
            		window.location.reload();
            	}else
            		alert(json.Msg);
            },
            error: function () {
                alert("error");
            }
        }
    );
}
function submitCollege(currCity){
	var url='/mgradmin/addArea';
	var college=document.getElementById("college").value;
	var dataInfo='city='+currCity+"&college="+college;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("添加成功");
            		window.location.reload();
            	}else
            		alert(json.Msg);
            },
            error: function () {
                alert("error");
            }
        }
    );
}
function submitBuilding(currCity,currCollege){
	var url='/mgradmin/addArea';
	var building=document.getElementById("building").value;
	var dataInfo='city='+currCity+"&college="+currCollege+"&building="+building;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("添加成功");
            		window.location.reload();
            	}else
            		alert(json.Msg);
            },
            error: function () {
                alert("error");
            }
        }
    );
}
function deleteCity(aid){
	if(confirm("确认删除？")){
	var url='/mgradmin/delArea';
	var dataInfo='aid='+aid;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("删除成功");
            		window.location.reload();
            	}else
            		alert(json.Msg);
            },
            error: function () {
                alert("error");
            }
        }
    );
	}
}
function deleteCollege(aid){
	if(confirm("确认删除？")){
	var url='/mgradmin/delArea';
	var dataInfo='aid='+aid;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("删除成功");
            		window.location.reload();
            	}else
            		alert(json.Msg);
            },
            error: function () {
                alert("error");
            }
        }
    );
	}
}
function deleteBuilding(aid){
	if(confirm("确认删除？")){
	var url='/mgradmin/delArea';
	var dataInfo='aid='+aid;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("删除成功");
            		window.location.reload();
            	}else
            		alert(json.Msg);
            },
            error: function () {
                alert("error");
            }
        }
    );
	}
}


var docEle = function() {
    return document.getElementById(arguments[0]) || false;
}
function openNewDiv_college(_id,city,college) {
    var m = "mask";
    if (docEle(_id)) document.removeChild(docEle(_id));
    if (docEle(m)) document.removeChild(docEle(m));
    // 新激活图层
    var newDiv = document.createElement("div");
    newDiv.id = _id;
    newDiv.style.position = "absolute";
    newDiv.style.zIndex = "9999";
    newDiv.style.width = "420px";
    newDiv.style.height = "270px";
    newDiv.style.top = "100px";
    newDiv.style.left = (parseInt(document.body.scrollWidth) - 300) / 2 + "px"; // 屏幕居中
    newDiv.style.background = "#EFEFEF";
    newDiv.style.border = "1px solid #860001";
    newDiv.style.padding = "5px";
    var dataInfo='<div class="floor_content" style=" width: 400px;height: 250px;margin: 0px auto;text-align: center;">'+
    '<form class="bs-example bs-example-form" role="form" action="/mgradmin/setManager" method="post">'+
    '<div class="input-group">'+
    '<span class="input-group-addon">校区负责人姓名</span>'+
    '<input name="managers.name" type="text" class="form-control" placeholder="请输入姓名" style="height: 50px">'+
    '</div>'+
    '<br>'+
    '<div class="input-group">'+
    '<span class="input-group-addon">电话</span>'+
    '<input name="managers.tel" type="text" class="form-control" placeholder="请输入电话" style="height: 50px">'+
    '</div>'+
    '<br>'+
    '<input type="hidden" value='+city+' name="city">'+
    '<input type="hidden" value='+college+' name="college">'+
    '<input type="hidden" value="1" name="managers.ring">'+
    '<button type="submit" class="btn btn-default" style="width: 80px;height: 45px">提交</button>'+
    '<button type="reset" class="btn btn-default"  style="width: 80px;height: 45px;margin-left: 25px">重置</button>'+
    '<button  class="btn btn-default" id="close"  style="width: 80px;height: 45px;margin-left: 25px">关闭</button>'+
    '</form>'+
    '</div>';
    newDiv.innerHTML =dataInfo;
    document.body.appendChild(newDiv);
    // mask图层
    var newMask = document.createElement("div");
    newMask.id = m;
    newMask.style.position = "absolute";
    newMask.style.zIndex = "1";
    newMask.style.width = document.body.scrollWidth + "px";
    newMask.style.height = document.body.scrollHeight + "px";
    newMask.style.top = "0px";
    newMask.style.left = "0px";
    newMask.style.background = "#000";
    newMask.style.filter = "alpha(opacity=40)";
    newMask.style.opacity = "0.40";
    document.body.appendChild(newMask);
    // 关闭mask和新图层
    var newA=document.getElementById("close");
    newA.onclick = function() {
        document.body.removeChild(docEle(_id));
        document.body.removeChild(docEle(m));
        return false;
    }
}

function openNewDiv_building(_id,city,college,building) {
    var m = "mask";
    if (docEle(_id)) document.removeChild(docEle(_id));
    if (docEle(m)) document.removeChild(docEle(m));
    // 新激活图层
    var newDiv = document.createElement("div");
    newDiv.id = _id;
    newDiv.style.position = "absolute";
    newDiv.style.zIndex = "9999";
    newDiv.style.width = "420px";
    newDiv.style.height = "270px";
    newDiv.style.top = "100px";
    newDiv.style.left = (parseInt(document.body.scrollWidth) - 300) / 2 + "px"; // 屏幕居中
    newDiv.style.background = "#EFEFEF";
    newDiv.style.border = "1px solid #860001";
    newDiv.style.padding = "5px";
    var dataInfo='<div class="floor_content" style=" width: 400px;height: 250px;margin: 0px auto;text-align: center;">'+
    '<form class="bs-example bs-example-form" role="form" action="/mgradmin/setManager" method="post">'+
    '<div class="input-group">'+
    '<span class="input-group-addon">楼栋负责人姓名</span>'+
    '<input name="managers.name" type="text" class="form-control" placeholder="请输入姓名" style="height: 50px">'+
    '</div>'+
    '<br>'+
    '<div class="input-group">'+
    '<span class="input-group-addon">电话</span>'+
    '<input name="managers.tel" type="text" class="form-control" placeholder="请输入电话" style="height: 50px">'+
    '</div>'+
    '<br>'+
    '<input type="hidden" value='+city+' name="city">'+
    '<input type="hidden" value='+college+' name="college">'+
    '<input type="hidden" value='+building+' name="building">'+
    '<input type="hidden" value="2" name="managers.ring">'+
    '<button type="submit" class="btn btn-default" style="width: 80px;height: 45px">提交</button>'+
    '<button type="reset" class="btn btn-default"  style="width: 80px;height: 45px;margin-left: 25px">重置</button>'+
    '<button  class="btn btn-default" id="close"  style="width: 80px;height: 45px;margin-left: 25px">关闭</button>'+
    '</form>'+
    '</div>';
    newDiv.innerHTML =dataInfo;
    document.body.appendChild(newDiv);
    // mask图层
    var newMask = document.createElement("div");
    newMask.id = m;
    newMask.style.position = "absolute";
    newMask.style.zIndex = "1";
    newMask.style.width = document.body.scrollWidth + "px";
    newMask.style.height = document.body.scrollHeight + "px";
    newMask.style.top = "0px";
    newMask.style.left = "0px";
    newMask.style.background = "#000";
    newMask.style.filter = "alpha(opacity=40)";
    newMask.style.opacity = "0.40";
    document.body.appendChild(newMask);
    // 关闭mask和新图层
    var newA=document.getElementById("close");
    newA.onclick = function() {
        document.body.removeChild(docEle(_id));
        document.body.removeChild(docEle(m));
        return false;
    }
}