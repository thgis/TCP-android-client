package com.betterchat.www.ui;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.betterchat.www.ChatMessage;
import com.betterchat.www.Client;
import com.betterchat.www.DBAdapter;
import com.betterchat.www.MessageTypes;
import com.betterchat.www.PublishMessage;
import com.betterchat.www.R;
import com.betterchat.www.MessageStructures.GetOnlineUserList;
import com.betterchat.www.MessageStructures.SendMessage;
import com.betterchat.www.utility.RetainedFragment;
import com.google.gson.Gson;

public class ChatFragment extends ListFragment implements OnClickListener {
	private LayoutInflater mLayoutInflater;
	private ArrayAdapter<ChatMessage> mAdapter;
	private DBAdapter mDBAdapter;
	private Handler handlerClient;
	private ArrayList<ChatMessage> mMessageList;
	private OnUserListUpdatedListener mListener;
	private RetainedFragment mWorkFragment;
	private Client client;
	public static final int UPDATED_USER_LIST = 0;
	private String mUserName = "test";
	private long timestamp = 0;
	private final String TIMESTAMP = "timestamp";
	private final String SHARED = "sharedchatterpref";
	//TODO Make sure client has a proper lock
	//TODO Remember mMessageList on flip screen somehow
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnUserListUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createHandler();
        FragmentManager fm = getFragmentManager();
        // Check to see if we have retained the worker fragment.
        mWorkFragment = (RetainedFragment)fm.findFragmentByTag("work"); 
        // If not retained (or first time running), we need to create it.
        if (mWorkFragment == null) {
        	mWorkFragment = new RetainedFragment();
        	// Tell it who it is working with.
        	mWorkFragment.setTargetFragment(this, 0);
        	fm.beginTransaction().add(mWorkFragment, "work").commit();
        }
        client = mWorkFragment.getClient();
        client.setHandler(handlerClient);
        
        
    }
    
    @Override
    public void onStart() {
    	super.onStart();
        mDBAdapter = new DBAdapter(getActivity());
        mDBAdapter.open();
        
        // load timestamp
        SharedPreferences settings = getActivity().getSharedPreferences(SHARED, 0);
        timestamp = settings.getLong(TIMESTAMP, 0);
        loadMessages();
 		// getNewMessages gets new messages from server
 		client.getNewMessages(timestamp);

    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayoutInflater = inflater;
		View view = mLayoutInflater.inflate(R.layout.chat_display, null);
		
    	mMessageList = new ArrayList<ChatMessage>();
    	// read messages from data base

    	
 		mAdapter = new CustomAdapter(getActivity(), R.layout.row, mMessageList);
 		
 		
// 		this.getListView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
 		setListAdapter(mAdapter);
    	
 		Button sendBtn = (Button) view.findViewById(R.id.chat_display_send_btn);
    	sendBtn.setEnabled(true);
    	sendBtn.setOnClickListener(this);
    	
    	EditText edit = (EditText) view.findViewById(R.id.ETSend);
    	
    	TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener(){
    		public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
    		    switch(actionId){
    		    case EditorInfo.IME_ACTION_SEND:
    		    	sendMessage();
    		    	break;
    		    }
    		    return true;
    		}
    	};
    	edit.setOnEditorActionListener(exampleListener);
    	edit.requestFocus();
		
		return view;
	}
	
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.chat_display_send_btn) {
			sendMessage();
		}
	}

	@Override
    public void onListItemClick(ListView list, View view, int position, long id) {
//    	final EventItem item = (EventItem) list.getAdapter().getItem(position);
	}
	
	public class CustomAdapter extends ArrayAdapter<ChatMessage> {
		private ArrayList<ChatMessage> mListItems;
		
		public CustomAdapter (Context context, int rowResourceId, ArrayList<ChatMessage> items) {
			super(context, rowResourceId, items);
	        mListItems = items;
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
	
    private String constructStringMessage(PublishMessage msg) {
    	Time time = new Time(msg.timeStamp);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss: ");
		String strTime=sdf.format(time);
		String name = "you";
		// see if you are the sender
		if (!msg.sender.equalsIgnoreCase(mUserName))
			name = msg.sender;
		return strTime + name + "\n" +  msg.message;
    }
    
    public void sendMessage() {
    	EditText edit = (EditText) getView().findViewById(R.id.ETSend);
    	String msg = edit.getText().toString();
    	SendMessage message = new SendMessage();
    	message.message = msg;
    	message.receiver = "all";
    	message.sender = mUserName;
    	Gson gson = new Gson();
    	String serializedMessage = gson.toJson(message);
    	client.SendMessage(serializedMessage);
    	edit.setText("");
    }
    
    public void addMessage(String message) {
    	ChatMessage chatMessage = new ChatMessage();
    	chatMessage.message = message;
		mAdapter.add(chatMessage);
		mAdapter.notifyDataSetChanged();
		/* Det har v�ret sjovt! */
    }
    
    private void loadMessages() {
    	PublishMessage[] messages = mDBAdapter.getLatestMessages(5);
    	for (int i=0;i<messages.length;i++) {
    		addMessage(constructStringMessage(messages[i]));
    	}
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
					addMessage(constructStringMessage((PublishMessage)msg.obj));
					timestamp = (int) ((PublishMessage) msg.obj).timeStamp;
					//  save message to database
					mDBAdapter.insertMessage((PublishMessage)msg.obj);
					break;
				case MessageTypes.GETONLINEUSERLIST:
					GetOnlineUserList userlist = (GetOnlineUserList) msg.obj;
					String[] list = userlist.userList;
					ArrayList<String> users = new ArrayList<String>();
					for(String user : list) {
						users.add(user);
					}
					mListener.OnUserListUpdated(users, UPDATED_USER_LIST);
					break;

				default:
					vibrate=false;
					break;
	    		}
	    		if (vibrate)
	    			((Vibrator)getActivity().getSystemService(Activity.VIBRATOR_SERVICE)).vibrate(300);
	    	}
	    };
	}
    @Override
    public void onStop()
    {
    	mDBAdapter.close();
    	super.onStop();
    }
}
