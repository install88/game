package com.example.demo.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class WebSocketSessions {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ConcurrentHashMap<String, String> sessionUsers = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, String> roomUsers = new ConcurrentHashMap<String, String>();

	
	public synchronized void registerRoom(String user, String roomNO) {
		roomUsers.put(user, roomNO);
	}
	public synchronized void removeUserFromRoom(String user) {
		if (roomUsers.containsKey(user)) {
			roomUsers.remove(user);
		}
	}
	public synchronized void registerSessionId(String user, String sessionId) {
		sessionUsers.put(sessionId, user);
	}

	public synchronized void removeSessionId(String sessionId) {
		if (sessionUsers.containsKey(sessionId)) {
			sessionUsers.remove(sessionId);
		}
	}

	public ArrayList<String> getAllUsers() {
		return new ArrayList<>(sessionUsers.values());
	}

	public ArrayList<String> getAllSessionIds() {
		return new ArrayList<>(sessionUsers.keySet());
	}

	/**
	 * 取得相同使用者的所有sessionIds
	 */
	public ArrayList<String> getSessionIds(String user) {
		ArrayList<String> sessionIds = new ArrayList<String>();
		for (Map.Entry<String, String> entry : sessionUsers.entrySet()) {
			String userInMap = entry.getValue();
			if (userInMap.equals(user)) {
				sessionIds.add(entry.getKey());
			}
		}
		return sessionIds;
	}
	
	public ArrayList<String> getMems(String roomNO){
		ArrayList<String> loginIDs = new ArrayList<String>();
		for (Map.Entry<String, String> entry : roomUsers.entrySet()) {
			String chkRoomNO = entry.getValue();
			if (chkRoomNO.equals(roomNO)) {
				loginIDs.add(entry.getKey());
			}
		}
		return loginIDs;
	}
	@Override
	public String toString() {
		return "[WebSocketSessions] sessionUsers: " + sessionUsers.size();
	}
}
