package iglugis.chatter;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChatterActivity extends Activity {
	private Client client;
	private final String SHARED = "sharedchatterpref";
	private String mUserName;
	private String mIpAddress;
	private ListView mList;
	private LayoutInflater mLayoutInflater;
	private CustomAdapter mAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.main);
        mLayoutInflater = getLayoutInflater();
        // Set up list
 		mList = (ListView) findViewById(R.id.chat_view);
// 		mList.setOnItemClickListener(this);
 		
 		
 		ArrayList<ChatMessage> testList = new ArrayList<ChatMessage>();
 		ChatMessage msg = new ChatMessage();
 		msg.message = "TESTTESTTEST";
 		testList.add(msg);
 		testList.add(msg);
 		testList.add(msg);
 		testList.add(msg);
 		testList.add(msg);
 		
 		mAdapter = new CustomAdapter(this, R.layout.row, testList);
 		mList.setAdapter(mAdapter);
 		
		mAdapter.setList(testList);
        
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
    			addMessage((String) msg.obj);
    		}
    		if (msg.what==MessageTypes.PUBLISHMESSAGE)
    		{
    			addMessage(((PublishMessage)msg.obj).Message);
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
    	Button sendBtn = (Button) findViewById(R.id.button1);
    	sendBtn.setEnabled(true);
    }
    
    public void Send(View view) {
    	EditText edit = (EditText) findViewById(R.id.ETSend);
    	String msg = edit.getText().toString();
    	addMessage("You: " + msg);
    	client.SendMessage(msg);
    	edit.setText("");
    }
    
	public class CustomAdapter extends ArrayAdapter<ChatMessage> {
		
		private ArrayList<ChatMessage> mListItems;
		
		public CustomAdapter (Context context, int textViewResourceId, ArrayList<ChatMessage> list) {
			super(context, textViewResourceId, list);
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
	        
	        ChatMessage chatMessage = mListItems.get(position);
            if (chatMessage != null) {
	    		holder.text.setText(chatMessage.message);
	    		
	    		holder.image.setImageResource(R.drawable.icon);	//Here we can use some custom graphic for each user
            }
            return convertView;
		}
	}
	
	private static class ViewHolder {
		TextView text;
		ImageView image;
	}
}