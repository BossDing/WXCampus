$("#upDate").on("change", function () {
    sendDate(this);
});

var currAid;
function sendAid(aid){
	currAid=aid;
}
function sendDate(){
    var currdate=document.getElementById("upDate").value;
    var date=currdate.substring(0,7);
   window.location='/mgradmin/seeAreaDetails?aid='+currAid+'&type=3&month='+date;
}

function Working(aid){
	var url='/mgradmin/setShopState';
	var dataInfo='aid='+aid+'&state=true';
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
function rest(aid){
	
	var url='/mgradmin/setShopState';
	var dataInfo='aid='+aid+'&state=false';
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

//上一页
function lastPage(aid){
	var page=parseInt(document.getElementById("page").value);
	page--;
		window.location='/mgradmin/seeAreaDetails/'+page+'?aid='+aid+'&type=1';
}
//下一页
function nextPage(aid){
	var page=parseInt(document.getElementById("page").value);
	page++;
	window.location='/mgradmin/seeAreaDetails/'+page+'?aid='+aid+'&type=1';
}

function backlatest(url){
	window.location=url;
}

function seeTrades(aid){
	window.location='/mgradmin/seeAreaDetails/1?aid='+aid+'&type=1';
}
function seeKucun(aid){
	window.location='/mgradmin/seeAreaDetails?aid='+aid+'&type=2';
}
function seeDataInfo(aid){
	window.location='/mgradmin/seeAreaDetails?aid='+aid+'&type=3';
}