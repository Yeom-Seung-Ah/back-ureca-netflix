package com.netflix.ureca.dto;


public class SaltInfo {
	
	private String userId, salt;

	public String getEmail() {
		return userId;
	}

	public void setEmail(String userId) {
		this.userId = userId;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public SaltInfo(String userId, String salt) {
		super();
		this.userId = userId;
		this.salt = salt;
	}

	public SaltInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "SaltInfo [userId=" + userId + ", salt=" + salt + "]";
	}
	
	

}

