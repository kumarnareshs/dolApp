package com.database;



import java.io.File;

import android.os.Environment;

public interface Constants {

	//public static final String HOST ="http://192.168.43.70";
	public static final String HOST ="http://192.168.1.6";
	//public static final String HOST ="http://192.168.1.3";
	public static final String PORT ="3000";
	public static final String DATABASE_NAME = "music.db";
	public static final int DATABASE_VERSION = 4;
	public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().toString();
	public static final String DATABASE_DIRECTORY = "mymusic";
	public static final File TEMP_DIR = new File(Environment.getExternalStorageDirectory()+ "/mymusic/");
	public static final String Temp_WavFile = "temp.wav";
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

	
	//Service 
	public static final String JOB_TYPE = "Job_Type";
	public static final String BACKGROUND_FINGERPRINT_JOB = "fingerprinting_job";
	
	//DAO Status
	public static final String FP_STATUS_NOMATCHFOUND = "no match found";
	public static final String FP_STATUS_FPGENERATED = "fp generated";
	public static final String FP_STATUS_FULLFPGENERATED = "full fp generated";
	public static final String FP_STATUS_FULLFPUPLOADED = "full fp uploaded";
	public static final String FP_STATUS_TRACKID_RECEIVED = "trackid received";
	public static final String FP_STATUS_METADATARECEIVED = "metadata received";
	
	public static final String SERVER_STATUS_READY_TO_UPLOAD = "ready to upload";
	public static final String SERVER_STATUS_UPLOADED = "uploaded";
}
