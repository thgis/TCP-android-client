package iglugis.chatter;

public class PublishMessage {
	public int type;
	public String sender;
	public String receiver;
	public String message;
	public String errorMessage;
	
	public PublishMessage()
	{
		type=MessageTypes.PUBLISHMESSAGE;
	}
}
