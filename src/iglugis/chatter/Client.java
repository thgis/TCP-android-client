package iglugis.chatter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;

public class Client implements Runnable {
	private Socket kkSocket;
	private byte[] buffer;
	private String Name;
	private Handler handle;
	
	OutputStream outStream;
	InputStream instream;
	
	private volatile boolean stop = false;
	
	public Client(String ipAddress, String name, Handler handle)
	{
		this.handle = handle;
		Name = name;
		buffer = new byte[1024];
		try {
			kkSocket = new Socket(ipAddress , 8000);
			outStream = kkSocket.getOutputStream();
			instream = kkSocket.getInputStream();
			SendMessage("User:" + Name);
		} catch (UnknownHostException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	 	    
	        	 Message lmsg = new Message();
	 	         lmsg.what = 0;
	 	         lmsg.obj = test;
	 	         handle.sendMessage(lmsg);
	        }
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
