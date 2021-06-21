package com.example.demo.vo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameVO {
	private Integer id;
	private Integer room_id;
	private String banker;
	private String roomNO;
	private String result;
	private String status;
	private Timestamp create_time;
	private Timestamp bet_start_time;
	private Timestamp bet_end_time;
	private float admin_get_money;
	private Timestamp end_time;
	private Integer limit_amount;
	
	private Map<String,List<BetVO>> records = new ConcurrentHashMap<String, List<BetVO>>();
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRoomNO() {
		return roomNO;
	}
	public void setRoomNO(String roomNO) {
		this.roomNO = roomNO;
	}
	
	public String getBanker() {
		return banker;
	}
	public void setBanker(String banker) {
		this.banker = banker;
	}

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Map<String, List<BetVO>> getRecords() {
		return records;
	}
	public void setRecords(Map<String, List<BetVO>> records) {
		this.records = records;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void addBet(BetVO vo) {
		if(records.containsKey(vo.getTarget())) {
			if(null==this.records.get(vo.getTarget())) {
				this.records.put(vo.getTarget(), new ArrayList<BetVO>());
			}
			this.records.get(vo.getTarget()).add(vo);
		}
	}
	
	public boolean hasBetRecords() {
		boolean hasBetRecord = false;
		for(String key:this.records.keySet()) {
			if(this.records.get(key).size()>0) {
				hasBetRecord = true;
				break;
			}			
		}
		return hasBetRecord;
	}
	@Override
	public String toString() {
		return "GameVO [id=" + id + ", roomNO=" + roomNO + ", result=" + result + ", records=" + records + "]";
	}

	public Integer getRoom_id() {
		return room_id;
	}
	public void setRoom_id(Integer room_id) {
		this.room_id = room_id;
	}
	public Timestamp getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}
	public Timestamp getBet_start_time() {
		return bet_start_time;
	}
	public void setBet_start_time(Timestamp bet_start_time) {
		this.bet_start_time = bet_start_time;
	}
	public Timestamp getBet_end_time() {
		return bet_end_time;
	}
	public void setBet_end_time(Timestamp bet_end_time) {
		this.bet_end_time = bet_end_time;
	}
	public float getAdmin_get_money() {
		return admin_get_money;
	}
	public void setAdmin_get_money(float admin_get_money) {
		this.admin_get_money = admin_get_money;
	}
	public Timestamp getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Timestamp end_time) {
		this.end_time = end_time;
	}
	public Integer getLimit_amount() {
		return limit_amount;
	}
	public void setLimit_amount(Integer limit_amount) {
		this.limit_amount = limit_amount;
	}
	
	
}
