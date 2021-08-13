if ("WebSocket" in window) {
	//ws://localhost:8080/websocket/{userID}
//    webSocket = new WebSocket("ws://192.168.50.20:8080/websocket/" + userID);
    webSocket = new WebSocket("ws://localhost:8080/websocket/" + userID);        
    //連通之後觸發
    webSocket.onopen = function () {
		console.log("已經連通了websocket");
    };

	//接收後端sever發送的消息
    webSocket.onmessage = function(evt) {
		console.log(evt);   		    			
        let received_msg = evt.data;
        console.log("數據已接收:" + received_msg);
        let obj = JSON.parse(received_msg);                	    
        let msg_type = Object.keys(obj)[0];
        console.log("msg_type=" + msg_type);
        switch(msg_type){
        	case "onOpenInfo":
        		console.log("onOpenInfo");
				obj[msg_type].forEach(function(data){
					let data_type = Object.keys(data)[0];
					switch(data_type){
						case "getOwnInfo":
							console.log("getOwnInfo");
							var msgVO = data[data_type];
							userName =  msgVO.msg_from_user_name;
							userHeadshot =  msgVO.msg_headshot;								
							break;        
			            case "showLastMsg"://顯示朋友的最後一筆訊息
			            	console.log("showLastMsg");
			                data[data_type].forEach(function(msgVO){			                
								showLastMessage(msgVO);	
			                });   
			            	break;			            	
						case "showMsgCount": //顯示朋友的未讀訊息筆數
							console.log("showMsgCount");
			                data[data_type].forEach(function(msgVO){
			    				userMap.set(msgVO.msg_from,msgVO.msg_count);	    				
			                });  
			            	break;   			            								
					}	
                });         		
        		break;             	
			case "showRecord"://顯示與該朋友的對話
				let chattingUserImg = document.getElementById('chattingUserImg');	
				let haveRead = false; 
                obj[msg_type].forEach(function(msgVO){
                	console.log(msgVO);
                	msgVO.msg_headshot = chattingUserImg.src;
                	if(msgVO.msg_to == userID){
                		console.log("~~~~~~~~~~~進來了");
						if(msgVO.msg_content){
							setOtherMessageInnerHTML(msgVO, 2, false);							
						}else{
							setOtherImgInnerHTML(msgVO, 2, false);							
						}		                			                		
                	}else{
						if(msgVO.msg_content){
							setOwnMessageInnerHTML(msgVO, 2, false);
						}else{
							setOwnImgInnerHTML(msgVO, 2, false);
						}	                			                			
                	}
                });
                
                //顯示完資料後，將關鍵字查詢設為null
                if(searchKeyword){
                	let target_top = $(".adjust_bar:last").offset().top;
                	let div = document.getElementsByClassName("ex1-2")[2];
					div.scrollTop = target_top -150;   
                	searchKeyword = null;
                }else{
	                //將scrollbar拉至最底下。
					scroll_to_bottom(2);                
                }                
            	break;                	                	
			case "receive_others_msg"://在線時，接收訊息。
				console.log("receive_others_msg有進來");
            	var msgVO = obj[msg_type];            					            	            
				if(msgVO.msg_from == othersideID && chatRoomIsOpen){
					msgVO.msg_headshot = document.getElementById('chattingUserImg').src;					
					if(msgVO.msg_content){
						setOtherMessageInnerHTML(msgVO, 2, true);
					}else{
						setOtherImgInnerHTML(msgVO, 2, true);
					}	
            		//已經開啟跟該用戶聊天的視窗，故要把訊息儲存成已讀。
                    var message = {            
                            "msg_from": msgVO.msg_from,
                            "msg_to": userID,
                            "msg_type" : "updateMsgStatus"
                    };        
					//發送訊息至後端server
					webSocket.send(JSON.stringify(message));     								
				}else{
			        if(friendMap.has(msgVO.msg_from)){
			        	let unread_a = document.querySelectorAll("a[name="+ msgVO.msg_from + "]")[0];
			        	userMap.set(msgVO.msg_from, userMap.get(msgVO.msg_from) + 1);
						unread_a.innerText = userMap.get(msgVO.msg_from);
			        	if(unread_a.style.visibility == 'hidden'){
							unread_a.style.visibility = "visible";
			        	}			        				        	     		
			        }else{//陌生人找你講話時，將陌生人顯示在清單中
					    $.ajax({
					        url:'http://localhost:8080/member/getMemberPhoto/',
					        method:'post',
					        data: {"msg_from" : msgVO.msg_from},
					        dataType:'JSON',
					        success:function(result){
					        	msgVO.msg_headshot = result.msg_headshot;
					        	showLastMessage(msgVO);
					        	let unread_a = document.querySelectorAll('a[name='+ msgVO.msg_from + ']')[0];
					        	userMap.set(msgVO.msg_from, userMap.get(msgVO.msg_from) + 1);
					        	unread_a.innerText = userMap.get(msgVO.msg_from);
					        	if(unread_a.style.visibility == 'hidden'){
									unread_a.style.visibility = "visible";
					        	}	
					        	
						        let message = {
									"msg_from": msgVO.msg_from,
									"msg_to": userID,
									"msg_type" : "addFriend"
						        };						        
						        webSocket.send(JSON.stringify(message));   					        					        	
					        },
					        error:function (data) {
					    		console.log("ajax失敗");    	
					        }
					    });		        			        	
			        } 					
				}
            	break; 
			case "receive_own_msg"://接收自己訊息。
            	var serverReceive = obj[msg_type];
            	if(serverReceive){
            		console.log("傳送成功");
//            		setMessageInnerHTML("O");
//            		scroll_to_bottom();
            	}			 
            	break;                 	                	                	                	
			case "broadcast"://接收廣播訊息。
            	var msgVO = obj[msg_type];
            	if(msgVO.msg_from != userID){
					if(msgVO.msg_content){
						console.log("msg_content有進來");	
						setOtherMessageInnerHTML(msgVO, 0, true);	
					}else{
						console.log("msg_img有進來");
						setOtherImgInnerHTML(msgVO, 0, true);
					}            	            	
            	}else{
            		scroll_to_bottom(0);
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
				for (let [key, value] of keywordMap) {
					setMessageInnerHTML(key+ ":" + value);					
				}            	 
//				console.log(keywordMap);
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

//點擊發送按鈕送出訊息(one to All)
function sendAll() {
	if(document.getElementById('toAll').value == ""){
		return;
	}

    var message = {            
		"msg_from": userID,
		"msg_to": "All",
		"msg_content": document.getElementById('toAll').value,
		"msg_status" : 0,
		"msg_type" : "broadcast"
    };                
    
    //放到自己的對話框裡面
	setOwnMessageInnerHTML(message, 0, true);
	scroll_to_bottom(0);    
	    
  	//發送訊息至後端server
    webSocket.send(JSON.stringify(message));
    
    //發送訊息欄清空
    $("#toAll").val("");
}

//點擊發送按鈕送出訊息(one to one)
function sendOne() {
	if(document.getElementById('toOne').value == ""){
		return;
	}
	
    var message = {            
		"msg_from": userID,
		"msg_to": othersideID,
		"msg_content": document.getElementById('toOne').value,
		"msg_status" : 0,
		"msg_type" : "save"
    };                
    
    //放到自己的對話框裡面
	setOwnMessageInnerHTML(message, 2, true);
	scroll_to_bottom(2);    
	    
  	//發送訊息至後端server
    webSocket.send(JSON.stringify(message));
    
    //發送訊息欄清空
    $("#toOne").val("");
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
function searchKeywordCount() {
    var searchText = $("#search").val();
    var message = {            
        "msg_from": userID,
        "msg_to": userID,
        "msg_content": searchText,
        "msg_type" : "searchKeywordCount"
    };        
  	//發送訊息至後端server
    webSocket.send(JSON.stringify(message));
    $("#search").val("");
} 


function setOwnMessageInnerHTML(msgVO, index, is_realtime) {
//    document.getElementById('message').innerHTML += '<div>' + innerHTML + '<div/>';
    let parent = document.getElementsByClassName("card-body")[index];
    
    //整條row
    let div = document.createElement("div");
    div.classList.add("d-flex");
    div.classList.add("justify-content-end");
    div.classList.add("mb-4");
    div.classList.add("msg_t01");
    
    //若是查詢關鍵字，則讓有關鍵字的訊息新增adjust_bar
    if(searchKeyword){
    	if(msgVO.msg_content.indexOf(searchKeyword) != -1){
    		console.log("有匹配到==========" + searchKeyword);
    		div.classList.add("adjust_bar");
//    		div.setAttribute("name", "adjust_bar");
    	}    	
    }
    
    //對話框以及內容
    let message_box = document.createElement("div");
    message_box.classList.add("msg_cotainer_send");
    message_box.innerHTML = msgVO.msg_content;

	//名子
	let message_name = document.createElement("span");
	message_name.classList.add("msg_naem_02");
	message_name.innerHTML = userName;

    //時間
    let message_time = document.createElement("span");
    message_time.classList.add("msg_time_send");
	let date;
	if(is_realtime){
		date = new Date();    	
	}else{
		date = new Date(msgVO.msg_time);			
	}
	let h = date.getHours();
	let m = (date.getMinutes()<10 ? '0' : '') + date.getMinutes();　
	message_time.innerHTML = h + ":" + m		  
   
    //個人大頭照圖片
    let img = document.createElement("img");
    img.classList.add("rounded-circle");
    img.classList.add("user_img_msg");
    img.src = userHeadshot;
    
    //包圖片的div
    let div_img = document.createElement("div");
    div_img.classList.add("img_cont_msg");    
    div_img.appendChild(img);
       
    //放入html中
    message_box.appendChild(message_name);
    message_box.appendChild(message_time);
    div.appendChild(message_box);
    div.appendChild(div_img);
    parent.appendChild(div);                
}

function setOtherMessageInnerHTML(msgVO, index, is_realtime) {
    let parent = document.getElementsByClassName("card-body")[index];
    
    //整條row
    let div = document.createElement("div");
    div.classList.add("d-flex");
    div.classList.add("justify-content-start");
    div.classList.add("mb-4");
    div.classList.add("msg_t01");
    
    
    //若是查詢關鍵字，則讓有關鍵字的訊息新增adjust_bar
    if(searchKeyword){
    	if(msgVO.msg_content.indexOf(searchKeyword) != -1){
    		console.log("有匹配到==========" + searchKeyword);
    		div.classList.add("adjust_bar");
//    		div.setAttribute("name", "adjust_bar"); 		
    	}    	
    }
           
    //個人大頭照圖片
    let img = document.createElement("img");
    img.classList.add("rounded-circle");
    img.classList.add("user_img_msg");
    img.src = msgVO.msg_headshot;
    
    //包圖片的div
    let div_img = document.createElement("div");
    div_img.classList.add("img_cont_msg");    
    div_img.appendChild(img);
    
    //新增onclick事件(跳轉到1-1聊天室)
    if(index==0){
		div_img.addEventListener('click', function(){
			console.log("觸發點事件~~~~~~~~~~~~~");
			openPageOneToOne("c_talk_1-1", msgVO.msg_from, msgVO.msg_headshot, msgVO.msg_from_user_name)
			othersideID = msgVO.msg_from;
	        let message = {
				"msg_from": msgVO.msg_from,
				"msg_to": userID,
	        };
	        
			if(friendMap.has(msgVO.msg_from)){
				message.msg_type = "getMessage";
		        //將未讀訊息數量改成0
//		        if(userMap.has(msgVO.msg_from)){
//		        	userMap.set(msg_from,0);	
//		        } 						
			}else{
				message.msg_type = "addFriend";
				showLastMessage(msgVO);
				
			}
	        webSocket.send(JSON.stringify(message));            	  
		});      
    }
        
    
    //對話框以及內容
    let message_box = document.createElement("div");
    message_box.classList.add("msg_cotainer");
    message_box.innerHTML = msgVO.msg_content;

	//名子
	let message_name = document.createElement("span");
	message_name.classList.add("msg_naem_01");
	message_name.innerHTML = msgVO.msg_from_user_name;

    //時間
    let message_time = document.createElement("span");
    message_time.classList.add("msg_time_send");
	let date;
	if(is_realtime){
		date = new Date();    	
	}else{
		date = new Date(msgVO.msg_time);			
	}
	let h = date.getHours();
	let m = (date.getMinutes()<10 ? '0' : '') + date.getMinutes();　
	message_time.innerHTML = h + ":" + m	  
          
    //放入html中
    message_box.appendChild(message_name);
    message_box.appendChild(message_time);
    div.appendChild(div_img);
    div.appendChild(message_box);    
    parent.appendChild(div);                
}

function setOwnImgInnerHTML(msgVO, index, is_realtime) {
    let parent = document.getElementsByClassName("card-body")[index];
    
    //整條row
    let div = document.createElement("div");
    div.classList.add("d-flex");
    div.classList.add("justify-content-end");
    div.classList.add("mb-4");
    div.classList.add("msg_t01");
    
    //對話框以及內容
    let message_box = document.createElement("div");
    message_box.classList.add("msg_cotainer_send");
    let message_img = document.createElement("img");
    message_img.src = msgVO.msg_img;
	message_img.width = 200;
	message_img.height = 200;    

	//名子
	let message_name = document.createElement("span");
	message_name.classList.add("msg_naem_02");
	message_name.innerHTML = userName;

    //時間
    let message_time = document.createElement("span");
    message_time.classList.add("msg_time_send");
	let date;
	if(is_realtime){
		date = new Date();    	
	}else{
		date = new Date(msgVO.msg_time);			
	}
	let h = date.getHours();
	let m = (date.getMinutes()<10 ? '0' : '') + date.getMinutes();　
	message_time.innerHTML = h + ":" + m		    
   
    //個人大頭照圖片
    let img = document.createElement("img");
    img.classList.add("rounded-circle");
    img.classList.add("user_img_msg");
    img.src = userHeadshot;
    
    //包圖片的div
    let div_img = document.createElement("div");
    div_img.classList.add("img_cont_msg");    
    div_img.appendChild(img);
       
    //放入html中
    message_box.appendChild(message_img);
    message_box.appendChild(message_name);
    message_box.appendChild(message_time);
    div.appendChild(message_box);
    div.appendChild(div_img);
    parent.appendChild(div);                
}


function setOtherImgInnerHTML(msgVO, index, is_realtime) {
    let parent = document.getElementsByClassName("card-body")[index];
    
    //整條row
    let div = document.createElement("div");
    div.classList.add("d-flex");
    div.classList.add("justify-content-start");
    div.classList.add("mb-4");
    div.classList.add("msg_t01");
    
    //個人大頭照圖片
    let img = document.createElement("img");
    img.classList.add("rounded-circle");
    img.classList.add("user_img_msg");
    img.src = msgVO.msg_headshot;
    
    //包圖片的div
    let div_img = document.createElement("div");
    div_img.classList.add("img_cont_msg");    
    div_img.appendChild(img);
    
    //新增onclick事件(跳轉到1-1聊天室)
    if(index==0){
		div_img.addEventListener('click', function(){
			console.log("觸發點事件~~~~~~~~~~~~~");
			openPageOneToOne("c_talk_1-1", msgVO.msg_from, msgVO.msg_headshot, msgVO.msg_from_user_name)
			othersideID = msgVO.msg_from;
	        let message = {
				"msg_from": msgVO.msg_from,
				"msg_to": userID,
	        };
	        
			if(friendMap.has(msgVO.msg_from)){
				message.msg_type = "getMessage";
		        //將未讀訊息數量改成0
//		        if(userMap.has(msgVO.msg_from)){
//		        	userMap.set(msg_from,0);	
//		        } 						
			}else{
				message.msg_type = "addFriend";
			}
	        webSocket.send(JSON.stringify(message));            	  
		});      
    }        
    
    //對話框以及內容
    let message_box = document.createElement("div");
    message_box.classList.add("msg_cotainer");
    let message_img = document.createElement("img");
    message_img.src = msgVO.msg_img;
	message_img.width = 200;
	message_img.height = 200;           

	//名子
	let message_name = document.createElement("span");
	message_name.classList.add("msg_naem_01");
	message_name.innerHTML = msgVO.msg_from_user_name;

    //時間
    let message_time = document.createElement("span");
    message_time.classList.add("msg_time");
	let date;
	if(is_realtime){
		date = new Date();    	
	}else{
		date = new Date(msgVO.msg_time);			
	}
	let h = date.getHours();
	let m = (date.getMinutes()<10 ? '0' : '') + date.getMinutes();　
	message_time.innerHTML = h + ":" + m		    
          
    //放入html中
    message_box.appendChild(message_img);
    message_box.appendChild(message_name);
    message_box.appendChild(message_time);
    div.appendChild(div_img);
    div.appendChild(message_box);    
    parent.appendChild(div);          
}


function showLastMessage(msgVO) {
	//註冊到好友清單中
	friendMap.set(msgVO.msg_from, msgVO.msg_from_user_name);

    let parent = document.getElementsByClassName("card-body");
    
    //整條row
    let div = document.createElement("div");
    div.classList.add("msg_t01");
    div.classList.add("row");
    div.classList.add("border_dff");
    
    //包圖片div
    let img_div = document.createElement("div");
    img_div.classList.add("col-2");
    img_div.classList.add("img_cont_msg");
    
    //朋友大頭照圖片
    let img = document.createElement("img");
    img.classList.add("rounded-circle");
    img.classList.add("user_img_msg");
    img.setAttribute("name", msgVO.msg_from);
    img.src = msgVO.msg_headshot;
    img_div.appendChild(img);
        
    //姓名div
    let name_div = document.createElement("div");
    name_div.classList.add("col-8");
    name_div.classList.add("msg_t03");    
    let name_span = document.createElement("span");
    name_span.classList.add("msg_naem_01_1");
    name_span.innerHTML = msgVO.msg_from_user_name
//    let lastMsg_a = document.createElement("a");
//    lastMsg_a.classList.add("fa_d01");
//    if(msgVO.msg_content){
//    	lastMsg_a.innerText = msgVO.msg_content;
//    }else{
//    	lastMsg_a.innerText = msgVO.msg_from_user_name + "  傳送了一個圖片";
//    }    
//    lastMsg_a.href = "#";
    name_div.appendChild(name_span);
//    name_div.appendChild(lastMsg_a);
        
    //放入html中
    div.appendChild(img_div);
    div.appendChild(name_div);    
            
    //未讀數量div
    let unread_div = document.createElement("div");
    unread_div.classList.add("col-2");
    let unread_a = document.createElement("a");
    unread_a.classList.add("fa_db");
    unread_a.classList.add("msg_link_01");
    unread_a.classList.add("badge_01");
    unread_a.classList.add("badge-pill");
    unread_a.classList.add("badge-warning");
    unread_a.setAttribute("name", msgVO.msg_from);
    unread_a.style.marginTop = 10 + 'px';
    if(typeof userMap.get(msgVO.msg_from) != "undefined"){
    	console.log("UserMap");
    	unread_a.innerText = userMap.get(msgVO.msg_from);
    }else{
    	console.log("設定為零");
    	userMap.set(msgVO.msg_from,0);    	
    	unread_a.innerText = 0;
    	unread_a.style.visibility = "hidden";
    	console.log(unread_a.innerText);    	
    }    
    unread_div.appendChild(unread_a);
    div.appendChild(unread_div);      
    
    //新增onclick事件(跳轉到1-1聊天室)
	div.addEventListener('click', function(){
		openPageOneToOne("c_talk_1-1", msgVO.msg_from, msgVO.msg_headshot, msgVO.msg_from_user_name)
		othersideID = msgVO.msg_from;
		chatRoomIsOpen = true;
        let message = {
			"msg_from": msgVO.msg_from,
			"msg_to": userID,
			"msg_type" : "getMessage"
        };
        webSocket.send(JSON.stringify(message));            
        //將未讀訊息數量改成0
        if(userMap.has(msgVO.msg_from)){
        	userMap.set(msgVO.msg_from,0);
        	let unread_a = document.querySelectorAll("a[name="+ msgVO.msg_from + "]")[0];
			unread_a.innerText = 0;
        	unread_a.style.visibility = "hidden";     		
        } 			  
	});    
    //放入html中
    parent[1].appendChild(div);
}

//scroll bar滑動至第一則未讀訊息上。
function setMessageInnerHTML_adjustBar(innerHTML) {
    document.getElementById('message').innerHTML += "<div id='adjust_bar'>" + innerHTML + '<div/>';
    var target_top = $("#adjust_bar").offset().top;
    document.getElementById('inner-container').scrollTop = target_top;                    
}

//送出訊息時，scroll bar滑動至底。
function scroll_to_bottom(index){
	let div = document.getElementsByClassName("ex1-2")[index];
	div.scrollTop = div.scrollHeight;                   
}   