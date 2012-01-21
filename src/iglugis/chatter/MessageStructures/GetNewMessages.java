package iglugis.chatter.MessageStructures;

import iglugis.chatter.MessageTypes;

public class GetNewMessages extends AbstractMessageStructure {
	public String receiver;
	public long lastSeenTimeStamp;
	
	
	public GetNewMessages()
	{
		this.type = MessageTypes.GETNEWMESSAGES;
	}

}
