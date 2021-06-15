package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import com.example.demo.IContent;
import com.example.demo.vo.MyPrincipal;


@Component
public class StompConnectEventListener implements ApplicationListener<SessionConnectEvent> {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	    
    @Autowired
    private WebSocketSessions webSocketSessions;
    

	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
		String roomNO = sha.getSessionAttributes()==null?"":sha.getSessionAttributes().get(IContent.WS_SESS_ROOM_NO_KEY)==null?"":String.valueOf(sha.getSessionAttributes().get(IContent.WS_SESS_ROOM_NO_KEY));
		MyPrincipal user = (MyPrincipal)event.getUser();
		webSocketSessions.registerSessionId(user.getLoginID(), sha.getSessionId());
		webSocketSessions.registerRoom(user.getLoginID(), roomNO);
	}
}
