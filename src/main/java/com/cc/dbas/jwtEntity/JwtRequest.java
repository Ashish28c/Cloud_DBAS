package com.cc.dbas.jwtEntity;

import java.io.Serializable;

public class JwtRequest implements Serializable {
private static final long serialVersionUID = 5926468583005150707L;
	
	private String emailID;
	private String password;
	
	public JwtRequest()
	{
		
	}

	public JwtRequest(String emailID, String password) {
		this.setEmailID(emailID);
		this.setPassword(password);
	}

	
	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
