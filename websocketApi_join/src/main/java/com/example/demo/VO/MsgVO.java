package com.example.demo.VO;
import java.sql.Timestamp;


public class MsgVO {
	private Integer msg_ID;
	private String msg_from;
	private String msg_from_user_name;
	private String msg_to;
	private String msg_to_user_name;
	private String msg_content;
	private Integer msg_status;
	private Timestamp msg_time;
	private String msg_img;
	
	public Integer getMsg_ID() {
		return msg_ID;
	}
	public void setMsg_ID(Integer msg_ID) {
		this.msg_ID = msg_ID;
	}
	public String getMsg_from() {
		return msg_from;
	}
	public void setMsg_from(String msg_from) {
		this.msg_from = msg_from;
	}
	public String getMsg_to() {
		return msg_to;
	}
	public void setMsg_to(String msg_to) {
		this.msg_to = msg_to;
	}
	public String getMsg_content() {
		return msg_content;
	}
	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}
	public Integer getMsg_status() {
		return msg_status;
	}
	public void setMsg_status(Integer msg_status) {
		this.msg_status = msg_status;
	}
	public Timestamp getMsg_time() {
		return msg_time;
	}
	public void setMsg_time(Timestamp msg_time) {
		this.msg_time = msg_time;
	}
	public String getMsg_from_user_name() {
		return msg_from_user_name;
	}
	public void setMsg_from_user_name(String msg_from_user_name) {
		this.msg_from_user_name = msg_from_user_name;
	}
	public String getMsg_to_user_name() {
		return msg_to_user_name;
	}
	public void setMsg_to_user_name(String msg_to_user_name) {
		this.msg_to_user_name = msg_to_user_name;
	}
	public String getMsg_img() {
		return msg_img;
	}
	public void setMsg_img(String msg_img) {
		this.msg_img = msg_img;
	}

}
