package com.example.demo.vo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BetBakMessageVO {
	private String roomNO;
	private String message;
	private String amount;
	private String gameStatus;
	private Integer limit_amount;
	private Map<String,String> poolMap = new ConcurrentHashMap<String,String>();
	public BetBakMessageVO() {
		
	}
	public BetBakMessageVO(String roomNO,String message) {
		super();
		this.roomNO = roomNO;
		this.message = message;
	}
	
	public String getRoomNO() {
		return roomNO;
	}
	public void setRoomNO(String roomNO) {
		this.roomNO = roomNO;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getPoolMap() {
		return poolMap;
	}

	public void setPoolMap(Map<String, String> poolMap) {
		this.poolMap = poolMap;
	}
	public void putPool(String poolName, String result) {
		this.poolMap.put(poolName,result);
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getGameStatus() {
		return gameStatus;
	}
	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}
	public Integer getLimit_amount() {
		return limit_amount;
	}
	public void setLimit_amount(Integer limit_amount) {
		this.limit_amount = limit_amount;
	}
	
}
