package com.database;



import java.io.File;

import android.os.Environment;

public interface Constants {

	public static final String HOST ="http://192.168.228.50";
	public static final String PORT ="3000";
	public static final String DATABASE_NAME = "music.db";
	public static final int DATABASE_VERSION = 4;
	public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().toString();
	public static final String DATABASE_DIRECTORY = "mymusic";
	File TEMP_DIR = new File(Environment.getExternalStorageDirectory()+ "/mymusic/");
	/*{
	    "appname": 
	        {
	                "fingerprintstatus": "c200",
	                "name": "Ravi Tamada",
	                "email": "ravi@gmail.com",
	                "address": "xx-xx-xxxx,x - street, x - country",
	                "gender" : "male",
	                
	        }
	  
	}*/
	//UserText Header Tag
	public static final String FINGERPRINT_STATUS = "Fingerprintstatus";
	public static final String APP_NAME = "appname";
	public static final String ALLSONGLIST2 = "allsonglist";
	public static final String LIST_TO_FINGERPRINT = "listToFingerprint";
	public static final String LISTOF_SONG_PATH_IN_LOCAL_DB = "listofSongPathInLocalDb";
	public static final String NO_OFFINGERPRINT_OPERATION_DONE = "NoOffingerprintOperationDone";
	public static final String NO_OF_DATABASE_OPERATION_DONE = "NoOfDatabaseOperationDone";
	public static final String NO_OF_SONG_PROCESSED = "NoOfSongProcessed";
	
	
	//common with other packages, so once changed here, change it in other packages also
	public static final String MyPREFERENCES = "MyPREFERENCES";
	public static final String ANDROIDID = "androidid";

	//DB related
	public static final String IS_UPLOADED = "isuploaded";
	public static final String UPLOADED_DATE = "uploadeddate";
	public static final String LAST_UPLOADED_DATE = "lastuploadeddate";
	public static final String LAST_MODIFIED_DATE = "lastmodifieddate";
	
}
