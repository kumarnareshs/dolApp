package com.fingerprint.service.task;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.convert.mp3.FingerPrintGenerator;
import com.database.Constants;
import com.fingerprint.server.ServerSyncAdapter;
import com.fingerprint.upload.Song;
import com.fingerprint.upload.Util;

import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;



public class InitialFingerPrintTask implements Constants{


 
	
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
	private IInitialFingerPrintTaskListener fplistener;
	
	public InitialFingerPrintTask(Application app,Context context,IInitialFingerPrintTaskListener fplistener){
		this._context=context;
		this.app=app;
		this.fplistener=fplistener;
	}
	
	private Long insertFingerPrintedSongInDB(Song FingerprintedSong ,String fingerprint) {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		Fingerprint fp = new Fingerprint();
		fp.setFingerprint(fingerprint);
		fp.setFilepath(FingerprintedSong.mpath);
		fp.setFileformat(FingerprintedSong.mimetype);
		fp.setFilename(FingerprintedSong.mSongName);
		fp.setCreateddate(now);
		fp.setFingerprintcreateddate(now);
		fp.setLastmodifieddate(now);
		fp.setIsuploaded(false);
		fp.setStatus(FP_STATUS_FPGENERATED);
		fp.setAndroidmusicid((Long)FingerprintedSong.mSongId);
		FingerprintDao fpdao=daoSession.getFingerprintDao();
		Long rowId=fpdao.insert(fp);
		if(rowId!=-1){
			return rowId;
		}
		Log.i(TAG, "Inserted for "+fp);
		return null;
	}
	
	 
		
	
	public void fingerprintMusic(final Song songToFingerprint) {
		
		File filename = new File(songToFingerprint.mpath);
		currentFingerPrintingSong = songToFingerprint.mpath;
		Log.i(TAG, "::fingerprintMusic:" + currentFingerPrintingSong);
		FingerPrintGenerator fpg = new FingerPrintGenerator(_context, filename,false,
				new FingerPrintGenerator.FingerPrintListener() {
	
					 
					@Override
					public void onCompletedSuccess(String fingerPrint) {
						//android.os.Debug.waitForDebugger();
						//TODO remove it
						
						statusmap.put(NO_OFFINGERPRINT_OPERATION_DONE, ++NoOffingerprintOperationDone+"");
						Long rowId=insertFingerPrintedSongInDB(songToFingerprint,fingerPrint);
						
						Log.i(TAG, "::onCompletedSuccess: Path "+songToFingerprint.mpath);
						if(rowId!= null && util.isConnectingToInternet()){
							//modify the song length and ingest to the FP Server Initally
							//new FingerPrintServer().Ingest(fingerPrint,songToFingerprint.mSongName,RemoteService.this.getApplication());
							//new FingerPrintServer().Query(fingerPrint,RemoteService.this.getApplication());
							statusmap.put(NO_OF_DATABASE_OPERATION_DONE, ++NoOfDatabaseOperationDone+"");
							 ServerSyncAdapter sync = new  ServerSyncAdapter(app);
							sync.sendToServer(rowId, FingerprintDao.TABLENAME);
							statusmap.put(NO_OF_SONG_PROCESSED, ++NoOfSongProcessed+"");
						}
						fplistener.onComplete();
					}
	
					@Override
					public void onCompletedFailure(String ExceptionMsg) {
						android.os.Debug.waitForDebugger();
						//TODO:: log the request
							Log.e(TAG, "FingerPrint Error" +ExceptionMsg);
							fplistener.onFailure();
							//fpHandler.postDelayed(fpTask, 1000L);
					}

					@Override
					public void onCompletedSuccess(String fingerPrint,
							Double length) {
						// TODO Auto-generated method stub
						
					}
				});
		fpg.generate();
	
	}	
}
