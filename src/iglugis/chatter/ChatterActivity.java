package iglugis.chatter;

import iglugis.chatter.MessageStructures.GetOnlineUserList;

import java.security.PublicKey;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

public class ChatterActivity extends Activity {
	private Client client;
	private final String SHARED = "sharedchatterpref";
	private String mUserName;
	private String mIpAddress;
	private ListView mList;
	private LayoutInflater mLayoutInflater;
	private CustomAdapter mAdapter;
	private int mCurrentView = 0;
	private View mSetupView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutInflater = getLayoutInflater();
        mSetupView = mLayoutInflater.inflate(R.layout.setup_display, null);
        setContentView(mSetupView);
 		
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
    		switch (msg.what) {
			case MessageTypes.USERLOGON:
				addMessage("Logged on succesful");
				break;
			case MessageTypes.PUBLISHMESSAGE:
				addMessage((String) msg.obj);
				break;
			case MessageTypes.GETONLINEUSERLIST:
				//TODO update list of online users
				break;
			default:
				break;
    		}
    	}
    };
    
    public void addMessage(String message) {
    	ChatMessage chatMessage = new ChatMessage();
    	chatMessage.message = message;
		mAdapter.add(chatMessage);
		mAdapter.notifyDataSetChanged();
    }
    
    public void Connect(View view) {
    	client = new Client(mIpAddress, mUserName, handlerClient);
    	client.connect();
    	client.Start();
    	client.sendUserLogon();
    	setContentView(R.layout.chat_display);
    	mCurrentView = 1;
    	
    	//setup the list
    	mList = (ListView) this.findViewById(R.id.list_chat_view);
    	
 		ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
 		mAdapter = new CustomAdapter(this, R.layout.row, list);
 		mList.setAdapter(mAdapter);
 		mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    	
    	Button sendBtn = (Button) findViewById(R.id.button1);
    	sendBtn.setEnabled(true);
    	GetOnlineUserList userList = new GetOnlineUserList();
    	client.SendMessage(new Gson().toJson(userList));
    	
    	EditText edit = (EditText) findViewById(R.id.ETSend);
    	
    	TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener(){
    		public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
    		    if (event.getAction() != KeyEvent.ACTION_DOWN) {
    		    	return false;
    		    }
    		    if(actionId == EditorInfo.IME_NULL){
    		    	sendMessage();//match this behavior to your 'Send' (or Confirm) button
    		    }

    		    return true;
    		}
    	};
    	edit.setOnEditorActionListener(exampleListener);
    }
    
    public void sendMessage() {
    	EditText edit = (EditText) findViewById(R.id.ETSend);
    	String msg = edit.getText().toString();
    	addMessage("You: " + msg);
    	PublishMessage message = new PublishMessage();
    	message.message=msg;
    	message.receiver="all";
    	message.sender=mUserName;
    	Gson gson = new Gson();
    	String serializedMessage = gson.toJson(message);
    	client.SendMessage(serializedMessage);
    	edit.setText("");
    }
    
    public void Send(View view) {
    	sendMessage();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	if(mCurrentView != 0) {
        		mCurrentView = 0;
        		setContentView(mSetupView);
        		return true;
        	}
        }
        return super.onKeyDown(keyCode, event);
    }

	public class CustomAdapter extends ArrayAdapter<ChatMessage> {
		
		private ArrayList<ChatMessage> mListItems;
		
		public CustomAdapter (Context context, int textViewResourceId, ArrayList<ChatMessage> list) {
			super(context, textViewResourceId, list);
			mListItems = list;
		}
		
		public void setList(ArrayList<ChatMessage> list) {
			this.clear();
			for(ChatMessage item : list) {
				this.add(item);
			}
			mListItems = list;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
	        if (convertView == null) {
	        	convertView = mLayoutInflater.inflate(R.layout.row, parent, false);
	            
	            holder = new ViewHolder();
	            
	            holder.text = (TextView) convertView.findViewById(R.id.row_txt);
	            holder.image = (ImageView) convertView.findViewById(R.id.image);
	            
	            convertView.setTag(holder);
	        } else {
	        	holder = (ViewHolder) convertView.getTag();
	        }
	        if(!mListItems.isEmpty()) {
	        	ChatMessage chatMessage = mListItems.get(position);
	        	if (chatMessage != null) {
	        		holder.text.setText(chatMessage.message);
	        		
	        		holder.image.setImageResource(R.drawable.icon);	//Here we can use some custom graphic for each user
	        	}
	        }
            return convertView;
		}
	}
	
	private static class ViewHolder {
		TextView text;
		ImageView image;
	}
}