/**
 * Created by ASUS on 2015/11/24.
 */
//初始化校区列表
function getCityList(){
    var dataInfo='';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'GET',
            data:dataInfo,
            success:backCityList,
            error: function () {
                alert("error");
            }
        }
    );
}
function backCityList(data){
    var dataMess=' <table class="table table-striped" id="list">'+
        '<caption style="text-align: center">地区列表</caption>'+
        '<thead>'+
        '<tr>'+
        '<th style="text-align: center">地区</th>'+
        '<th style="text-align: center">XX</th>'+
        '<th style="text-align: center">XX</th>'+
        '</tr>'+
        '</thead>'+
        '<tbody id="cityList">';
    for(var i=0;i<data.length;i++){
        dataMess+='<tr>'+
            '<td>Tanmay</td>'+
            '<td>Bangalore</td>'+
            '<td>560001</td>'+
            '</tr>';
    }
    dataMess+='</tbody>';
    document.getElementById("list").innerHTML="";
    $("#list").append(dataMess);
}

//获取校区列表
function getSchoolList(){
    var dataInfo='';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'GET',
            data:dataInfo,
            success:backSchoolList,
            error: function () {
                alert("error");
            }
        }
    );
}
function backSchoolList(data){
    var dataMess=' <table class="table table-striped" id="list">'+
        '<caption style="text-align: center">校区列表</caption>'+
        '<thead>'+
        '<tr>'+
        '<th style="text-align: center">校区</th>'+
        '<th style="text-align: center">XX</th>'+
        '<th style="text-align: center">XX</th>'+
        '</tr>'+
        '</thead>'+
        '<tbody id="cityList">';
    for(var i=0;i<data.length;i++){
        dataMess+='<tr>'+
            '<td>Tanmay</td>'+
            '<td>Bangalore</td>'+
            '<td>560001</td>'+
            '</tr>';
    }
    dataMess+='</tbody>';
    document.getElementById("list").innerHTML="";
    $("#list").append(dataMess);
}

//获取宿舍楼栋列表
function getBuildList(){
    var dataInfo='';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'GET',
            data:dataInfo,
            success:backBuildList,
            error: function () {
                alert("error");
            }
        }
    );
}
function backBuildList(data){
    var dataMess=' <table class="table table-striped" id="list">'+
        '<caption style="text-align: center">宿舍列表</caption>'+
        '<thead>'+
        '<tr>'+
        '<th style="text-align: center">宿舍</th>'+
        '<th style="text-align: center">XX</th>'+
        '<th style="text-align: center">XX</th>'+
        '</tr>'+
        '</thead>'+
        '<tbody id="cityList">';
    for(var i=0;i<data.length;i++){
        dataMess+='<tr>'+
            '<td>Tanmay</td>'+
            '<td>Bangalore</td>'+
            '<td>560001</td>'+
            '</tr>';
    }
    dataMess+='</tbody>';
    document.getElementById("list").innerHTML="";
    $("#list").append(dataMess);
}