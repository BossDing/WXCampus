function dealApply(aid){
	var url='/mgradmin/dealApplyIncomes';
	var datainfo='aid='+aid;
	$.ajax(
	        {
	            url:url,
	            dataType: "json",
	            type: 'POST',
	            data:datainfo,
	            success:function(json){
	            	if(json.Msg=="OK"){
	            		alert("处理成功");
	            		window.location.reload();
	            	}
	            },
	            error: function () {
	                alert("error");
	            }
	        }
	    );
}