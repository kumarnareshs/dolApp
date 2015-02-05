package com.fingerprint.database;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.database.Constants;
import com.fileupload.MyApplication;

public class CommonDB implements Constants,ICommonDB{

	private SQLiteDatabase db;

	public CommonDB(MyApplication app) {
		this.db=app.getGlobalDaoSession().getDatabase();
	}

	public Boolean setUploadedStatus(Long id,String tableName) {
		// TODO check for last uploaded date, if not null ,dont update uploaded date 
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		ContentValues cv = new ContentValues();
		cv.put(LAST_MODIFIED_DATE, now.toString());
		cv.put(LAST_UPLOADED_DATE, now.toString());
		cv.put(UPLOADED_DATE, now.toString());
		return (int)db.update(tableName, cv, "_id="+id,null) > 0;
	}
	
	
}
