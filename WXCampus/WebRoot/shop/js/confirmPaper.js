/**
 * Created by ASUS on 2015/11/12.
 */
function payFor(){
	var username=document.getElementById("username").value;
	var userroom=document.getElementById("userroom").value;
	window.location='/shop/pay?userName='+username+'&userRoom='+userroom;
}
function backGouwuche(){
	window.location='/shop';
}
function gotoCar(){
	window.location='/shop';
}