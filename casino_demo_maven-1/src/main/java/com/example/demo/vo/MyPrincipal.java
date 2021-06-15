package com.example.demo.vo;

import java.security.Principal;

public class MyPrincipal implements Principal{
	private UserBean userBean;
	public MyPrincipal(UserBean userBean) {
		this.userBean = userBean;
	}
	@Override
	public String getName() {
		return String.valueOf(userBean.getLoginID());
	}
	public String getUserName() {
		return String.valueOf(userBean.getUserName());
	}
	public String getMemID() {
		return String.valueOf(userBean.getMemID());
	}
	
	public String getRolID() {
		return String.valueOf(userBean.getRolID());
	}
	public String getLoginID() {
		return String.valueOf(userBean.getLoginID());
	}
	public String getDepID() {
		return String.valueOf(userBean.getDepID());
	}
	public UserBean getUserBean() {
		return userBean;
	}
	
}
