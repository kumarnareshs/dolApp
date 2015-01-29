package com.fingerprint.upload;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.database.Constants;

import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.SDcardOpenHelper;

public class DB implements Constants{

	
	private SQLiteDatabase db;
	private SDcardOpenHelper sdhelper;
	
	DB(){
		sdhelper = new SDcardOpenHelper();
		db = sdhelper.open();
	}
	
	DB(SQLiteDatabase db){
		this.db =db;
	}
	
	
	public Map<Long,String> getListOfSongsPathInLocalDb() {
		String filePathSqlQuery="SELECT "+FingerprintDao.Properties.Filepath.name +" , "+
				FingerprintDao.Properties.Androidmusicid.name+" FROM "+FingerprintDao.TABLENAME;
		Map<Long,String> result = new HashMap<Long,String>();
		
		Cursor c =db.rawQuery(filePathSqlQuery, null);
		if (c.moveToFirst()) {
			do {
				result.put(c.getLong(1),c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
		return result;
	}

	public boolean deleteSongsInLocalDb(List<Long> songlisttodelete) {
		Long arraylist[] = songlisttodelete.toArray(new Long[0]);
		String deletelist = "";
		for (Long l : arraylist) {
			deletelist += Long.toString(l) + ",";
		}
		deletelist = deletelist.substring(0, deletelist.length() - 1);
		String whereClause = FingerprintDao.Properties.Androidmusicid + " IN "
				+ " ( " + deletelist + " ) ";
		return db.delete(FingerprintDao.TABLENAME, whereClause, null) > 0;

	}

	public Boolean setUploadedStatus(Long id) {
		// TODO check for last uploaded date, if not null ,dont update uploaded date 
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		ContentValues cv = new ContentValues();
		cv.put(LAST_MODIFIED_DATE, now.toString());
		cv.put(LAST_UPLOADED_DATE, now.toString());
		cv.put(UPLOADED_DATE, now.toString());
		return (int)db.update(FingerprintDao.TABLENAME, cv, "_id="+id,null) > 0;
	}
}
