package com.netflix.ureca.dto;


import java.util.Date;

public class Login {
	
	private String userId, token, name;
	private Date loginTime;
	
	public Login(String userId, String token, String name, Date loginTime) {
		super();
		this.userId = userId;
		this.token = token;
		this.name = name;
		this.loginTime = loginTime;
	}

	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	@Override
	public String toString() {
		return "Login [userId=" + userId + ", token=" + token + ", name=" + name + ", loginTime=" + loginTime
				+ "]";
	}
	
	
	
	

}

