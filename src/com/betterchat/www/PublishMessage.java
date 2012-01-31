package com.betterchat.www;

public class PublishMessage {
	public int type;
	public String sender;
	public String receiver;
	public String message;
	public String errorMessage;
	public long timeStamp=0;
	
	public PublishMessage()
	{
		type=MessageTypes.PUBLISHMESSAGE;
	}
}
