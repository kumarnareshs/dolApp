package com.fileupload;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.database.Constants;

import de.greenrobot.daoexample.DaoMaster;

 class SDcardOpenHelper implements Constants {

	private static File dbFile;
	private static SQLiteDatabase db;

	private SDcardOpenHelper(){}
	
	public static SQLiteDatabase open() {
		if (db == null) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// TODO create database directory dir in sdcard
				dbFile = new File(SDCARD_DIRECTORY + "/" + DATABASE_DIRECTORY,
						DATABASE_NAME);
			} else {
				return null;
			}
			if (dbFile.exists()) {

				return openDB();
			} else {
				return createDB();
			}
		} else {
			return db;
		}
	}

	private static SQLiteDatabase openDB() {
		db = SQLiteDatabase.openDatabase(dbFile.toString(), null,
				SQLiteDatabase.OPEN_READWRITE);
		return db;
	}

	private static SQLiteDatabase createDB() {
		db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		db.close();
		db = SQLiteDatabase.openDatabase(dbFile.toString(), null,
				SQLiteDatabase.OPEN_READWRITE);
		DaoMaster.createAllTables(db, false);
		return db;

	}

}
