/**
 * Created by ASUS on 2015/11/7.
 */
//��ȡ��Ƶ�б�
var category;
function getFood(foodclass){
	category=foodclass;
	  var dataInfo="category="+foodclass;
	    var url='/index/getItems';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data:dataInfo,
            success:backFoodList,
            error: function () {
                alert("error");
            }
        }
    );
}
//�����ɹ���������Ʒ�б�
var arr=[];
function backFoodList(data){
	var isFirst=map.get(category);
	if(isFirst==true){
		document.getElementById(category+"xiala").src="/index/images_shop/xiala.png";
		document.getElementById(category+"xiala").style.height="30px";
		map.put(category,false);
		document.getElementById(category).innerHTML="";
	    var foodList='';
	    for(var i=0;i<data.itemList.length;i++){
	    	arr[i]=data.itemList[i].iid;
	        foodList+=' <div class="food_info">'+
	            '<div class="food_info_left">'+
	            '<img src="'+data.itemList[i].icon+'" style="width:10em;height:10em;margin-left:20px">'+
	            '</div>'+
	            '<div class="food_info_center">'+
	            '<p style="color: #843534;font-size: 2em;margin-left: 10px">'+data.itemList[i].iname+'</p>'+
	            '<p style="color: #843534;font-size: 2em;margin-left: 10px">销量：'+data.itemList[i].sales+'</p>'+
	            '<p style="color: #843534;font-size: 2em;margin-left: 10px">￥'+data.itemList[i].price+'</p>'+
	            '</div>'+
	            '<div class="food_info_right" style="text-align: right">'+
	            '<p style="margin-top: 1em"><img src="/index/image_find/save.png" style="width:3.5em;height: 3.5em;margin-right: 1em " onclick=save("'+data.itemList[i].iid+'")></p>'+
	            '<p style="margin-top: 3em"><span id="'+data.itemList[i].iid+'_addspan"  onclick=addNum("'+data.itemList[i].iid+'","'+data.itemList[i].restNum+'")><img id="'+data.itemList[i].iid+'_add"  src="/index/image_find/add.png" style="width: 3.5em;height: 3.5em;float: right;margin-right: 1em"></span>'+
	            ' <span id="'+data.itemList[i].iid+'" class="kuankuan">0</span>'+
	            '<span id="'+data.itemList[i].iid+'_span"><img id="'+data.itemList[i].iid+'_reduce" src="/index/images_shop/reduce_inl.png" style="width: 3.5em;height: 3.5em;margin-right: 1em;float: right "></span></p>'+
	            '</div>'+
	            '</div>'
	           ;
	    
	    }
	    $("#"+category).append(foodList);
	    document.getElementById(category).style.backgroundColor="#f5f5f5";
	    getFoodInfo();
	}
	else{
		document.getElementById(category+"xiala").src="/index/images_shop/next_1.png";
		document.getElementById(category+"xiala").style.height="55px";
		map.put(category,true);
		document.getElementById(category).innerHTML="";
	}
	
    
}


function getFoodInfo(){
	var url='/shop/getCarts'
	 $.ajax(
		        {
		            url:url,
		            dataType: "json",
		            type: 'POST',
		            data:'',
		            success:backFoodListInfo,
		            error: function () {
		                alert("error");
		            }
		        }
		    );
}

function backFoodListInfo(json){
	for(var i=0;i<json.itemList.length;i++){
		var data=document.getElementById(json.itemList[i].iid);
		if(data!=null){
			data.innerHTML="";
			data.innerHTML=json.itemList[i].num;
			var iid=json.itemList[i].iid;
			document.getElementById(iid+'_reduce').src="/index/image_find/reduce.png";
			var test=document.getElementById(iid+'_span');
			changeOnclick(test,iid);
		}
	}
	
}
function changeOnclick(obj,iid)
{
	obj.onclick=function(){
		reduceNum(iid);
	};
}


//选择地区
function selectLocation(city,college){
	window.location="/index/area?city="+city+"&college="+college;
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
        	  if(data.Msg=="OK"){
        		  alert("收藏成功");
        	  }
        	  else{
        		  alert(data.Msg);
        	  }
          },
          error: function () {
              alert("未登录用户不能收藏");
          }
      }
  );
}

