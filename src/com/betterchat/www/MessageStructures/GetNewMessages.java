package com.betterchat.www.MessageStructures;

import com.betterchat.www.MessageTypes;

public class GetNewMessages extends AbstractMessageStructure {
	public String receiver;
	public long lastSeenTimeStamp;
	
	
	public GetNewMessages()
	{
		this.type = MessageTypes.GETNEWMESSAGES;
	}

}
