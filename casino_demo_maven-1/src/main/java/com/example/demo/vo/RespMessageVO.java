package com.example.demo.vo;

public class RespMessageVO {
	private String type;
	private Object payload;
	public RespMessageVO(String type, Object payload) {
		this.type = type;
		this.payload = payload;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getPayload() {
		return payload;
	}
	public void setPayload(Object payload) {
		this.payload = payload;
	}
	
	
}
