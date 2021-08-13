//A端要球與B通話時，把RTC實體new出來，並產生offer送至B端。
function build_chatting(){
	// get a local stream, show it in a self-view and add it to be sent
	var constraints = { "audio": true, "video": false };
	navigator.mediaDevices.getUserMedia(constraints)
	.then(function(stream) {
		console.log("navigator~~~");
		console.log(stream);
		peer = new RTCPeerConnection(configuration);
		
		stream.getTracks().forEach(track => {
			console.log("track~~~~~~");
			peer.addTrack(track, stream);
		});
		
		peer.ontrack = event => {
			console.log("觸發ontrack offer端~~~~~~~~~");
			console.log(event.streams[0]);
			remoteVideo.srcObject = event.streams[0];
			remoteVideo.play();
		};
			
		localStream = stream;
		//發送端處理offer
		peer.createOffer().then(offer => {
			console.log("createOffer~~~~~~~~~~~");
			var rtc_user = {"msg_type" : "sendOffer","targetUser": othersideID,"originUser":userID, "offerSDP" : offer};			
		    peer.setLocalDescription(offer);
		    webSocket.send(JSON.stringify(rtc_user));
		});
				
		peer.onicecandidate = function(event){
			console.log("Offer onicecandidate~~~~");
			console.log(answerSetting_isReady);
			var candidate = event.candidate;
			if(candidate){
				var rtc_user = {"msg_type" : "storeCandidate","targetUser": othersideID,"originUser":userID, "candidate" : event.candidate, "answerSetting_isReady":answerSetting_isReady};
				webSocket.send(JSON.stringify(rtc_user));	
			}
		};	
		peer.oniceconnectionstatechange = handleICEConnectionStateChangeEvent;
		document.getElementById("hangup-button").disabled = false;
	})
	.catch(function(err) {
	 	/* 處理error */
	});				

}

//B端收到A端送來的offer時觸發。
function createAnswer(offerJson){
	// get a local stream, show it in a self-view and add it to be sent
	var constraints = { "audio": true, "video": false };
	navigator.mediaDevices.getUserMedia(constraints)
	.then(function(stream) {
		console.log("navigator_offerSDP~~~");
		if(!peer){
			peer = new RTCPeerConnection(configuration);	
		}		
		console.log(stream);
		stream.getTracks().forEach(track => {
			console.log("track~~~~~~");
			peer.addTrack(track, stream);
		});
		
		peer.ontrack = event => {
			console.log("觸發ontrack Answer端~~~~~~~~~");
			console.log(event.streams[0]);
			remoteVideo.srcObject = event.streams[0];
			remoteVideo.play();
		};			
						
		const offerSDP = new RTCSessionDescription(offerJson.offerSDP);
		peer.setRemoteDescription(offerSDP);
		//發送端處理offer
		peer.createAnswer().then(answerSDP => {
			console.log("createAnswer~~~~~~~~~~~");
			var targetUser = offerJson.originUser;
			var rtc_user = {"msg_type" : "sendAnswer","targetUser": targetUser,"originUser":userID,"answerSDP" : answerSDP};			
		    peer.setLocalDescription(answerSDP);			    
		    webSocket.send(JSON.stringify(rtc_user));
		});
				
		peer.onicecandidate = function(event){
			console.log("answer_onicecandidate~~~~");
			console.log(answerSetting_isReady);
			var candidate = event.candidate;
			if(candidate){
				var targetUser = offerJson.originUser;
				var rtc_user = {"msg_type" : "storeCandidate","targetUser": targetUser,"originUser":userID,"candidate" : event.candidate,"answerSetting_isReady":answerSetting_isReady};
				webSocket.send(JSON.stringify(rtc_user));	
			}
		};	
		peer.oniceconnectionstatechange = handleICEConnectionStateChangeEvent;
		document.getElementById("hangup-button").disabled = false;
	})
	.catch(function(err) {
	  /* 處理error */
	});				
}

//關閉RTC連線通道
function closeVideoCall() {
	remoteVideo.srcObject = null;
	answerSetting_isReady = false;
	if (peer) {
		peer.close();
		peer = null;
	}			
//	document.getElementById("hangup-button").disabled = true;
	let modal = document.getElementById("myModal");
	modal.style.display = "none";
}	

//監聽RTC連線狀態
function handleICEConnectionStateChangeEvent(event) {
	switch(peer.iceConnectionState) {
		case "closed":
	    case "failed":
	    case "disconnected":		    	
	    	console.log("監聽到關閉 : " + peer.iceConnectionState);
			closeVideoCall();
	      	break;
	}
}