package iglugis.chatter.MessageStructures;

import iglugis.chatter.MessageTypes;

public class GetOnlineUserList extends AbstractMessageStructure {
	public String[] userList;
	
	public GetOnlineUserList()
	{
		this.type=MessageTypes.GETONLINEUSERLIST;
	}
}
