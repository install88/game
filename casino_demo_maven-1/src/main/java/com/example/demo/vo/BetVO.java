package com.example.demo.vo;

import java.sql.Timestamp;

public class BetVO {
	private String memID;
	private String loginID;
	private String target;
	private float amount;
	private String roomNO;	
	private Integer bet_records_id;
	private Integer game_records_id;
	private Integer result;
	private Timestamp bet_time;
	private float get_money;
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
	public String getRoomNO() {
		return roomNO;
	}
	public void setRoomNO(String roomNO) {
		this.roomNO = roomNO;
	}
	
	public String getMemID() {
		return memID;
	}
	public void setMemID(String memID) {
		this.memID = memID;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	
	public Integer getBet_records_id() {
		return bet_records_id;
	}
	public void setBet_records_id(Integer bet_records_id) {
		this.bet_records_id = bet_records_id;
	}
	public Integer getGame_records_id() {
		return game_records_id;
	}
	public void setGame_records_id(Integer game_records_id) {
		this.game_records_id = game_records_id;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	
	public Timestamp getBet_time() {
		return bet_time;
	}
	public void setBet_time(Timestamp bet_time) {
		this.bet_time = bet_time;
	}
	
	public float getGet_money() {
		return get_money;
	}
	public void setGet_money(float get_money) {
		this.get_money = get_money;
	}
	@Override
	public String toString() {
		return "BetVO [memID=" + memID + ", loginID=" + loginID + ", target=" + target + ", amount=" + amount
				+ ", roomNO=" + roomNO + "]";
	}
	
	
}

