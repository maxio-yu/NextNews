package com.example.nextnews.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	static final String KEY_ROWID = "_id";
	static final String KEY_ID = "id";
	static final String KEY_TITLE = "title";
	static final String KEY_DESCRIPTION = "description";
	static final String KEY_GUID = "guid";
	static final String KEY_LINK = "link";
	static final String KEY_CATEGORY = "category";
	static final String KEY_EMOTION_LEVEL = "emotion_level";
	static final String KEY_CREATED_AT = "created_at";
	static final String KEY_UPDATED_AT = "updated_at";
	static final String KEY_PICTURE = "picture";
	static final String KEY_PUB_DATE = "pub_date";
	static final String TAG = "DBAdapter";

	static final String DATABASE_NAME = "MyDB";
	static final String NEWS_DATABASE_TABLE = "news";
	static final String READ_DATABASE_TABLE = "read_status";
	static final int DATABASE_VERSION = 1;

	static final String NEWS_DATABASE_CREATE = "create table "
			+ NEWS_DATABASE_TABLE
			+ "( _id integer primary key autoincrement, "
			+ "id integer not null, "
			+ "title text not null, "
			+ "description text not null, "
			+ "guid text not null, "
			+ "link text not null, "
			+ "category text not null, "
			+ "emotion_level integer not null,  "
			+ "created_at text not null,  "
			+ "updated_at text not null,  "
			+ "picture text not null,  " + "pub_date text not null);";
	
	static final String READ_DATABASE_CREATE = "create table "
			+ READ_DATABASE_TABLE 
			+ "( _id integer primary key autoincrement, "
			+ "id integer not null, " 
			+ "society integer not null, "
			+ "technology integer not null, " 
			+ "sports integer not null, "
			+ "entertainment integer not null, "
			+ "uncategory integer not null);";

	final Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;

	public DBAdapter(Context cxt) {
		this.context = cxt;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			try {
				db.execSQL(NEWS_DATABASE_CREATE);
				db.execSQL(READ_DATABASE_CREATE);
				ContentValues initialValues = new ContentValues();
				initialValues.put("id", 0);
				initialValues.put("society", 0);
				initialValues.put("technology", 0);
				initialValues.put("sports", 0);
				initialValues.put("entertainment", 0);
				initialValues.put("uncategory", 0);
				db.insert(READ_DATABASE_TABLE, null, initialValues);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.wtf(TAG, "Upgrading database from version " + oldVersion
					+ "to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS news");
			onCreate(db);
		}
	}
	
	public void clearTable() {
		String sql = "DELETE FROM " + NEWS_DATABASE_TABLE + ";";
		db.execSQL(sql);
		revertSeq();
	}

	private void revertSeq() {
		String sql = "update sqlite_sequence set seq=0 where name='"
				+ NEWS_DATABASE_TABLE + "'";
		db.execSQL(sql);
	}

	// open the database
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// close the database
	public void close() {
		DBHelper.close();
	}

	// insert a contact into the database
	public long insertNews(int id, String title, String description,
			String guid, String link, String category, int emotion_level,
			String created_at, String updated_at, String picture,
			String pub_date) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ID, id);
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DESCRIPTION, description);
		initialValues.put(KEY_GUID, guid);
		initialValues.put(KEY_LINK, link);
		initialValues.put(KEY_CATEGORY, category);
		initialValues.put(KEY_EMOTION_LEVEL, emotion_level);
		initialValues.put(KEY_CREATED_AT, created_at);
		initialValues.put(KEY_UPDATED_AT, updated_at);
		initialValues.put(KEY_PICTURE, picture);
		initialValues.put(KEY_PUB_DATE, pub_date);
		return db.insert(NEWS_DATABASE_TABLE, null, initialValues);
	}

	// delete a particular contact
	public boolean deleteNewsById(long rowId) {
		return db.delete(NEWS_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteNewsByCategory(String category) {
		return db.delete(NEWS_DATABASE_TABLE, KEY_CATEGORY + "=" + "'" + category + "'", null) > 0;
	}

	// obtain all the contacts
	public Cursor getAllNews() {
		return db.query(NEWS_DATABASE_TABLE, new String[] { KEY_ID, KEY_TITLE,
				KEY_DESCRIPTION, KEY_GUID, KEY_LINK, KEY_CATEGORY,
				KEY_EMOTION_LEVEL, KEY_CREATED_AT, KEY_UPDATED_AT, KEY_PICTURE,
				KEY_PUB_DATE }, null, null, null, null, null);
	}

	// obtain a particular news by category
	public Cursor getNewsByCategory(String category, String emotion_level) throws SQLException {
		Cursor mCursor = null;
		if (emotion_level.equals("全部")) {
			mCursor = db.query(true, NEWS_DATABASE_TABLE, new String[] { KEY_ID,
					KEY_TITLE, KEY_DESCRIPTION, KEY_GUID, KEY_LINK,
					KEY_CATEGORY, KEY_EMOTION_LEVEL, KEY_CREATED_AT,
					KEY_UPDATED_AT, KEY_PICTURE, KEY_PUB_DATE }, KEY_CATEGORY
					+ "=" + "'" + category + "'", null, null, null, null, null);
		} else if (emotion_level.equals("正能量")) {
			mCursor = db.query(true, NEWS_DATABASE_TABLE, new String[] { KEY_ID,
					KEY_TITLE, KEY_DESCRIPTION, KEY_GUID, KEY_LINK,
					KEY_CATEGORY, KEY_EMOTION_LEVEL, KEY_CREATED_AT,
					KEY_UPDATED_AT, KEY_PICTURE, KEY_PUB_DATE }, KEY_CATEGORY
					+ "=" + "'" + category + "' AND " + KEY_EMOTION_LEVEL + " >= 0" , null, null, null, null, null);
		} else if (emotion_level.equals("负能量")) {
			mCursor = db.query(true, NEWS_DATABASE_TABLE, new String[] { KEY_ID,
					KEY_TITLE, KEY_DESCRIPTION, KEY_GUID, KEY_LINK,
					KEY_CATEGORY, KEY_EMOTION_LEVEL, KEY_CREATED_AT,
					KEY_UPDATED_AT, KEY_PICTURE, KEY_PUB_DATE }, KEY_CATEGORY
					+ "=" + "'" + category + "' AND " + KEY_EMOTION_LEVEL + " < 0", null, null, null, null, null);
		}
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	// updates a contact
	public boolean updateNews(long rowId, int id, String title,
			String description, String guid, String link, String category,
			int emotion_level, String created_at, String updated_at,
			String picture, String pub_date) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ID, id);
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DESCRIPTION, description);
		initialValues.put(KEY_GUID, guid);
		initialValues.put(KEY_LINK, link);
		initialValues.put(KEY_CATEGORY, category);
		initialValues.put(KEY_EMOTION_LEVEL, emotion_level);
		initialValues.put(KEY_CREATED_AT, created_at);
		initialValues.put(KEY_UPDATED_AT, updated_at);
		initialValues.put(KEY_PICTURE, picture);
		initialValues.put(KEY_PUB_DATE, pub_date);
		return db.update(NEWS_DATABASE_TABLE, initialValues,
				KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean updateReadStatus(String category) {
		ContentValues initialValues = new ContentValues();
		Cursor cursor = db.query(true, READ_DATABASE_TABLE, new String[] { category }, "id = 0"	, null, null, null, null, null);
		Log.w("DDDDDBBBBB", "WWWW");
		if (cursor != null)
			cursor.moveToFirst();
		int categoryIndex = cursor.getColumnIndex(category);
		int count = cursor.getInt(categoryIndex);
		Log.w("DDDDDBBBBB", ""+count);
		initialValues.put(category, count+1);
		return db.update(READ_DATABASE_TABLE, initialValues,
				"id = 0", null) > 0;
	}
	
	public Cursor getReadStatus() {
		Cursor cursor = db.query(READ_DATABASE_TABLE, new String[] { "society", "technology", "sports", "entertainment", "uncategory" }, null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
}