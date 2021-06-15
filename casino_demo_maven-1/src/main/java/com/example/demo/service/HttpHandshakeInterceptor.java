package com.example.demo.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.demo.IContent;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		logger.info("Call beforeHandshake"); 
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		if("".contentEquals(loginID)) {
			return false;
		}
		
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            final String roomNO = httpRequest.getParameter("roomNO");
            attributes.put(IContent.WS_SESS_HSESSID_KEY, session.getId());
            attributes.put(IContent.WS_SESS_LOGINID_KEY, loginID);
            attributes.put(IContent.WS_SESS_ROOM_NO_KEY, roomNO);
  
        }

        return true;
	}
	
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		logger.info("Call afterHandshake");
		
	}
}
