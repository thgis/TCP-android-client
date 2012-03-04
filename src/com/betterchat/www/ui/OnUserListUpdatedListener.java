package com.betterchat.www.ui;

import java.util.ArrayList;

public interface OnUserListUpdatedListener {
	public void OnUserListUpdated(ArrayList<String> users, int action);
}
