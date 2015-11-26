/**
 * Created by ASUS on 2015/11/26.
 */
function backPerson(){
	window.location='/usr';
}
function selectAll(){
	window.location='/usr/trades/1';
}
function selectOld(){
	window.location='/usr/trades/1?state=1';
}
function selectNew(){
	window.location='/usr/trades/1?state=0';
}
function trade_info(rid){
	window.location='/usr/spetrade?rid='+rid;
}