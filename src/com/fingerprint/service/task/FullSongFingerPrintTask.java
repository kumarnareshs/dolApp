package com.fingerprint.service.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.convert.mp3.FingerPrintGenerator;
import com.database.Constants;
import com.fingerprint.service.server.ServerSyncAdapter;
import com.fingerprint.upload.Song;
import com.fingerprint.upload.Util;

import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.FingerprintDao.Properties;



public class FullSongFingerPrintTask implements Constants{


 
	private DaoSession daoSession;
	private Util util;
	private static int NoOffingerprintOperationDone=0;
	private static int NoOfDatabaseOperationDone=0;
	private static int NoOfSongProcessed=0;
	private static Map<String,String> statusmap;
	private String currentFingerPrintingSong;
	private  final String TAG = getClass().getName();
	private Application app;
	private Context _context;
	private FingerprintTaskListener fplistener;
	
	public FullSongFingerPrintTask(Application app,Context context,FingerprintTaskListener fplistener){
		this._context=context;
		this.app=app;
		this.fplistener=fplistener;
	}
	
	private void updateFingerPrintedSongInDB(Song FingerprintedSong ,String fingerprint,Long RowId) {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		Fingerprint fp = new Fingerprint();
		fp.setFulllengthfingerprint(fingerprint);
		fp.setIsfulllengthfingerprintgenerated(true);
		fp.setIsfulllengthfingerprintuploaded(false);
		fp.setLastmodifieddate(now);
		FingerprintDao fpdao=daoSession.getFingerprintDao();
		List<String> fields = new ArrayList<String>();
		fields.add(Properties.Fulllengthfingerprint.columnName);
		fields.add(Properties.Lastmodifieddate.columnName);
		fpdao.updateColumn(fp, fields);
	}
	
	public void fingerprintMusic(final Song songToFingerprint,final Long RowId) {
		
		File filename = new File(songToFingerprint.mpath);
		currentFingerPrintingSong = songToFingerprint.mpath;
		Log.i(TAG, "::fingerprintMusic:" + currentFingerPrintingSong);
		FingerPrintGenerator fpg = new FingerPrintGenerator(_context, filename,true,
				new FingerPrintGenerator.FingerPrintListener() {
	
					 
					@Override
					public void onCompletedSuccess(String fingerPrint) {
						//android.os.Debug.waitForDebugger();
						//TODO remove it
						
						
					}
	
					@Override
					public void onCompletedFailure(String ExceptionMsg) {
						android.os.Debug.waitForDebugger();
						//TODO:: log the request
							Log.e(TAG, "FingerPrint Error" +ExceptionMsg);
							fplistener.onFailure();
					}

					@Override
					public void onCompletedSuccess(String fingerPrint,
							Double length) {
						// TODO Auto-generated method stub
						statusmap.put(NO_OFFINGERPRINT_OPERATION_DONE, ++NoOffingerprintOperationDone+"");
						updateFingerPrintedSongInDB(songToFingerprint,fingerPrint,RowId);
						statusmap.put(NO_OF_DATABASE_OPERATION_DONE, ++NoOfDatabaseOperationDone+"");
						
						Log.i(TAG, "::onCompletedSuccess: Path "+songToFingerprint.mpath);
						if(util.isConnectingToInternet()){
							ServerSyncAdapter syaAdapter = new ServerSyncAdapter(app);
							syaAdapter.sendFullFingerPrint(RowId,fingerPrint,length);
						}
						fplistener.onComplete();
	
						
					}
				});
		fpg.generate();
	
	}	
}
