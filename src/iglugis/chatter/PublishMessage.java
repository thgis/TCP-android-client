package iglugis.chatter;

public class PublishMessage {
	public int id;
	public String Sender;
	public String Receiver;
	public String Message;
	public String ErrorMessage;
	
	public PublishMessage()
	{
		id=MessageTypes.PUBLISHMESSAGE;
	}
}
