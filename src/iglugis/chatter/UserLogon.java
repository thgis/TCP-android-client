package iglugis.chatter;
import iglugis.chatter.MessageTypes;

public class UserLogon {
	public int type;
	public String username;
	public boolean success;
	public String errormessage;
	
	public UserLogon()
	{
		type=MessageTypes.USERLOGON;
	}
	
}
