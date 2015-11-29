/**
 * Created by ASUS on 2015/11/26.
 */
//ȫ������


//上一页
function lastPage(){
	var page=parseInt(document.getElementById("page").value);
	page--;
	window.location='/mgradmin/dealInGoods/'+page;
}
//下一页
function nextPage(){
	var page=parseInt(document.getElementById("page").value);
	page++;
	window.location='/mgradmin/dealInGoods/'+page;
}



function disInfo(rid){
	window.location='/mgradmin/igdetails?type=1&rid='+rid;
}
function backDealIngoods(){
	window.location='/mgradmin/dealInGoods';
}
function dealIngoods(rid){
	var url='/mgradmin/confirmDealIg';
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
                alert("货物不足");
            }
        }
    );
}