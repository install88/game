let userMap= new Map();
const userID = document.getElementById('userID').value;
console.log("userID" + userID);
var remoteVideo = document.querySelector('video#remoteVideo');
var webSocket;
var commWebSocket;
let answerSetting_isReady = false;
let localStream;
var peer;

//使用Google的stun服务器
// 	var configuration = { "iceServers": [{ "url": "stun:stun.example.org" }] };
var configuration = { "iceServers": [{ "url": "stun:stun.1und1.de:3478" }] };



$(document).ready(function(){
    $("#onLineUser").change(function(){
    	document.getElementById('message').
    	innerHTML="";
    	var msg_from = $("#onLineUser").val();
        var message = {
                "msg_from": msg_from,
                "msg_to": userID,
                "msg_type" : "getMessage"
        };
        webSocket.send(JSON.stringify(message));            
        //將未讀訊息數量改成0
        if(userMap.has(msg_from)){
        	userMap.set(msg_from,0);	
        } 
        build_chatting();
    });
    
    $("#progressbarTWInput").change(function(){
    	$("#preview_progressbarTW_imgs").html(""); // 清除預覽
		readURL(this);
	});        
});  

function readURL(input){
	if(input.files && input.files.length >= 0) {
		for(var i = 0; i < input.files.length; i ++){
			var reader = new FileReader();
			reader.onload = function (e) {
				var message = {            
					"msg_from": userID,
    	            "msg_to": $("#onLineUser").val(),
                    "msg_img": e.target.result,
                    "msg_status" : 0,
                    "msg_type" : "save"
	            };  		
				webSocket.send(JSON.stringify(message));					
    	    }
			reader.readAsDataURL(input.files[i]);	    
		}
	}else{
		let noPictures = $("<p>目前沒有圖片</p>");
		$("#preview_progressbarTW_imgs").append(noPictures);		
	}
}