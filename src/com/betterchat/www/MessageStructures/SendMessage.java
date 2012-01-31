package com.betterchat.www.MessageStructures;

import com.betterchat.www.MessageTypes;

public class SendMessage extends AbstractMessageStructure {
	public String sender;
	public String receiver;
	public String message;
	public long timeStamp;
	public boolean success;
	
	public SendMessage()
	{
		this.type= MessageTypes.SENDMESSAGE;
	}
	

}
