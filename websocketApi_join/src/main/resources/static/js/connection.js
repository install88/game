if ("WebSocket" in window) {
	//ws://localhost:8080/websocket/{userID}
    webSocket = new WebSocket("ws://192.168.50.21:8080/websocket/" + userID);        
    //連通之後觸發
    webSocket.onopen = function () {
		console.log("已經連通了websocket");
    };

	//接收後端sever發送的消息
    webSocket.onmessage = function(evt) {
		console.log(evt);   		    			
        var received_msg = evt.data;
        console.log("數據已接收:" + received_msg);
        var obj = JSON.parse(received_msg);                	    
        var msg_type = Object.keys(obj)[0];
        console.log("msg_type=" + msg_type);
        switch(msg_type){
            case "showLastMsg"://顯示朋友的最後一筆訊息
                obj[msg_type].forEach(function(msgVO){
    				console.log("發送人:" + msgVO.msg_from);
    				console.log("接收人:" + msgVO.msg_to);
    				console.log("訊息: "  + msgVO.msg_content);
    				console.log("狀態: "  + msgVO.msg_status);
    				console.log("時間: "  + new Date(msgVO.msg_time));
    				console.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					if(msgVO.msg_content){
						setMessageInnerHTML(msgVO.msg_from_user_name + ":" + msgVO.msg_content);	
					}else{
						setImgInnerHTML(msgVO.msg_img);
					}	    				
                });   
            	break;
			case "showMsgCount": //顯示朋友的未讀訊息筆數
                obj[msg_type].forEach(function(msgVO){
    				console.log("發送人:" + msgVO.msg_from);
    				console.log("筆數: "  + msgVO.msg_count);
    				console.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    				userMap.set(msgVO.msg_from,msgVO.msg_count);	    				
    				setMessageInnerHTML(msgVO.username + ":" + userMap.get(msgVO.msg_from) + "筆未讀");
                });  
            	break;                	
			case "showRecord"://顯示與該朋友的對話
				let haveRead = false; 
                obj[msg_type].forEach(function(msgVO){
                	if(haveRead == false && msgVO.msg_status == 0 && msgVO.msg_to == userID){
                		haveRead = true;
                		setMessageInnerHTML("下列未讀");
						if(msgVO.msg_content){
							setMessageInnerHTML_adjustBar(msgVO.msg_from_user_name + ":" + msgVO.msg_content);
						}else{
							setImgInnerHTML(msgVO.msg_img);
						}		                			                		
                	}else{
						if(msgVO.msg_content){
							setMessageInnerHTML(msgVO.msg_from_user_name + ":" + msgVO.msg_content);
						}else{
							setImgInnerHTML(msgVO.msg_img);
						}	                			                			
                	}
                });
                //若訊息都是已讀狀態，將scrollbar拉至最底下。
                if(haveRead == false){
                	document.getElementById('inner-container').scrollTop = document.getElementById('inner-container').scrollHeight;
                }
            	break;                	                	
			case "receive_others_msg"://在線時，接收訊息。
				console.log("receive_others_msg有進來");
            	var msgVO = obj[msg_type];
				if(msgVO.msg_content){
					console.log("msg_content有進來");	
					setMessageInnerHTML(msgVO.msg_from_user_name + ":" + msgVO.msg_content);	
				}else{
					console.log("msg_img有進來");
					setImgInnerHTML(msgVO.msg_img);
				}
            	
            	//若聊天視窗無開啟的情況，未讀訊息要增加。
            	if($("#onLineUser").val()!= msgVO.msg_from){
            		handleMapLogic(userMap, msgVO.msg_from, 1);
            		setMessageInnerHTML(msgVO.msg_from_user_name + "的未讀訊息有" + userMap.get(msgVO.msg_from));	
            	}else{            	
            		setMessageInnerHTML(msgVO.msg_from_user_name + "的未讀訊息有" + 0);            		
            		//已經開啟跟該用戶聊天的視窗，故要把訊息儲存成已讀。
                    var message = {            
                            "msg_from": msgVO.msg_from,
                            "msg_to": msgVO.msg_to,
                            "msg_type" : "updateMsgStatus"
                    };        
					//發送訊息至後端server
					webSocket.send(JSON.stringify(message));            		            		
            	}    
            	break; 
			case "receive_own_msg"://接收自己訊息。
            	var serverReceive = obj[msg_type];
            	if(serverReceive){
            		setMessageInnerHTML("O");
            		scroll_to_bottom();
            	}			 
            	break;                 	                	                	                	
			case "broadcast"://接收廣播訊息。
            	var msgVO = obj[msg_type];
            	if(msgVO.msg_from != userID){
					if(msgVO.msg_content){
						console.log("msg_content有進來");	
						setMessageInnerHTML(msgVO.msg_from_user_name + ":" + msgVO.msg_content);	
					}else{
						console.log("msg_img有進來");
						setImgInnerHTML(msgVO.msg_img);
					}            	            	
            	}else{
            		scroll_to_bottom();
            	}            	
//            	setMessageInnerHTML(msg_content);
            	break;    
			case "sendOffer"://接收offer。
            	var offerJson = obj[msg_type];
            	console.log("接收offer");
            	//之後要加若已在通話，要回傳給撥打人告知他已在通話(用answerSetting_isReady試試看)
            	console.log(answerSetting_isReady);
            	if(answerSetting_isReady){
            		var rtc_user = {"msg_type" : "reject","targetUser":offerJson.originUser,"msg_content": "通話中"};
            		webSocket.send(JSON.stringify(rtc_user));
            		return;
            	}          
            	  	
            	//判斷是否要接收通話
				let yes = confirm(offerJson.originUserName + '要與你通話');				
				if (yes){
				    createAnswer(offerJson);
				}else{
					var rtc_user = {"msg_type" : "reject","targetUser":offerJson.originUser,"msg_content":"拒絕通話"};
					webSocket.send(JSON.stringify(rtc_user));
				}            	
            	 	
            	break;  
			case "sendCandidate"://接收candidate。
				console.log("有peer");         	
				obj[msg_type].forEach(function(candidateJson){
					console.log(candidateJson.candidate);
					peer.addIceCandidate(candidateJson.candidate);
            	}); 				
            	break;
			case "sendAnswer"://A端接收answer。
            	let answerJson = obj[msg_type];
    			let answerSDP = new RTCSessionDescription(answerJson.answerSDP);
				console.log("setRemote Answer");
				console.log(answerJson);
    			peer.setRemoteDescription(answerSDP);
    			answerSetting_isReady = true;
    			//傳送已經Answer設定好了
    			var rtc_user = {"msg_type" : "sendAnswerIsReady","targetUser":answerJson.originUser ,"answerSetting_isReady":answerSetting_isReady};
    			console.log(answerSetting_isReady);
    			webSocket.send(JSON.stringify(rtc_user));	    			
				
    			//告知Server可以轉發兩端的candidate給對方
    			var rtc_user = {"msg_type" : "sendCandidate","originUser":userID,"targetUser":answerJson.originUser};
    			webSocket.send(JSON.stringify(rtc_user));
            	break;
			case "sendAnswerIsReady":
				console.log("sendAnswerIsReady is true");
				answerSetting_isReady = true;
				console.log(answerSetting_isReady);
				break;
			case "reject":								
				let msg_reject = obj[msg_type];
				if(msg_reject.msg_content == "通話中"){
					alert("對方正在通話中");
				}else{
					alert("對方拒絕與你通話");
				}
				closeVideoCall();
				break;
			default://取得關鍵字搜尋筆數(msg_type=showKeyword)
				let keywordMap= new Map();
				obj[msg_type].forEach(function(msgVO){
					if(msgVO.msg_from == userID){
            			handleMapLogic(keywordMap, msgVO.msg_to, msgVO.msg_count);
            		}else{
            			handleMapLogic(keywordMap, msgVO.msg_from, msgVO.msg_count);             		
            		}
            	}); 
				console.log(keywordMap);
        }
    };

    //連接關閉的回調事件
    webSocket.onclose = function () {
        console.log("連接已關閉...");
    };
}else {
    // 瀏覽器不支持 WebSocket
    alert("瀏覽器不支持 WebSocket!");
}

