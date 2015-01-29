package de.greenrobot.daoexample;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.database.Constants;

public class SDcardOpenHelper implements Constants {

	private File dbFile;
	private SQLiteDatabase db;

	public SQLiteDatabase open() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			//TODO create database directory dir in sdcard
			dbFile = new File(SDCARD_DIRECTORY+"/"+DATABASE_DIRECTORY,DATABASE_NAME);
		} else {
			return null;
		}
		if (dbFile.exists()) {

			return openDB();
		} else {
			return createDB();
		}
	}

	private SQLiteDatabase openDB() {
		db = SQLiteDatabase.openDatabase(dbFile.toString(), null,
				SQLiteDatabase.OPEN_READWRITE);
		return db;
	}

	private SQLiteDatabase createDB() {
		db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		db.close();
		db = SQLiteDatabase.openDatabase(dbFile.toString(), null,
				SQLiteDatabase.OPEN_READWRITE);
		DaoMaster.createAllTables(db, false);
		return db;

	}

}

