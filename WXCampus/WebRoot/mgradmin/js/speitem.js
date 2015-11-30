 $("#uploadImg").on("change", function () {
	 document.getElementById("insertImg").innerHTML="";
	 var datainfo='<img id="img" src="" style="width:60px;height:60px;">';
	 $("#insertImg").append(datainfo);
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