//增加数量
function addNum(iid,restnum){
	var num=parseInt(document.getElementById(iid).innerHTML);
	if(num<restnum){
	num++;
	}
	else{
		alert("卖光啦");
		document.getElementById(iid+'_add').src="/index/image_find/add_old.png";
		document.getElementById(iid+'_addspan').onclick="";
		return;
	}
	document.getElementById(iid).innerHTML="";
	document.getElementById(iid).innerHTML=num;
	if(num>0){	
		document.getElementById(iid+'_reduce').src='/index/image_find/reduce.png';
//		var obj=document.getElementById(iid+'_span');
//		obj.onclick=reduceNum(iid);
		document.getElementById(iid+'_span').onclick=function(){
			reduceNum(iid,restnum);
		};
	}
	sendajax(iid,"0");
	document.getElementById("totalNum").innerHTML=parseInt(document.getElementById("totalNum").innerHTML)+1;
}
//减少数量
function reduceNum(iid,restNum){
	var num=parseInt(document.getElementById(iid).innerHTML);
	num--;
	if(num<restNum){
		document.getElementById(iid+'_add').src="/index/image_find/add.png";
		document.getElementById(iid+'_addspan').onclick=function(){
			addNum(iid,restNum);
		};
	}
	document.getElementById(iid).innerHTML="";
	document.getElementById(iid).innerHTML=num;
	if(num<=0){
		document.getElementById(iid+'_reduce').src='/index/images_shop/reduce_inl.png';
		document.getElementById(iid+'_span').onclick='';
//		var obj=document.getElementById(iid+'_span');
//		obj.attachEvent("onclick", Foo); 
//		function Foo() 
//		{ 
//		reduceNum(iid);
//		} 
	}
	sendajax(iid,"1");
	document.getElementById("totalNum").innerHTML=parseInt(document.getElementById("totalNum").innerHTML)-1;
}

function gotoshop()
{
	var str=parseInt(document.getElementById("totalNum").innerHTML);
	if(str==0)
		alert("您尚未选择任何商品！");
	else
	    window.location='/shop';
}

function findFood(){
	window.location='/index/find';
}
function sendajax(iid,type){
	var url='/shop/incart';
	var dataInfo='iid='+iid+'&type='+type;
	$.ajax(
		      {
		          url:url,
		          dataType: "json",
		          type: 'POST',
		          data:dataInfo,
		          success:function reback(data){
		        	  console.log("success");
		          },
		          error: function () {
		        	  console.log("error");
		          }
		      }
		  );
}

function personInfo(){
	window.location='/usr';
}



function Map() {   
    /** 存放键的数组(遍历用到) */  
    this.keys = new Array();   
    /** 存放数据 */  
    this.data = new Object();   
       
    /**  
     * 放入一个键值对  
     * @param {String} key  
     * @param {Object} value  
     */  
    this.put = function(key, value) {   
        if(this.data[key] == null){   
            this.keys.push(key);   
        }   
        this.data[key] = value;   
    };   
       
    /**  
     * 获取某键对应的值  
     * @param {String} key  
     * @return {Object} value  
     */  
    this.get = function(key) {   
        return this.data[key];   
    };   
       
    /**  
     * 删除一个键值对  
     * @param {String} key  
     */  
    this.remove = function(key) {   
        this.keys.remove(key);   
        this.data[key] = null;   
    };   
       
    /**  
     * 遍历Map,执行处理函数  
     *   
     * @param {Function} 回调函数 function(key,value,index){..}  
     */  
    this.each = function(fn){   
        if(typeof fn != 'function'){   
            return;   
        }   
        var len = this.keys.length;   
        for(var i=0;i<len;i++){   
            var k = this.keys[i];   
            fn(k,this.data[k],i);   
        }   
    };   
       
    /**  
     * 获取键值数组(类似Java的entrySet())  
     * @return 键值对象{key,value}的数组  
     */  
    this.entrys = function() {   
        var len = this.keys.length;   
        var entrys = new Array(len);   
        for (var i = 0; i < len; i++) {   
            entrys[i] = {   
                key : this.keys[i],   
                value : this.data[i]   
            };   
        }   
        return entrys;   
    };   
       
    /**  
     * 判断Map是否为空  
     */  
    this.isEmpty = function() {   
        return this.keys.length == 0;   
    };   
       
    /**  
     * 获取键值对数量  
     */  
    this.size = function(){   
        return this.keys.length;   
    };   
       
    /**  
     * 重写toString   
     */  
    this.toString = function(){   
        var s = "{";   
        for(var i=0;i<this.keys.length;i++,s+=','){   
            var k = this.keys[i];   
            s += k+"="+this.data[k];   
        }   
        s+="}";   
        return s;   
    };   
}   

