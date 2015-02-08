package com.fingerprint.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.database.Constants;
import com.fileupload.MyApplication;

import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao.Properties;

public class CommonDB implements Constants, ICommonDB {

	
	private SQLiteDatabase db;

	public CommonDB(MyApplication app) {
		this.db = app.getGlobalDaoSession().getDatabase();
	}


	public Boolean setInitialUploadedStatus(Long id, String tablename) {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		ContentValues cv = new ContentValues();
		cv.put(Properties.Lastmodifieddate.columnName, now.toString());
		cv.put(Properties.Lastuploadeddate.columnName, now.toString());
		cv.put(Properties.Uploadeddate.columnName, now.toString());
		cv.put(Properties.Status.columnName, SERVER_STATUS_UPLOADED);
		return (int) db.update(tablename, cv, "_id=" + id, null) > 0;
	}
	
}
