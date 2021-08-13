let userMap= new Map();
let friendMap = new Map();
const userID = document.getElementById('userID').value;
var othersideID;
var chatRoomIsOpen = false;
console.log("userID" + userID);
var userName;
var userHeadshot;
var remoteVideo = document.querySelector('video#remoteVideo');
var webSocket;
var commWebSocket;
let answerSetting_isReady = false;
let localStream;
var peer;
var searchKeyword = null;

//使用Google的stun服务器
// 	var configuration = { "iceServers": [{ "url": "stun:stun.example.org" }] };
var configuration = { "iceServers": [{ "url": "stun:stun.1und1.de:3478" }] };



$(document).ready(function(){      
    $(".sendFileToAll").click(function(){
    	console.log("hello!!!!!!!!!!!!!!!!!!!");
    	$('#sendFileToAllInput').click();
	});      	
	
    $("#sendFileToAllInput").change(function(){
    	console.log("change~~~~");
		readURL(this,"ALL", 0, true);		
	});  	
	
    $(".sendFileToOne").click(function(){
    	console.log("hello!!!!!!!!!!!!!!!!!!!");
    	$('#sendFileToOneInput').click();
	});      	
	
    $("#sendFileToOneInput").change(function(){
    	console.log("change~~~~");
		readURL(this, othersideID, 2, true);		
	}); 	
	 
});  

function readURL(input,msg_to, index, is_realtime){
	console.log(msg_to);
	if(!isCanvasSupported){
		console.log("無法用canvas");
	}else{
		compress(input, function(base64Img){						
			let msgVO = {            
					"msg_from": userID,
    	            "msg_to": msg_to,
                    "msg_img": base64Img,
                    "msg_status" : 0,
	        };
	        
	        setOwnImgInnerHTML(msgVO, index, is_realtime);  	
	        			                    
		    if(msg_to == "ALL"){
		    	msgVO["msg_type"] = "broadcast";
		    }else{
		    	msgVO["msg_type"] = "save";
		    }    									
			webSocket.send(JSON.stringify(msgVO));				
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