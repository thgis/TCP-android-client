package iglugis.chatter.MessageStructures;

import iglugis.chatter.MessageTypes;

public class NewUserOnline extends AbstractMessageStructure {
	
	public String userName;
	
	public NewUserOnline()
	{
		this.type = MessageTypes.NEWUSERONLINE;
	}

}
