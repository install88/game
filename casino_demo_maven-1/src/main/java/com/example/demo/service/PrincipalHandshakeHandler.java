package com.example.demo.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.demo.vo.MyPrincipal;
import com.example.demo.vo.UserBean;
@Component
public class PrincipalHandshakeHandler extends DefaultHandshakeHandler {
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	
	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
		MemberService memService = new MemberService(); 
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginID = "";
		if (principal instanceof UserDetails) {
			loginID = ((UserDetails) principal).getUsername();
		} else {
			loginID = principal.toString();
		}
		if (request instanceof ServletServerHttpRequest) {
			if (StringUtils.isEmpty(loginID)) {
				return null;
			}else {
				return new MyPrincipal(memService.getMemberByLoginID(loginID));
			}
		}
		return null;

	}
}
