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
	if(!isCanvasSupported){
		console.log("無法用canvas");
	}else{
		compress(input, function(base64Img){
			console.log(base64Img);
			setImgInnerHTML(base64Img);
			var message = {            
					"msg_from": userID,
    	            "msg_to": $("#onLineUser").val(),
                    "msg_img": base64Img,
                    "msg_status" : 0,
                    "msg_type" : "save"
	        };  		
			webSocket.send(JSON.stringify(message));				
		});
	}		
}

function isCanvasSupported() {
	let elem = document.createElement('canvas');
	return !!(elem.getContext && elem.getContext('2d'));
}

function compress(input, callback) {
	if(input.files && input.files.length >= 0) {
		for(var i = 0; i < input.files.length; i ++){
			var reader = new FileReader();
			reader.onload = function (e) {
				var image = $('<img/>');
				image.load(function () {
					console.log("開始壓縮");
					var canvas = document.createElement('canvas');
					var context = canvas.getContext('2d');
					// 等比例缩放圖片
					var scale = 1;
					var tt = 800; // 可以根据具体的要求去设定
					if (this.width > tt || this.height > tt) {
						if (this.width > this.height) {
							scale = tt / this.width;
						}else {
							scale = tt / this.height;
						}
					}
					context.width = this.width * scale;
					context.height = this.height * scale; // 計算等比缩小圖片					
					canvas.width = context.width;
					canvas.height = context.height;					 
					context.drawImage(this, 0, 0, context.width, context.height);  // 加载圖片
					var data = canvas.toDataURL('image/jpeg');
				 	//壓縮完成執行callback
			     	callback(data);
				});		
				image.attr('src', e.target.result);
    	    }
			reader.readAsDataURL(input.files[i]);	    
		}
	}else{
		let noPictures = $("<p>目前沒有圖片</p>");
		$("#preview_progressbarTW_imgs").append(noPictures);
	}				
}	