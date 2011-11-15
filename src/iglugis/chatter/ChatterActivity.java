package iglugis.chatter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChatterActivity extends Activity {
	private Client client;
	private final String SHARED = "sharedchatterpref";
	private String mUserName;
	private String mIpAddress;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.main);
        
        EditText textUser = (EditText)findViewById(R.id.ETUserName);
        EditText serverIP = (EditText)findViewById(R.id.ETServerIP);
        
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(SHARED, 0);
        mUserName = settings.getString("userName", "your name");
        mIpAddress = settings.getString("ipAddress", "0.0.0.0");

        serverIP.setText(mIpAddress);
        textUser.setText(mUserName);
        
        textUser.addTextChangedListener(new TextWatcher() {
            public void  onTextChanged  (CharSequence s, int start, int before, int count) { 
            }

			@Override
			public void afterTextChanged(Editable edit) {
				mUserName = edit.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			} 
        });
        
        serverIP.addTextChangedListener(new TextWatcher() {
            public void  onTextChanged  (CharSequence s, int start, int before, int count) { 
            }

			@Override
			public void afterTextChanged(Editable edit) {
				mIpAddress = edit.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			} 
        });
    }
    
    @Override
    protected void onStop() {
    	if(client != null)
    		client.Stop();
    	
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(SHARED, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userName", mUserName);
        editor.putString("ipAddress", mIpAddress);

        // Commit the edits!
        editor.commit();
        
        super.onStop();
    }
    
    private Handler handlerClient = new Handler() {
   	 
    	public void handleMessage(Message msg) {
    		if (msg.what == 0) {
    			EditText text = (EditText) findViewById(R.id.ETReceived);
    			text.append((String)msg.obj + "\n" );
    		}
    	}
    };
    
    public void Connect(View view) {
    	client = new Client(mIpAddress, mUserName, handlerClient);
    	client.Start();
    	Button sendBtn = (Button) findViewById(R.id.button1);
    	sendBtn.setEnabled(true);
    	
    }
    
    public void Send(View view) {
    	EditText edit = (EditText) findViewById(R.id.ETSend);
    	String msg = edit.getText().toString();
    	client.SendMessage(msg);
    	edit.setText("");
    }
}