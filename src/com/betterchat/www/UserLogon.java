package com.betterchat.www;
import com.betterchat.www.MessageTypes;

public class UserLogon {
	public int type;
	public String username;
	public boolean success;
	public String errormessage;
	public int id;
	
	public UserLogon()
	{
		type=MessageTypes.USERLOGON;
	}
	
}
