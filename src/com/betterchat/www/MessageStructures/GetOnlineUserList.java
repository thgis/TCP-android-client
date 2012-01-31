package com.betterchat.www.MessageStructures;

import com.betterchat.www.MessageTypes;

public class GetOnlineUserList extends AbstractMessageStructure {
	public String[] userList;
	
	public GetOnlineUserList()
	{
		this.type=MessageTypes.GETONLINEUSERLIST;
	}
}
