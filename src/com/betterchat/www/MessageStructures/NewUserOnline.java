package com.betterchat.www.MessageStructures;

import com.betterchat.www.MessageTypes;

public class NewUserOnline extends AbstractMessageStructure {
	
	public String userName;
	
	public NewUserOnline()
	{
		this.type = MessageTypes.NEWUSERONLINE;
	}

}
