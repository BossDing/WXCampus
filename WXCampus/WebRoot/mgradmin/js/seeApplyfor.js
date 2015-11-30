//上一页
function lastPage(){
	var page=parseInt(document.getElementById("page").value);
	page--;
	window.location='/mgradmin/seeApplyfor/'+page;
}
//下一页
function nextPage(){
	var page=parseInt(document.getElementById("page").value);
	page++;
	window.location='/mgradmin/seeApplyfor/'+page;
}
