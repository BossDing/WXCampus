/**
 * Created by ASUS on 2015/11/7.
 */
//��ȡ��Ƶ�б�
function getFood(foodclass){
	  var dataInfo=foodclass;
	    var url='/index/getItems';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'GET',
            data:dataInfo,
            success:backFoodList,
            error: function () {
                alert("error");
            }
        }
    );
}
//�����ɹ���������Ʒ�б�
function backFoodList(data){
    document.getElementById("bottom").innerHTML="";
    var foodList='';
    for(var i=0;i<data.itemList.length;i++){
        foodList+=' <div class="food_info">'+
            '<div class="food_info_left">'+
            '<img src="'+data.itemList[i].icon+'" style="width:10em;height:10em;margin-left:20px">'+
            '</div>'+
            '<div class="food_info_center">'+
            '<p style="color: #843534;font-size: 2em;margin-left: 10px">'+data.itemList[i].iname+'</p>'+
            '<p style="color: #843534;font-size: 2em;margin-left: 10px">'+data.itemList[i].restNum+'</p>'+
            '<p style="color: #843534;font-size: 2em;margin-left: 10px">'+data.itemList[i].realPrice+'￥</p>'+
            '</div>'+
            '<div class="food_info_right" style="text-align: right">'+
            '<p style="margin-top: 1em"><img src="/index/image_find/save.png" style="width:3em;height: 3em;margin-right: 1em " onclick=save("'+data.itemList[i].iid+'")></p>'+
            '<p style="margin-top: 3em"><span><img src="/index/image_find/add.png" style="width: 2.5em;height: 2.5em;float: right;margin-right: 1em"></span>'+
            ' <span id="kuankuan">2</span>'+
            '<span><img src="/index/image_find/reduce.png" style="width: 2.5em;height: 2.5em;margin-right: 1em;float: right "></span></p>'+
            '</div>'+
            '</div>'+
            '<HR style="float: left;width: 100%;height: 2px;color: #080808">';
    }
    $("#bottom").append(foodList);
}

//选择地区
function selectLocation(){
	window.location="/index/area.html";
}
//收藏
function save(iid){
	var dataInfo="iid="+iid;
	var url='/usr/addItemStar';
  $.ajax(
      {
          url:url,
          dataType: "json",
          type: 'POST',
          data:dataInfo,
          success:function reback(data){
        	  if(data=="OK"){
        		  alert("收藏成功");
        	  }
        	  else{
        		  alert("收藏失败");
        	  }
          },
          error: function () {
              alert("error");
          }
      }
  );
}


