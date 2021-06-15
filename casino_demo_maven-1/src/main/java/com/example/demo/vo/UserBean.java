package com.example.demo.vo;

public class UserBean {
	private String memID;
	private String userName;
	private String rolID;
	private String depID;
	private String loginID;
	private Float money;
	public UserBean() {
		
	}
//	public UserBean(String memID, String userName, String loginID, Float money) {
//		super();
//		this.memID = memID;
//		this.userName = userName;
//		this.loginID = loginID;
//		this.money = money;
//	}
	
	public UserBean(String loginID, String userName, Float money) {
		super();
//		this.memID = memID;
		this.userName = userName;
		this.loginID = loginID;
		this.money = money;
	}	
	
	public String getMemID() {
		return memID;
	}
	public void setMemID(String memID) {
		this.memID = memID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRolID() {
		return rolID;
	}
	public void setRolID(String rolID) {
		this.rolID = rolID;
	}
	public String getDepID() {
		return depID;
	}
	public void setDepID(String depID) {
		this.depID = depID;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	
	public Float getMoney() {
		return money;
	}
	public void setMoney(Float money) {
		this.money = money;
	}
	@Override
	public String toString() {
		return "UserBean [memID=" + memID + ", userName=" + userName + ", rolID=" + rolID + ", depID=" + depID
				+ ", loginID=" + loginID + "]";
	}
	
}
