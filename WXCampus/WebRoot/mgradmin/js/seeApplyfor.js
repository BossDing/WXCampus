//上一页
function lastPage(){
	var page=parseInt(document.getElementById("page").value);
	page--;
	window.location='/mgradmin/seeApplyfor/'+page;
}
//下一页
function nextPage(){
	var page=parseInt(document.getElementById("page").value);
	page++;
	window.location='/mgradmin/seeApplyfor/'+page;
}

function dealApply(aid){
	var url='/mgradmin/dealApplyfor';
	var dataInfo='aid='+aid;
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