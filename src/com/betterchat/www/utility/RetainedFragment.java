package com.betterchat.www.utility;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.betterchat.www.Client;
import com.betterchat.www.MessageStructures.GetOnlineUserList;
import com.betterchat.www.ui.ChatFragment;
import com.google.gson.Gson;

public class RetainedFragment extends Fragment {
	private Client client;
	private String mIpAddress = "176.34.177.147";
	private String mUserName = "test";
	private int timestamp = 0;
	
	private Handler mHandler;
    /**
     * Fragment initialization.  We way we want to be retained and
     * start our thread.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tell the framework to try to keep this fragment around
        // during a configuration change.
        setRetainInstance(true);

        // Start up the worker
        client = new Client(mIpAddress, mUserName, mHandler);
        client.pauseUpdate();
        boolean isConnected = startClient();
        
    	if(isConnected) {
    		//loadMessages();	//TODO Thomas to fix
    	} else {
    		// we didn't connect
    		//TODO Handle it
    	}
    }
    
    /**
     * This is called when the Fragment's Activity is ready to go, after
     * its content view has been installed; it is called both after
     * the initial fragment creation and after the fragment is re-attached
     * to a new activity.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ChatFragment targetFragment = (ChatFragment) getTargetFragment();
        mHandler = targetFragment.getHandler();
        
        // We are ready for our thread to go.
        client.resumeUpdate(mHandler);
    }

    
    /**
     * This is called right before the fragment is detached from its
     * current activity instance.
     */
    @Override
    public void onDetach() {
        // This fragment is being detached from its activity.  We need
        // to make sure its thread is not going to touch any activity
        // state after returning from this function.
    	client.pauseUpdate();
        super.onDetach();
    }
    
	private boolean startClient() {
    	boolean isConnected = client.connect();
    	if(isConnected) {
	    	client.Start();
	    	client.sendUserLogon();
	    	
	    	GetOnlineUserList userList = new GetOnlineUserList();
	    	client.SendMessage(new Gson().toJson(userList));
	    	
	    	// inform server to send messages earlier than <timestamp>
	    	if (timestamp!=0)
	    		client.getNewMessages(timestamp);
    	}
    	return isConnected;
    }

	public void sendMessage(String serializedMessage) {
		client.SendMessage(serializedMessage);
	}
}
