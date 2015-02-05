package com.fingerprint.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.database.Constants;
import com.fileupload.MyApplication;

import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.FingerprintDao.Properties;

public class FingerprintDB implements IFingerprintDB,Constants{

	private SQLiteDatabase db;
	private FingerprintDao fingerprintDao;
	private DaoSession daoSession;
	private DaoSession localdaoSession;

	public FingerprintDB(MyApplication app) {
		this.db = app.getGlobalDaoSession().getDatabase();
		this.daoSession = app.getGlobalDaoSession();
		this.fingerprintDao = daoSession.getFingerprintDao();
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

	@Override
	public void setFingerprintStatus(List<Long> listOfnomatchFoundSongs,
			String status) {
		
		List<Fingerprint> fplist = new ArrayList<Fingerprint>();
		for(Long l:listOfnomatchFoundSongs){
			Fingerprint fp = new Fingerprint();
			fp.setId(l);
			fp.setStatus(status);
			fplist.add(fp);
		}
		List<String> column = new ArrayList<String>();
		column.add(Properties.Status.columnName);
		fingerprintDao.updateColumnInTx(fplist, column);
		fingerprintDao.refresh(fplist);
		
	}

}
