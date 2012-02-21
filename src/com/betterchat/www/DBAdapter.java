package com.betterchat.www;

import com.betterchat.www.MessageStructures.SendMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	public static final String KEY_ID = "id";
	public static final String KEY_SENDER = "sender";
	public static final String KEY_RECEIVER = "receiver";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_TIMESTAMP = "timestamp";
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "chatter";
	private static final String DATABASE_TABLE = "messages";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE =
		"create table " + DATABASE_TABLE + "("
		+ KEY_ID + " integer primary key autoincrement, "
		+ KEY_SENDER + " text not null, "
		+ KEY_RECEIVER + " text not null, "
		+ KEY_MESSAGE + " text not null, "
		+ KEY_TIMESTAMP + " integer not null "
	+ ");";
	private final Context context;
	private DBHelper mDBHelper;
	private SQLiteDatabase db;
	
	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		mDBHelper = new DBHelper(ctx);
	}
	
	protected void finalize() throws Throwable
	{
		close();
		super.finalize();
	}
	
	public DBAdapter open() throws SQLiteException
	{
		db = mDBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		mDBHelper.close();
	}
	public long insertMessage(PublishMessage message)
	{
		ContentValues cv = new ContentValues();
		cv.put(KEY_MESSAGE, message.message);
		cv.put(KEY_RECEIVER, message.receiver);
		cv.put(KEY_SENDER,message.sender);
		cv.put(KEY_TIMESTAMP, message.timeStamp);
		
		try{
		return db.insert(DATABASE_TABLE, null, cv);
		}
		catch (SQLiteConstraintException e) {
			Log.d("sqlite","Database problem");
		}
		return -1;
	}
	public PublishMessage[] getLatestMessages(int maxNum)
	{
		
		Cursor cursor = db.query(DATABASE_TABLE, null, null, null, null, null, KEY_TIMESTAMP + " DESC","" + maxNum);
		PublishMessage[] messageArray = new PublishMessage[cursor.getCount()];
		for (int i=cursor.getCount() ;i>0;i--) {
			cursor.moveToPosition(i-1);
			PublishMessage message = new PublishMessage();
			message.message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
			message.receiver = cursor.getString(cursor.getColumnIndex(KEY_RECEIVER));
			message.sender = cursor.getString(cursor.getColumnIndex(KEY_SENDER));
			message.timeStamp = cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP));
			
			messageArray[cursor.getCount()-i] = message;
		}
		cursor.close();
		return messageArray;
	}
	public void clearAllMessages()
	{
		db.delete(DATABASE_TABLE, "1=1", null);
	}
	
	
	private static class DBHelper extends SQLiteOpenHelper
	{
		DBHelper(Context context) {
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// denne metode kaldes når databasen oprettes for første gang
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			
		}
		
	}
	
	
	

}
