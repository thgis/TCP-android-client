package com.betterchat.www;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.betterchat.www.MessageStructures.GetOnlineUserList;
import com.betterchat.www.MessageStructures.SendMessage;
import com.betterchat.www.ui.actionbar.ActionBarActivity;
import com.google.gson.Gson;

public class ChatterActivity extends ActionBarActivity {
	private Client client;
	private final String SHARED = "sharedchatterpref";
	private String mUserName;
	private String mIpAddress;
	private ListView mList;
	private LayoutInflater mLayoutInflater;
	private CustomAdapter mAdapter;
	private int mCurrentView;
	public long timestamp = 0;
	private ArrayList<ChatMessage> mMessageList;
	private final static String TIMESTAMP = "timestamp";
    private Handler handlerClient;
	private boolean mIsRotateEvent = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageList = new ArrayList<ChatMessage>();
        mLayoutInflater = getLayoutInflater();
        mCurrentView = 0;
        createHandler();
        
        if(savedInstanceState != null) {
        	final Object data = getLastNonConfigurationInstance();
            if(data != null) {
            	ReetainContainer container = (ReetainContainer) data;
            	client = container.mClient;
            	client.resumeUpdate(handlerClient);
            	mMessageList = container.mChatMessages;
            	
            	mCurrentView = savedInstanceState.getInt("currentView", 1);
            	mUserName = savedInstanceState.getString("userName");
            	mIpAddress = savedInstanceState.getString("ipAddress");
            }
        }

        if(mCurrentView == 1) {
        	createChatterView();
        } else {
        	createSetupView();
        }
    }
    
	@Override
    public Object onRetainNonConfigurationInstance() {
		mIsRotateEvent = true;
		ReetainContainer container = new ReetainContainer();
		container.mChatMessages = mMessageList;
		container.mClient = client;
        return container;
    }
	
	@Override
    protected void onSaveInstanceState (Bundle outState) {
    	outState.putInt("currentView", mCurrentView);
    	outState.putString("userName", mUserName);
    	outState.putString("ipAddress", mIpAddress);
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onStop() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(SHARED, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userName", mUserName);
        editor.putString("ipAddress", mIpAddress);
        editor.putLong(TIMESTAMP, timestamp);

        // Commit the edits!
        editor.commit();
        
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	if(client != null && !mIsRotateEvent)
    		client.Stop();
    	super.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	if(mCurrentView != 0) {
        		mCurrentView = 0;
        		setContentView(createSetupView());
        		return true;
        	}
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case android.R.id.home:
	            Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
	            break;
            case R.id.menu_users:
                Toast.makeText(this, "Tapped users", Toast.LENGTH_SHORT).show();
                //TODO <--Here trying to figure out how to make a proper drop down
                LinearLayout lin = (LinearLayout)findViewById(R.id.user_list_container);
                setLayoutAnimSlidedownfromtop(lin, this);
                lin.addView(getLayoutInflater().inflate(R.layout.user_panel,null),0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void createChatterView() {
    	setContentView(R.layout.chat_display);
    	mCurrentView = 1;
    	
    	//setup the list
    	mList = (ListView) this.findViewById(R.id.list_chat_view);
    	
 		mAdapter = new CustomAdapter(this, R.layout.row, mMessageList);
 		mList.setAdapter(mAdapter);
 		mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    	
    	Button sendBtn = (Button) findViewById(R.id.button1);
    	sendBtn.setEnabled(true);
    	
    	EditText edit = (EditText) findViewById(R.id.ETSend);
    	
    	TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener(){
    		public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
    		    switch(actionId){
//    		    case EditorInfo.IME_NULL:
//        		    if (event.getAction() != KeyEvent.ACTION_DOWN) {
//        		    	return false;
//        		    }
//    		    	sendMessage();
//    		    	break;
    		    case EditorInfo.IME_ACTION_SEND:
    		    	sendMessage();
    		    	break;
    		    }
    		    return true;
    		}
    	};
    	edit.setOnEditorActionListener(exampleListener);
    	edit.requestFocus();
	}

	private View createSetupView() {
		if(client != null) {
			client.Stop();
			client = null;
		}
        View mSetupView = mLayoutInflater.inflate(R.layout.setup_display, null);
        setContentView(mSetupView);
 		
        EditText textUser = (EditText)findViewById(R.id.ETUserName);
        EditText serverIP = (EditText)findViewById(R.id.ETServerIP);
        
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(SHARED, 0);
        mUserName = settings.getString("userName", "your name");
        mIpAddress = settings.getString("ipAddress", "0.0.0.0");
        timestamp = settings.getLong(TIMESTAMP, 0);
        
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
        return mSetupView;
	}
    
    public void addMessage(String message) {
    	ChatMessage chatMessage = new ChatMessage();
    	chatMessage.message = message;
		mAdapter.add(chatMessage);
		mAdapter.notifyDataSetChanged();
    }
    
    public void Connect(View view) {
    	createChatterView();
    	createClient();
    }
    
    public void createClient() {
    	client = new Client(mIpAddress, mUserName, handlerClient);
    	client.connect();
    	client.Start();
    	client.sendUserLogon();
    	
    	GetOnlineUserList userList = new GetOnlineUserList();
    	client.SendMessage(new Gson().toJson(userList));
    	
    	// inform server to send messages earlier than <timestamp>
    	if (timestamp!=0)
    		client.getNewMessages(timestamp);
    }
    
    private void createHandler() {
    	handlerClient = new Handler() {
	    	public void handleMessage(Message msg) {
	    		boolean vibrate=true;
	    		switch (msg.what) {
				case MessageTypes.USERLOGON:
					addMessage("Logged on succesful");
					break;
				case MessageTypes.PUBLISHMESSAGE:
					Time time = new Time(((PublishMessage) msg.obj).timeStamp);
					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss: ");
					String strTime=sdf.format(time);
					String name = "you";
					// see if you are the sender
					if (!((PublishMessage) msg.obj).sender.equalsIgnoreCase(mUserName))
						name = ((PublishMessage) msg.obj).sender;
					addMessage(strTime + name + "\n" + ((PublishMessage) msg.obj).message);
					timestamp = ((PublishMessage) msg.obj).timeStamp;
					break;
				case MessageTypes.GETONLINEUSERLIST:
					//TODO update list of online users
					GetOnlineUserList userlist = (GetOnlineUserList) msg.obj;
					String[] list = userlist.userList;
					break;
				default:
					vibrate=false;
					break;
	    		}
	    		if (vibrate)
	    			((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);
	    	}
	    };
	}
    
    public void sendMessage() {
    	EditText edit = (EditText) findViewById(R.id.ETSend);
    	String msg = edit.getText().toString();
    	//addMessage("You: " + msg);
    	SendMessage message = new SendMessage();
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
	
	
	public static void setLayoutAnimSlidedownfromtop(ViewGroup panel, Context ctx) {

		  AnimationSet set = new AnimationSet(true);

		  Animation animation = new AlphaAnimation(0.0f, 1.0f);
		  animation.setDuration(100);
		  set.addAnimation(animation);

		  animation = new TranslateAnimation(
		      Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		      Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
		  );
		  animation.setDuration(500);
		  set.addAnimation(animation);

		  LayoutAnimationController controller =
		      new LayoutAnimationController(set, 0.25f);
		  panel.setLayoutAnimation(controller);

	}
}