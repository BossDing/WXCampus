var docEle = function() {
    return document.getElementById(arguments[0]) || false;
}
var imagedata;
function uploadImg(_id) {
    var m = "mask";
    if (docEle(_id)) document.removeChild(docEle(_id));
    if (docEle(m)) document.removeChild(docEle(m));
    // 新激活图层
    var newDiv = document.createElement("div");
    newDiv.id = _id;
    newDiv.style.position = "absolute";
    newDiv.style.zIndex = "9999";
    newDiv.style.width = "420px";
    newDiv.style.height = "370px";
    newDiv.style.top = "100px";
    newDiv.style.left = (parseInt(document.body.scrollWidth) - 300) / 2 + "px"; // 屏幕居中
    newDiv.style.background = "#EFEFEF";
    newDiv.style.border = "1px solid #860001";
    newDiv.style.padding = "5px";
    var dataInfo=
        '<form action="/mgradmin/uploadAd" method="post" enctype="multipart/form-data">'+
        '<div style="width: 400px;height: 350px;float: left">'+
        '<div style="width: 420px;height: 50px;float: left ">'+
        ' <p><input type="file" id="upImg" style="margin-left: 50px" name="adimg">'+
        '<button id="close" style="top:10px;right: 20px;position: absolute;">关闭</button></p>'+
        '</div>'+
        '<div style="width: 400px;height: 250px ;float: left">'+
        '<img src="" style="width: 250px;height: 250px;margin-left: 50px" id="img">'+
        '</div>'+
        '<div style="width: 400px;height: 50px ;float: left;text-align: center">'+
        '<button style="margin-top: 10px" type="submit">上传</button>'+
        '</div>'+
        '</div></form>';
    newDiv.innerHTML =dataInfo;
    document.body.appendChild(newDiv);
    // mask图层
    var newMask = document.createElement("div");
    newMask.id = m;
    newMask.style.position = "absolute";
    newMask.style.zIndex = "1";
    newMask.style.width = document.body.scrollWidth + "px";
    newMask.style.height = document.body.scrollHeight + "px";
    newMask.style.top = "0px";
    newMask.style.left = "0px";
    newMask.style.background = "#000";
    newMask.style.filter = "alpha(opacity=40)";
    newMask.style.opacity = "0.40";
    document.body.appendChild(newMask);
    // 关闭mask和新图层
    var newA=document.getElementById("close");
    newA.onclick = function() {
        document.body.removeChild(docEle(_id));
        document.body.removeChild(docEle(m));
        return false;
    }

    //将选择的图片显示在div框里
    $("#upImg").on("change", function () {
        previewImage(this);
    });
    function previewImage(file){
        if(file.files&&file.files[0]){

            var reader=new FileReader();
            reader.onload= function (event) {
                $("#img").attr("src",event.target.result);
                var str=event.target.result;
                imagedata=event.target.result;
            };
            reader.readAsDataURL(file.files[0]);
        }
    }
}

function deleteImg(id){
	if (confirm("确认要删除？")) {
		var url='/mgradmin/delAd';
		var dataInfo='astid='+id;
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
