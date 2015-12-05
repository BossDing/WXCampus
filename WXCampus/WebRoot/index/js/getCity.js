/**
 * Created by ASUS on 2015/11/12.
 */
//搜索城市
function findcity(){
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
//搜索成功，返回城市列表
function backCityList(data){
    document.getElementById("floor_left").innerHTML="";
    var collegeList='';
    for(var i=0;i<data.colleges.length;i++){
       collegeList+='<div class="floor_left_content"  onclick=inialBuildings("'+data.colleges[i].college+'")>'+
           '<p>'+data.colleges[i].college+'</p>'+
           '</div>';
    }
    $("#floor_left").append(collegeList);
}

//初始化城市列表
function inialCity(){
	
    var url="/index/location";
    var datainfo="";
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:"",
            success:backcityList,
            error: function () {
                alert("error");
            }
        }
    );
}
//返回城市列表
function backcityList(data){
    document.getElementById("floor_left").innerHTML="";
    var collegeList='';
    for(var i=0;i<data.cities.length;i++){
       collegeList+='<div id='+data.cities[i].city+' class="floor_left_content"  onclick=inialColleges("'+data.cities[i].city+'")>'+
           '<p id='+data.cities[i].city+'_1>'+data.cities[i].city+'</p>'+
           '</div>';
    }
    $("#floor_left").append(collegeList);
}

//点击返回大学列表
var currentSelect="";
var collInfo;
function inialColleges(data){
	if(currentSelect==""){
	}
	else{
		document.getElementById(currentSelect+"_1").style.color="#000000";
		document.getElementById(currentSelect).style.backgroundColor="#EEEEEE";
	}
currentSelect=data;
document.getElementById(data).style.backgroundColor="#FD033E";
document.getElementById(data+"_1").style.color="#ffffff";
collInfo=data;
 var url='/index/location';
    var dataInfo="city="+data;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:backCollList,
            error: function () {
                alert("error");
            }
        }
    );
}
//返回大学列表
function backCollList(data){
    document.getElementById("floor_right").innerHTML="";
    var buildList='';
    for(var i=0;i<data.colleges.length;i++){
        buildList+='<div class="floor_right_content" onclick=tiaozhuan("'+collInfo+'","'+data.colleges[i].college+'")>'+
            '<p>'+data.colleges[i].college+'</p>'+
            '</div>';
    }
    $("#floor_right").append(buildList);
}

function tiaozhuan(city,college){
	window.location='/index/area?city='+city+'&college='+college;
}

//刷新界面
function refresh(){
	window.location='/index/getCity';
}

function backMainPage(){
	window.location='/index';
}