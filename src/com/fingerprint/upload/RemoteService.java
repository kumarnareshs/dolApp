package com.fingerprint.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.convert.mp3.FingerPrintGenerator;
import com.convert.mp3.FingerPrintGenerator.FingerPrintListener;
import com.database.Constants;
import com.fileupload.MyApplication;
import com.fileupload.TestServerSyncAdapter;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.remoting.adapters.Adapter;

import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.SDcardOpenHelper;

public class RemoteService extends Service implements Constants {
	
	
	private Handler serviceHandler;
	private Handler trackIdHandler;
	private Handler fpHandler;
	private Task myTask = new Task();
	private TrackIdWorker trackidworker = new TrackIdWorker();
	private FingerPrintTask fpTask = new FingerPrintTask();
	private List<Long> listOfSongsToFingerPrintFully;
	private DaoSession daoSession;
	private DaoMaster daoMaster;
	private SQLiteDatabase db;
	private Util util;
	private static int songCounter=0;
	private static int NoOffingerprintOperationDone=0;
	private static int NoOfDatabaseOperationDone=0;
	private static int NoOfSongProcessed=0;
	private SDcardOpenHelper sdhelper;
	private MyApplication app;
	private RestAdapter restAdapter;
	private FingerPrintRepository fingerPrintRepo;
	private static Map<String,String> statusmap;
	@SuppressWarnings("unused")
	private static final String TAG = "com.fingerprint.upload.RemoteService";
	
	@Override
	public IBinder onBind(Intent arg0) { 
		serviceHandler = new Handler();
		fpHandler= new Handler();
		trackIdHandler = new Handler();
		serviceHandler.postDelayed(myTask, 1000L);
		trackIdHandler.postDelayed(trackidworker, 1000L);
		statusmap= new HashMap<String,String>();
		util = new Util(getApplicationContext());
		sdhelper = new SDcardOpenHelper();
		db = sdhelper.open();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		app = (MyApplication) this.getApplication();
		restAdapter = app.getLoopBackAdapter();
		fingerPrintRepo = restAdapter.createRepository(FingerPrintRepository.class);
		Log.d(getClass().getSimpleName(), "onBind()");
		return remoteServiceStub;
	}

