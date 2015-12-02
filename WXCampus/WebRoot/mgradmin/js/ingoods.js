/**
 * Created by ASUS on 2015/11/26.
 */
//ȫ������


//上一页
function lastPage(){
	var page=parseInt(document.getElementById("page").value);
	page--;
	window.location='/mgradmin/seeInGoods/'+page;
}
//下一页
function nextPage(){
	var page=parseInt(document.getElementById("page").value);
	page++;
	window.location='/mgradmin/seeInGoods/'+page;
}

//提交订单
function submitTrade(){
	
	var url='/mgradmin/seeItems';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:'',
            success:backInsertInfo,
            error: function () {
                alert("error");
            }
        }
    );
}

var arr=[];
function backInsertInfo(json){
	var data='<table class="table table-striped" id="list" style="text-align: center">'+
    '<caption style="text-align: center">商品列表</caption>'+
    '<thead>'+
    '<tr>'+
    '<th style="text-align: center">编号</th>'+
    '<th style="text-align: center">名称</th>'+
   '<th style="text-align: center">价格</th>'+
   '<th style="text-align: center">库存</th>'+
    '<th style="text-align: center">数量</th>'+
    '<th style="text-align: center">勾选</th>'+
    '</tr>'+
    '</thead>'+
    '<tbody>';
	for(var i=0;i<json.itemList.length;i++){
		arr[i]=json.itemList[i].iid;
		data+='<tr>'+
	    '<td>'+json.itemList[i].iid+'</td>'+
	    '<td>'+json.itemList[i].iname+'</td>'+
	    '<td>'+json.itemList[i].realPrice+'</td>'+
	    '<td>'+json.itemList[i].restNum+'</td>'+
	    '<td><input id='+json.itemList[i].iid+' type="number" style="width: 60px"></td>'+
	        '<td><input id='+json.itemList[i].iid+'_cbox type="checkbox"></td></tr>';
	}
        data+='</tbody>'+
        '</table>'+
        ' <div style="width:100%;height:50px;float:left;text-align:center">'+
                '<p><button style="margin-top:15px" onclick="confirmTrade()">确定提交</button>'+
          '</div>'
        ;
$("#insertFood").append(data);
}

//确认订单
function confirmTrade(){
	var str='';
	for(var i=0;i<arr.length;i++){
		var temp=document.getElementById(arr[i]+"_cbox");
		if(temp.checked==true){
			var numbe=document.getElementById(arr[i]).value;
			str+=arr[i]+":"+numbe+";";
		}
	}
	var content="content="+str;
	var url='/mgradmin/ingoods';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:content,
            success:function(json){
            	if(json.Msg=="OK"){
            		alert("提交成功");
            		window.location.reload();
            	}else
            		alert(json.Msg);
            },
            error: function () {
                alert("货物不足");
            }
        }
    );
}

function disInfo(rid){
	window.location='/mgradmin/igdetails?rid='+rid;
}
function backIngoods(){
	window.location='/mgradmin/seeInGoods';
}

function ConfirmInfo(rid){
	var url='/mgradmin/confirmIngoods';
	var dataInfo='rid='+rid;
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
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