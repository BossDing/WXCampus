function play_wav(){
    var div = document.getElementById('div1');
    div.innerHTML = '<audio id="bgsound" src="/audio/inform.mp3" autoplay="autoplay" hidden="true" controls="controls"></audio>';
    //setTimeout(function(){div.innerHTML='';},2000);
}

function showUnreadNews()
{
	var url='/mgradmin/inform';
    $.ajax(
        {
            url:url,
            dataType: "json",
            type: 'POST',
            data: '',
            success:function(json){
            	if(json.Msg=="YES"){
            		play_wav();
            	}
            },
            error: function () {
                
            }
        }
    );
}