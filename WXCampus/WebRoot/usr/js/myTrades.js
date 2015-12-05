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

function nextPage(page){
	var page_1=parseInt(page);
	page_1++;
	window.location='/usr/trades/'+page_1;
}
function lastPage(page){
	var page_1=parseInt(page);
	page_1--;
	window.location='/usr/trades/'+page_1;
}