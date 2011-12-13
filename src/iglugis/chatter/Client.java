package iglugis.chatter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import com.google.gson;

import android.os.Handler;
import android.os.Message;

public class Client implements Runnable {
	private Socket kkSocket;
	private byte[] buffer;
	private String Name;
	private Handler handle;
	private String ipadresse;
	enum RECEIVESTATE {WAITING, RECEIVING,ENDING};
	
	OutputStream outStream;
	InputStream instream;
	
	private String inputBuffer;
	private RECEIVESTATE receiveState;
	
	private volatile boolean stop = false;
	
	public Client(String ipAddress, String name, Handler handle)
	{
		this.handle = handle;
		Name = name;
		buffer = new byte[1024];
		this.ipadresse = ipAddress;
		this.receiveState = RECEIVESTATE.WAITING;
	}
	
	public void connect()
	{
		try {
			kkSocket = new Socket(ipAddress , 8000);
			outStream = kkSocket.getOutputStream();
			instream = kkSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public void Read()
    {
		try {
			
	        int bytes = instream.available();
	        if( bytes > 0 )
	        {
	        	 instream.read(buffer, 0, bytes);
	        	 String test = new String(buffer,0,bytes);
	        }
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void handleData(String data)
    {
    	for (char c : data.toCharArray()) 
    	{
			switch (this.receiveState) {
			case WAITING:
				if (c == 0x02)
				{
					this.receiveState=RECEIVESTATE.RECEIVING;
					this.inputBuffer="";
				}
				break;
			case RECEIVING:
				this.inputBuffer = this.inputBuffer + c;
				if (c==0x10)
					this.receiveState=RECEIVESTATE.ENDING;
				break;

			case ENDING:
				if (c==0x03)
				{	
					String completeMessage= this.inputBuffer;
					handleMessage(completeMessage);
				}
				this.receiveState=RECEIVESTATE.WAITING;
				break;

			default:
				break;
			}
		}
    }
    
    private void handleMessage(String message)
    {
    }
    
    public void SendMessage(String msg)
    {
    	byte[] startByte= {0x02};
    	byte[] endByte= {0x10,0x03};
        byte[] msgByte = msg.getBytes();
        
        try {
        	outStream.write(startByte, 0, startByte.length);
			outStream.write(msgByte, 0, msgByte.length);
			outStream.write(endByte, 0, endByte.length);
			outStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public synchronized void Start()
    {
    	Thread listenThread = new Thread(this);
    	listenThread.start();
    }

	@Override
	public void run() {
		while(!stop)
		{
			Read();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Shutdown();
	}
	
	private void Shutdown()
	{
    	try {
			kkSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	kkSocket = null;
	}
    
    public synchronized void Stop()
    {
    	stop = true;
    }
}
