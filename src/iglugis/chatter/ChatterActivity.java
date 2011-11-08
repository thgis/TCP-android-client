package iglugis.chatter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

public class ChatterActivity extends Activity {
	private String ipAddress = "192.168.16.200";
	private Client client;
	
	private EditText textToSend;
	private EditText textReceived;
	private EditText textUser;
	private EditText serverIP;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.main);
        
        textToSend = (EditText)findViewById(R.id.ETSend);
        textReceived = (EditText)findViewById(R.id.ETReceived);
        textUser = (EditText)findViewById(R.id.ETUserName);
        serverIP = (EditText)findViewById(R.id.ETServerIP);
        
        serverIP.setText(ipAddress);
        textUser.setText("LarsMobi");
    }
    
    @Override
    protected void onStop() {
    	if(client != null)
    		client.Stop();
    	super.onStop();
    }
    
    private Handler handlerClient = new Handler() {
   	 
    	public void handleMessage(Message msg) {
    		if (msg.what == 0) {
    			textReceived.append((String)msg.obj + "\n" );
    		}
    	}
    };
    
    public void Connect(View view) {
    	ipAddress = serverIP.getText().toString();
    	client = new Client(ipAddress, textUser.getText().toString(),handlerClient);
    	client.Start();
    }
    
    public void Send(View view) {
    	String msg = textToSend.getText().toString();
    	client.SendMessage(msg);
    	textToSend.setText("");
    }
}