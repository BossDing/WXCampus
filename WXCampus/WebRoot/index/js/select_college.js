/**
 * Created by ASUS on 2015/11/12.
 */
//搜索校区
function findcollege(){
    var collegeList=document.getElementById("collegeInfo").value;
    var dataInfo='q='+collegeList;
    var url='/index/searchArea/';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:backCollegeList,
            error: function () {
                alert("error");
            }
        }
    );
}
//搜索成功，返回校区列表
function backCollegeList(data){
    document.getElementById("floor_left").innerHTML="";
    document.getElementById("floor_right").innerHTML="";
    currentSelect="";
    var collegeList='';
    for(var i=0;i<data.colleges.length;i++){
       collegeList+='<div id='+data.colleges[i].college+' class="floor_left_content"  onclick=inialBuildings("'+data.colleges[i].city+'","'+data.colleges[i].college+'")>'+
           '<p id='+data.colleges[i].college+'_1>'+data.colleges[i].college+'</p>'+
           '</div>';
    }
    $("#floor_left").append(collegeList);
}

//初始化校区列表
function inialCollege(){
	
    var url="/index/location";
    var data="city=济南";
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:data,
            success:backCollList,
            error: function () {
                alert("error");
            }
        }
    );
}
//返回校区列表
function backCollList(data){
    document.getElementById("floor_left").innerHTML="";
    var collegeList='';
    for(var i=0;i<data.colleges.length;i++){
       collegeList+='<div id='+data.colleges[i].college+' class="floor_left_content"  onclick=inialBuildings("'+data.colleges[i].college+'")>'+
           '<p>'+data.colleges[i].college+'</p>'+
           '</div>';
    }
    $("#floor_left").append(collegeList);
}

//点击返回宿舍楼列表
var currentSelect="";
var collInfo;
var cityinfo;
function inialBuildings(city,college){
	if(currentSelect==""){
	}
	else{
		document.getElementById(currentSelect+"_1").style.color="#000000";
		document.getElementById(currentSelect).style.backgroundColor="#EEEEEE";
	}
currentSelect=college;
document.getElementById(college).style.backgroundColor="#FD033E";
document.getElementById(college+"_1").style.color="#ffffff";

collInfo=college;
cityinfo=city;
 var url='/index/location';
    var dataInfo="city="+city+"&college="+college;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:backBuilList,
            error: function () {
                alert("error");
            }
        }
    );
}
//搜索成功，返回楼栋列表
function backBuilList(data){
    document.getElementById("floor_right").innerHTML="";
    var buildList='';
    for(var i=0;i<data.buildings.length;i++){
        buildList+='<div id='+data.buildings[i].building+' class="floor_right_content" onclick=tiaozhuan("'+data.buildings[i].building+'")>'+
            '<p id='+data.buildings[i].building+'_1>'+data.buildings[i].building+'</p>'+
            '</div>';
    }
    $("#floor_right").append(buildList);
}

function tiaozhuan(build){
	document.getElementById(build).style.backgroundColor="#FD033E";
	document.getElementById(build+"_1").style.color="#ffffff";
	window.location='/index?city='+cityinfo+'&college='+collInfo+'&building='+build;
}

function backshang(){
	window.location='/index/getCity';
}

//刷新界面
function refresh(){
	window.location='/index/getCity';
}