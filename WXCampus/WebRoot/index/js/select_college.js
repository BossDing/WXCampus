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
    var collegeList='';
    for(var i=0;i<data.colleges.length;i++){
       collegeList+='<div class="floor_left_content"  onclick=inialBuildings("'+data.colleges[i].college+'")>'+
           '<p>'+data.colleges[i].college+'</p>'+
           '</div>';
    }
    $("#floor_left").append(collegeList);
}

//初始化校区列表
function inialCollege(){
	
    var url="/index/location";
    var data="city=城市";
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
	console.log(data);
    document.getElementById("floor_left").innerHTML="";
    var collegeList='';
    for(var i=0;i<data.colleges.length;i++){
       collegeList+='<div class="floor_left_content"  onclick=inialBuildings("'+data.colleges[i].college+'")>'+
           '<p>'+data.colleges[i].college+'</p>'+
           '</div>';
    }
    $("#floor_left").append(collegeList);
}

//点击返回宿舍楼列表
var collInfo;
function inialBuildings(data){
	collInfo=data;
 var url='/index/location';
    var dataInfo="city=城市&college="+data;
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
        buildList+='<div class="floor_right_content" onclick=tiaozhuan("'+collInfo+'","'+data.buildings[i].building+'")>'+
            '<p>'+data.buildings[i].building+'</p>'+
            '</div>';
    }
    $("#floor_right").append(buildList);
}

function tiaozhuan(college,build){
	window.location='/index?city=城市&college='+college+'&building='+build;
}