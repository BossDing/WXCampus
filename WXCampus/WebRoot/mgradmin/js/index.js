/**
 * Created by ASUS on 2015/11/26.
 */
//ȫ������
function getAllList(){

}
//Ӫҵ
function Working(){
	var url='/mgradmin/setShopState';
	var dataInfo='state=true';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:backWorkingInfo,
            error: function () {
                alert("error");
            }
        }
    );
}
function backWorkingInfo(json){
    if(json.Msg=="OK"){
        document.getElementById("working").style.backgroundColor="#F6EB13";
        document.getElementById("rest").style.backgroundColor="#9d9d9d";
    }
    else{
        alert("error");
    }
}

//��Ϣ
function rest(){
	
	var url='/mgradmin/setShopState';
	var dataInfo='state=false';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:backRestInfo,
            error: function () {
                alert("error");
            }
        }
    );
}
function backRestInfo(json){
    if(json.Msg=="OK"){
        document.getElementById("working").style.backgroundColor="#9d9d9d";
        document.getElementById("rest").style.backgroundColor="#F6EB13";
    }
    else{
        alert("error");
    }
}

//������ڿ�ı仯
$("#upDate").on("change", function () {
    sendDate(this);
});

function sendDate(){
    var currdate=document.getElementById("upDate").value;
   window.location='/mgradmin?date='+currdate;
}

//未处理订单
function newTrades(){
	window.location='/mgradmin?state=0&date='+cuDate;
}
//已处理订单
function oldTrades(){
	window.location='/mgradmin?state=1&date='+cuDate;
}
//全部订单
function allTrades(){
	window.location='/mgradmin?date='+cuDate;
}
var cuDate='';
var cuState='';
function sendDateInfo(date,state){
	cuDate=date;
	cuState=state;
}

//上一页
function lastPage(){
	var page=parseInt(document.getElementById("page").value);
	page--;
	if(cuState==0){
		window.location='/mgradmin/'+page+'?date='+cuDate;
	}
	else if(cuState==1){
		window.location='/mgradmin/'+page+'?state=0&date='+cuDate;
	}
	else{
		window.location='/mgradmin/'+page+'?state=1&date='+cuDate;
	}
}
//下一页
function nextPage(){
	var page=parseInt(document.getElementById("page").value);
	page++;
	if(cuState==0){
		window.location='/mgradmin/'+page+'?date='+cuDate;
	}
	else if(cuState==1){
		window.location='/mgradmin/'+page+'?state=0&date='+cuDate;
	}
	else{
		window.location='/mgradmin/'+page+'?state=1&date='+cuDate;
	}
}
//处理订单
function dealTrade(rid){
	var url='/mgradmin/confirmTrade';
	var dataInfo='rid='+rid;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		window.location.reload();
            	}
            },
            error: function () {
                alert("error");
            }
        }
    );
}

function applyCash(){
	var url='/mgradmin/applyIncome';
	
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:'',
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("申请成功");
            	}
            	else{
            		alert(json.Msg);
            	}
            },
            error: function () {
                alert("error");
            }
        }
    );
}

function submitAlipay(){
var url='/mgradmin/setAlipayCard';
var datainfo=document.getElementById("alipay").value;	
var data_1="alipayNo="+datainfo;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:data_1,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("设置成功");
            		window.location.reload();
            	}
            },
            error: function () {
                alert("error");
            }
        }
    );
}