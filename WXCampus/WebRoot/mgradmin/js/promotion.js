function submitPromotion(){
	var url='/mgradmin/addPromotion';
	var promotion=document.getElementById("promotion").value;
	var dataInfo='content='+promotion;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("添加成功");
            		window.location.reload();
            	}
            },
            error: function () {
                alert("error");
            }
        }
    );
}

function Display(id){
	var url='/mgradmin/adjustIsshow';
	var dataInfo='pid='+id+'&type=1';
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
function cancelDisplay(id){
	var url='/mgradmin/adjustIsshow';
	var dataInfo='pid='+id+'&type=0';
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

function changePro(id){
	var url='/mgradmin/editPromotion';
	var promotion=document.getElementById(id).value;
	var dataInfo='content='+promotion+'&pid='+id;;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("修改成功");
            		window.location.reload();
            	}
            },
            error: function () {
                alert("error");
            }
        }
    );
}

function deletePro(id){
	if(confirm("确定要删除?")){
	var url='/mgradmin/delPromotion';
	var dataInfo='pid='+id;;
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