	private IRemoteService.Stub remoteServiceStub = new IRemoteService.Stub() {
		public String getCurrentSong() throws RemoteException {
			return currentFingerPrintingSong;
		}

		@Override
		public Map getStatus() throws RemoteException {
			// TODO Auto-generated method stub
			return statusmap;
		}

	};

	 

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(getClass().getSimpleName(),"onCreate()");
		
	}
	@Override
	public void onDestroy() {
		/* TODO: service is not destroying */
		if (serviceHandler != null) {
			serviceHandler.removeCallbacks(myTask);
		}
		statusmap=null;
		serviceHandler = null;
		fpHandler.removeCallbacks(fpTask);
		fpHandler=null;
		stopSelf();
		Log.d(getClass().getSimpleName(),"onDestroy() service");
		super.onDestroy();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		Log.d(getClass().getSimpleName(), "onStart()"); 
			
		return START_NOT_STICKY;
	}
	
	class TrackIdWorker implements Runnable{
		
		@Override
		public void run() {
			android.os.Debug.waitForDebugger();
			 restAdapter.createRepository(FingerPrintRepository.class);
			 fingerPrintRepo.invokeStaticMethod("getnomatchfoundsongs", null, new Adapter.Callback() {
				
				

				@Override
				public void onSuccess(String response) {
					Log.i(TAG, "getnomatchfoundsongs"+response);
					trackIdHandler.postDelayed(trackidworker, 3000L);
					JSONObject jObject;
					try {
						jObject = new JSONObject(response);
						String cids = jObject.getString("cids");
						listOfSongsToFingerPrintFully = Util.ConvertStringToList(cids);
						updateSongNotAvailableStatus(listOfSongsToFingerPrintFully);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				private void updateSongNotAvailableStatus(
						List<Long> listOfSongsToFingerPrintFully) {
					
					
				}

				@Override
				public void onError(Throwable t) {
				android.os.Debug.waitForDebugger();
					Log.e(TAG, "getnomatchfoundsongs"+t);
					trackIdHandler.postDelayed(trackidworker, 3000L);
				}
			});
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	class Task implements Runnable {

		public void run() {
			SongLoader as = new SongLoader();
			List<Song> allsonglist = as.loadInBackground(getApplicationContext());
			statusmap.put(ALLSONGLIST2, allsonglist.size()+"");
			Log.i(TAG, "::run:" + "all song list "+allsonglist.size());
			Map<Long,String> listofSongPathInLocalDb =new DB(db).getListOfSongsPathInLocalDb();
			statusmap.put(LISTOF_SONG_PATH_IN_LOCAL_DB, listofSongPathInLocalDb.size()+"");
			Log.i(TAG, "::run:" + "list of songs in local Db"+listofSongPathInLocalDb.size());
			listToFingerprint = getListOfSongsToFingerPrint(allsonglist,listofSongPathInLocalDb);
			Log.i(TAG, "::run:" + "list to fingerprint"+listToFingerprint.size());
			statusmap.put(LIST_TO_FINGERPRINT, listToFingerprint.size()+"");
			fpHandler.postDelayed(fpTask, 1000L);
			if(allsonglist.size()<listofSongPathInLocalDb.size()){
				deleteSongsInLocalDb(allsonglist,listofSongPathInLocalDb);
			}
			 
		}

		
		private void deleteSongsInLocalDb(List<Song> allsonglist,
				Map<Long, String> listofSongPathInLocalDb) {
			List<Long> songlisttodelete=getListOfSongsToDelete(allsonglist,listofSongPathInLocalDb);
			if(new DB(db).deleteSongsInLocalDb(songlisttodelete)){
				Log.i(TAG, "Deleted "+songlisttodelete.size()+" Songs in DB");
			};
			
		}

		 
	}

	
	class FingerPrintTask implements Runnable{

		@Override
		public void run() {
			if (listToFingerprint.size()>songCounter) {
				fingerprintMusic(listToFingerprint.get(songCounter));
				songCounter++;
			}else{
				TestServerSyncAdapter sync = new TestServerSyncAdapter(getApplication());
				sync.startSync(FingerprintDao.TABLENAME);
			}
		}
		
	}


	private void fingerprintMusic(final Song songToFingerprint) {
		
		File filename = new File(songToFingerprint.mpath);
		currentFingerPrintingSong = songToFingerprint.mpath;
		Log.i(TAG, "::fingerprintMusic:" + currentFingerPrintingSong);
		FingerPrintGenerator fpg = new FingerPrintGenerator(getApplicationContext(), filename,false,
				new FingerPrintGenerator.FingerPrintListener() {

					 
					@Override
					public void onCompletedSuccess(String fingerPrint) {
						//android.os.Debug.waitForDebugger();
						//TODO remove it
						
						statusmap.put(NO_OFFINGERPRINT_OPERATION_DONE, ++NoOffingerprintOperationDone+"");
						Long rowId=insertFingerPrintedSongInDB(songToFingerprint,fingerPrint);
						
						Log.i(TAG, "::onCompletedSuccess: FingerPrintGenerated Successfully");
						Log.i(TAG, "::onCompletedSuccess: Path "+songToFingerprint.mpath);
						if(rowId!= null && util.isConnectingToInternet()){
							//modify the song length and ingest to the FP Server Initally
							//new FingerPrintServer().Ingest(fingerPrint,songToFingerprint.mSongName,RemoteService.this.getApplication());
							//new FingerPrintServer().Query(fingerPrint,RemoteService.this.getApplication());
							statusmap.put(NO_OF_DATABASE_OPERATION_DONE, ++NoOfDatabaseOperationDone+"");
							 ServerSyncAdapter sync = new  ServerSyncAdapter(getApplication());
							sync.startSync(FingerprintDao.TABLENAME,rowId);
							statusmap.put(NO_OF_SONG_PROCESSED, ++NoOfSongProcessed+"");
						}
						 
						fpHandler.postDelayed(fpTask, 1000L);

						
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
		fp.setUploadeddate(now);
		fp.setLastuploadeddate(now);
		fp.setAndroidmusicid((Long)FingerprintedSong.mSongId);
		FingerprintDao fpdao=daoSession.getFingerprintDao();
 
		Long rowId=fpdao.insert(fp);
		if(rowId!=-1){
			return rowId;
		}
		 
		Log.i(TAG, "Inserted for "+fp);
		return null;
	}

	 
	private List<Song> listToFingerprint;
	private String currentFingerPrintingSong;
 
	private List<Song> getListOfSongsToFingerPrint(List<Song> allsonglist,
			Map<Long,String> listofSongPathInLocalDb) {
		List<Song> result = new ArrayList<Song>();
		for (Song song : allsonglist) {
			
			if (!(listofSongPathInLocalDb.containsValue(song.mpath) && listofSongPathInLocalDb.containsKey(song.mSongId))) {
				result.add(song);
			}
		}
		return result;
	}
	 
	private List<Long> getListOfSongsToDelete(List<Song> allsonglist,
			Map<Long,String> listofSongPathInLocalDb) {
		Set<Long> songlistinAndroid = new HashSet<Long>();
		for (Song song : allsonglist) {
			songlistinAndroid.add(song.mSongId);
		}
		List<Long> resultSongid = new ArrayList<Long>();
		Map<Long,String> result = new HashMap<Long,String>();
		for (Long songid : listofSongPathInLocalDb.keySet()) {
			
			if (!(songlistinAndroid.contains(songid))) {
				resultSongid.add(songid);
			}
		}
		return resultSongid;
	}
	
	
}
