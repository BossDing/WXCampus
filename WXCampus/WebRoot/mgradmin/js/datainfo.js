$("#upDate").on("change", function () {
    sendDate(this);
});

function sendDate(){
    var currdate=document.getElementById("upDate").value;
    var date=currdate.substring(0,7);
   window.location='/mgradmin/datainfo?month='+date;
}