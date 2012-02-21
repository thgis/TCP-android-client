package com.betterchat.www.utility;

import android.util.Log;

public class Logger {
	private static final int MAX_MESSAGE_LENGHT = 3000;
	private static final boolean isLoggerEnabled = true;
	
	private static String mTag = "Chatter";
	
	public static void log(String message) {
		if (isLoggerEnabled) {
			if (message.length() < MAX_MESSAGE_LENGHT) {
				Log.i(mTag, message);
			} else {
				Log.i(mTag, message.substring(0, MAX_MESSAGE_LENGHT));
			}
		}
	}
	
	public static void error(String message) {
		if (isLoggerEnabled) {
			if (message.length() < MAX_MESSAGE_LENGHT) {
				Log.e(mTag, message);
			} else {
				Log.e(mTag, message.substring(0, MAX_MESSAGE_LENGHT));
			}
		}
	}
}