function closeWebSocket() {
    //直接關閉websocket的連接
    webSocket.close();
}

//點擊發送按鈕送出訊息
function send() {
    var selectText = $("#onLineUser").find("option:selected").text();
    var message = {            
            "msg_from": userID,
            "msg_to": selectText,
            "msg_content": document.getElementById('text').value,
            "msg_status" : 0,
        };                
    if(selectText == "ALL"){
    	message["msg_type"] = "broadcast";
    }else{
    	message["msg_type"] = "save";
    }    
    
    //放到自己的對話框裡面
	setMessageInnerHTML(message.msg_content);
	scroll_to_bottom();    
	    
  	//發送訊息至後端server
    webSocket.send(JSON.stringify(message));
    
    //發送訊息欄清空
    $("#text").val("");
}

//處理map
function handleMapLogic(map, key, count) {
	if(map.has(key)){
		map.set(key, map.get(key) + count);
	}else{
		map.set(key, count)
	}
}   

//點擊搜尋按鈕，搜尋關鍵字。
function search() {
    var searchText = $("#search").val();
    var message = {            
        "msg_from": userID,
        "msg_to": userID,
        "msg_content": searchText,
        "msg_type" : "searchKeyword"
    };        
  	//發送訊息至後端server
    webSocket.send(JSON.stringify(message));
    $("#search").val("");
} 


function setMessageInnerHTML(innerHTML) {
    document.getElementById('message').innerHTML += '<div>' + innerHTML + '<div/>';
}

function setImgInnerHTML(innerHTML) {
	let msg_div = document.createElement("div");
	let msg_img = document.createElement("img");
	msg_img.src = innerHTML;
	msg_img.width = 200;
	msg_img.height = 200;
	msg_div.append(msg_img);
    $("#message").append(msg_div);
}    

//scroll bar滑動至第一則未讀訊息上。
function setMessageInnerHTML_adjustBar(innerHTML) {
    document.getElementById('message').innerHTML += "<div id='adjust_bar'>" + innerHTML + '<div/>';
    var target_top = $("#adjust_bar").offset().top;
    document.getElementById('inner-container').scrollTop = target_top;                    
}   

//送出訊息時，scroll bar滑動至底。
function scroll_to_bottom(){
	let div = document.getElementById('inner-container');
	div.scrollTop = div.scrollHeight;                   
}   