package com.betterchat.www;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.betterchat.www.animation.ExpandCollapseAnimation;
import com.betterchat.www.ui.OnUserListUpdatedListener;
import com.betterchat.www.ui.actionbar.ActionBarActivity;

public class ChatterActivity extends ActionBarActivity implements OnUserListUpdatedListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main_view);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//        		return true;
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
                HorizontalScrollView lin = (HorizontalScrollView) findViewById(R.id.user_list_scrollview);
                ExpandCollapseAnimation animation = new ExpandCollapseAnimation(lin, 500);
                lin.startAnimation(animation);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void OnUserListUpdated(ArrayList<String> users, int action) {
		LinearLayout userContainer = (LinearLayout)findViewById(R.id.user_list_container);
		userContainer.removeAllViews();
		if(users != null) {
			for(String user : users) {
				TextView userView = (TextView) getLayoutInflater().inflate(R.layout.user, null);
				userView.setText(user);
				userContainer.addView(userView);
			}
		}
	}
}