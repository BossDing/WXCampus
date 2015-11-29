function modifyItem(iid){
	
}
function deleteItem(iid){
	if (confirm("确认要删除？")) {
		var url='/mgradmin/items';
		var dataInfo='del='+iid;
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
	            	}
	            },
	            error: function () {
	                alert("error");
	            }
	        }
	    );
    }
}
function addItem(){
	window.location="/mgradmin/addItem";
}