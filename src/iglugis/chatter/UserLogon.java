package iglugis.chatter;
import iglugis.chatter.MessageTypes;

public class UserLogon {
	public int id;
	public String username;
	public boolean success;
	public String errormessage;
	
	public UserLogon()
	{
		id=MessageTypes.USERLOGON;
	}
	
}
