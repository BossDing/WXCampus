/**
 * Created by ASUS on 2015/11/27.
 */
function changePrice(iid){
	var url='/mgradmin/setPrice';
	var dataInfo='iosid='+iid+'&price='+document.getElementById(iid).value;
	$.ajax(
	        {
	            url:url,
	            dataType: "json",
	            type: 'POST',
	            data:dataInfo,
	            success:function(json){
	            	if(json.Msg!="OK"){
	            		document.getElementById(iid+"_msg").innerHTML=json.Msg;
	            	}
	            	else{
	            		document.getElementById(iid+"_msg").innerHTML="修改成功";
	            	}
	            },
	            error: function () {
	                alert("error");
	            }
	        }
	    );
}
function changeStartPrice(){
	var url='/mgradmin/setStartPrice';
	var dataInfo='price='+document.getElementById("startPrice").value;
	$.ajax(
	        {
	            url:url,
	            dataType: "json",
	            type: 'POST',
	            data:dataInfo,
	            success:function(json){
	            	if(json.Msg=="OK"){
	            		alert("修改成功");
	            	}
	            },
	            error: function () {
	                alert("error");
	            }
	        }
	    );
}

function changeNum(iid){
	var url='/mgradmin/modifyRestNum';
	var dataInfo='iosid='+iid+'&restNum='+document.getElementById(iid).value;
	$.ajax(
	        {
	            url:url,
	            dataType: "json",
	            type: 'POST',
	            data:dataInfo,
	            success:function(json){
	            	if(json.Msg!="OK"){
	            		document.getElementById(iid+"_msg").innerHTML=json.Msg;
	            	}
	            	else{
	            		document.getElementById(iid+"_msg").innerHTML="修改成功";
	            	}
	            },
	            error: function () {
	                alert("error");
	            }
	        }
	    );
}

function backGetSeller(){
	window.location='/mgradmin/getSellers'
}