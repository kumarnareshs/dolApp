package com.fingerprint.database;

import java.util.List;
import java.util.Map;

import android.app.Application;

import com.fileupload.MyApplication;

import de.greenrobot.daoexample.DaoSession;

public class DBAdapter implements IFingerprintDB,ICommonDB {

	private IFingerprintDB IF;
	
	public Boolean setInitialUploadedStatus(Long id, String tablename) {
		return IC.setInitialUploadedStatus(id, tablename);
	}

	public void setFingerprintStatus(Long listOfnomatchFoundSongs,
			String fpStatusNomatchfound) {
		IF.setFingerprintStatus(listOfnomatchFoundSongs, fpStatusNomatchfound);
	}

	private ICommonDB IC;
	private static DBAdapter dbadapter ;
	private MyApplication myapp;

	public void setFingerprintStatus(List<Long> listOfnomatchFoundSongs,
			String fpStatusNomatchfound) {
		IF.setFingerprintStatus(listOfnomatchFoundSongs, fpStatusNomatchfound);
	}


	public Map<Long, String> getListOfSongsPathInLocalDb() {
		return IF.getListOfSongsPathInLocalDb();
	}

	public boolean deleteSongsInLocalDb(List<Long> songlisttodelete) {
		return IF.deleteSongsInLocalDb(songlisttodelete);
	}

	private DBAdapter() {
	}

	private DBAdapter(Application app) {
		myapp = (MyApplication) app;
		IF = new FingerprintDB(myapp);
		IC = new CommonDB(myapp);
	}

	public static DBAdapter getInstance(Application app) {
		
		if (dbadapter == null) {
			dbadapter = new DBAdapter(app);
		}
		return dbadapter;
	}

	public DaoSession getGlobalDaoSession() {
		return myapp.getGlobalDaoSession();
	}

	public DaoSession getNewDaoSession() {
		return myapp.getNewDaoSession();
	}

	

}
