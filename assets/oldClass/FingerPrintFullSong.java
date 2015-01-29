package com.fingerprint.upload;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.convert.mp3.FingerPrintGenerator;
import com.database.Constants;
import com.fingerprint.upload.RemoteService.FingerPrintTask;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.FingerprintDao.Properties;
import de.greenrobot.daoexample.SDcardOpenHelper;

public class FingerPrintFullSong implements Constants{

	private List<Long> listOfSongsToFingerPrintFully;
	private int songCounter =0;
	private SDcardOpenHelper sdhelper;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private Object currentFingerPrintingSong;
	private Context _context;
	private Util util;
	private Handler fpHandler;	
	private static int NoOfDatabaseOperationDone=0;
	private static int NoOfSongProcessed=0;
	private FingerPrintTask fpTask = new FingerPrintTask();
	private static Map<String,String> statusmap;
	@SuppressWarnings("unused")
	private static final String TAG = "com.fingerprint.upload.FingerPrintFullSong";
	
	FingerPrintFullSong(Context _context,List<Long> listOfSongsToFingerPrintFully){
		this.listOfSongsToFingerPrintFully=listOfSongsToFingerPrintFully;
		sdhelper = new SDcardOpenHelper();
		db = sdhelper.open();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		this._context=_context;
		util = new Util(_context);
		
	}
	class FingerPrintFullSongTask implements Runnable{

		@Override
		public void run() {
			if (listOfSongsToFingerPrintFully.size()>songCounter) {
				Long id=listOfSongsToFingerPrintFully.get(songCounter);
				QueryBuilder<Fingerprint> qb = daoSession.getFingerprintDao().queryBuilder();
				qb.where(Properties.Id.eq(id),Properties.Fulllengthfingerprint.isNotNull());
				List<Fingerprint> fplist = qb.list();
				if(fplist.size()>0){					
					fingerprintMusic(fplist.get(0));
				}
				songCounter++;
			}else{
				//TestServerSyncAdapter sync = new TestServerSyncAdapter(getApplication());
				//sync.startSync(FingerprintDao.TABLENAME);
			}
		}
		
	}
	


	private void fingerprintMusic(final Fingerprint fingerprint) {
		
		File filename = new File(fingerprint.getFilepath());
		currentFingerPrintingSong = fingerprint.getFilepath();
		Log.i(TAG, "::fingerprintMusic:" + currentFingerPrintingSong);
		FingerPrintGenerator fpg = new FingerPrintGenerator(_context, filename,true,
				new FingerPrintGenerator.FingerPrintListener() {

					 
					@Override
					public void onCompletedSuccess(String fingerPrintedString) {
						//android.os.Debug.waitForDebugger();
						//TODO remove it
						
						//statusmap.put(NO_OFFINGERPRINT_OPERATION_DONE, ++NoOffingerprintOperationDone+"");
						Long rowId=UpdateFingerPrintedSongInDB(fingerPrintedString,fingerprint);
						
						Log.i(TAG, "::onCompletedSuccess: FingerPrintGenerated Successfully");
						Log.i(TAG, "::onCompletedSuccess: Path "+fingerprint.mpath);
						if(rowId!= null && util.isConnectingToInternet()){
							//modify the song length and ingest to the FP Server Initally
							//new FingerPrintServer().Ingest(fingerPrint,songToFingerprint.mSongName,RemoteService.this.getApplication());
							//new FingerPrintServer().Query(fingerPrint,RemoteService.this.getApplication());
							statusmap.put(NO_OF_DATABASE_OPERATION_DONE, ++NoOfDatabaseOperationDone+"");
							 ServerSyncAdapter sync = new  ServerSyncAdapter(_context);
							sync.startSync(FingerprintDao.TABLENAME,rowId);
							statusmap.put(NO_OF_SONG_PROCESSED, ++NoOfSongProcessed+"");
						}
						 
						fpHandler.postDelayed(fpTask, 1000L);

						
					}

					private Long UpdateFingerPrintedSongInDB(
							String fingerPrintedString, Fingerprint fingerprint) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void onCompletedFailure(String ExceptionMsg) {
						android.os.Debug.waitForDebugger();
						//TODO:: log the request
							Log.e(TAG, "FingerPrint Error" +ExceptionMsg);
							fpHandler.postDelayed(fpTask, 1000L);
						 
					}
				});
		fpg.generate();

	}
}
