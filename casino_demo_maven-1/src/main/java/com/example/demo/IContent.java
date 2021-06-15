package com.example.demo;

public interface IContent {
	public final static String ROOM_STATUS_INIT = "INIT";
	public final static String ROOM_STATUS_NORMAL = "NORMAL";
	public final static String ROOM_STATUS_CLOSE = "CLOSE";
	public final static String ROOM_STATUS_DELETE = "DELETE";
	
	public final static String GAME_STATUS_INIT = "INIT";
	public final static String GAME_STATUS_RUNNING = "RUNNING";
	public final static String GAME_STATUS_BETTING = "BETTING";
	public final static String GAME_STATUS_DRAWING = "DRAWING";
	public final static String GAME_STATUS_CLOSE = "CLOSE";
	
	public final static String WS_SESS_ROOM_NO_KEY = "roomNO";
	public final static String WS_SESS_LOGINID_KEY = "loginID";
	public final static String WS_SESS_HSESSID_KEY = "httpSessionID";
}

