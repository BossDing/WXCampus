/**
 * Created by ASUS on 2015/11/16.
 */
//������֤��
function sendConfirm(){
	var tel=document.getElementById("username").value;
	var dataInfo="tel="+tel;
	var url='/usr/vcode';
	$.ajax(
	        {
	            url:url,
	            dataType: "json",
	            type: 'POST',
	            data:dataInfo,
	            success:backSuccess,
	            error: function () {
	                alert("error");
	            }
	        }
	    );
    
}
function backSuccess(data){
        alert(data.Msg);
}
//����ע��
function signLiji(){
    var username=document.getElementById("username").value;
    var password=document.getElementById("password").value;
    var confirm_id=document.getElementById("confirm_id").value;
    var url;

    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'GET',
            data:dataInfo,
            success:backSuccSign,
            error: function () {
                alert("error");
            }
        }
    );
}

function backSuccSign(){

}