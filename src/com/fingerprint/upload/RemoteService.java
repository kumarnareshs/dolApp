package com.fingerprint.upload;

import java.util.ArrayList;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.database.Constants;
import com.fileupload.MyApplication;
import com.fingerprint.database.DBAdapter;
import com.fingerprint.service.task.FullFingerPrintTask;
import com.fingerprint.service.task.IInitialFingerPrintTaskListener;
import com.fingerprint.service.task.InitialFingerPrintTask;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.remoting.JsonUtil;
import com.strongloop.android.remoting.adapters.Adapter;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.FingerprintDao.Properties;
import de.greenrobot.daoexample.Metadata;
import de.greenrobot.daoexample.MetadataRepository;

public class RemoteService extends Service implements Constants {
	
	
	private Handler serviceHandler;
	private Handler trackIdHandler;
	private Handler initialfpHandler;
	private Handler generateFullFPHandler;
	private Handler MetaDataHandler;
	private BackgroundFPWorker bgFPWorker = new BackgroundFPWorker();
	private NoMatchFoundWorker nomatchfoundworker = new NoMatchFoundWorker();
	private InitialFingerPrintWorker initialFPTask = new InitialFingerPrintWorker();
	private FullFingerPrintWorker fullfpworker = new FullFingerPrintWorker();
	private static int songCounter=0;
	private static int NoOffingerprintOperationDone=0;
	private static int NoOfDatabaseOperationDone=0;
	private static int NoOfSongProcessed=0;
	private MyApplication app;
	private RestAdapter restAdapter;
	private FingerPrintRepository fingerPrintRepo;
	private static Map<String,String> statusmap;
	private List<Song> listToFingerprint;
	private String currentFingerPrintingSong;
	private DBAdapter dbadapter;
	private FingerprintDao fingerprintdao;
	@SuppressWarnings("unused")
	private static final String TAG = "com.fingerprint.upload.RemoteService";
	
	@Override
	public IBinder onBind(Intent i) {

		Log.d(getClass().getSimpleName(), "onBind()");
		String jobType = i.getStringExtra(JOB_TYPE);
		dbadapter = DBAdapter.getInstance(getApplication());
		statusmap = new HashMap<String, String>();
		app = (MyApplication) this.getApplication();
		restAdapter = app.getLoopBackAdapter();
		fingerprintdao = dbadapter.getNewDaoSession().getFingerprintDao();
		fingerPrintRepo = restAdapter.createRepository(FingerPrintRepository.class);
		metaDataRepo = restAdapter.createRepository(MetadataRepository.class);
		if (jobType == BACKGROUND_FINGERPRINT_JOB) {
			serviceHandler = new Handler();
			serviceHandler.postDelayed(bgFPWorker, 1000L);
		}
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
	private MetadataRepository metaDataRepo;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(getClass().getSimpleName(),"onCreate()");
	}
	@Override
	public void onDestroy() {
		/* TODO: service is not destroying */
		if (serviceHandler != null) {
			serviceHandler.removeCallbacks(bgFPWorker);
		}
		statusmap=null;
		serviceHandler = null;
		initialfpHandler.removeCallbacks(initialFPTask);
		initialfpHandler=null;
		stopSelf();
		Log.d(getClass().getSimpleName(),"onDestroy() service");
		super.onDestroy();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		Log.d(getClass().getSimpleName(), "onStart()"); 
		return START_NOT_STICKY;
	}
	
	class NoMatchFoundWorker implements Runnable{
		
		@Override
		public void run() {
			//android.os.Debug.waitForDebugger();
			
			 fingerPrintRepo.invokeStaticMethod("getnomatchfoundsongs", null, new Adapter.Callback() {
				@Override
				public void onSuccess(String response) {
					Log.i(TAG, "getnomatchfoundsongs"+response);
					JSONObject jObject;
					try {
						jObject = new JSONObject(response);
						String cids = jObject.getString("cids");
                        List<Long> listOfnomatchFoundSongs = Util.ConvertStringToList(cids);
						dbadapter.setFingerprintStatus(listOfnomatchFoundSongs,FP_STATUS_NOMATCHFOUND);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onError(Throwable t) {
				android.os.Debug.waitForDebugger();
					Log.e(TAG, "getnomatchfoundsongs"+t);
				}
			});
		}
	}
	

	class TrackIdWorker implements Runnable{
		@Override
		public void run() {
			//build not included
			QueryBuilder qb = fingerprintdao.queryBuilder();
			qb.where(Properties.Isuploaded.eq(Boolean.TRUE),Properties.Status.eq(FP_STATUS_FPGENERATED));
			List<Fingerprint> modellist = qb.list();
			String requestParam ="[";
			for (Fingerprint fingerprint : modellist) {
				requestParam = fingerprint.getId().toString()+",";
			}
			requestParam = requestParam.substring(0, requestParam.length()-1) +"]";
			 Map<String,String> param = new  HashMap<String,String>();
			 param.put("ids", requestParam);
			 fingerPrintRepo.invokeStaticMethod("getTrackIds", param, new Adapter.Callback() {
				@Override
				public void onSuccess(String response) {
					//[{"rowid":"","trackid":""}]
					Log.i(TAG, "getnomatchfoundsongs"+response);
					JSONArray responceArray;
					try {
						responceArray = new JSONArray(response);
						for (int i = 0; i < responceArray.length(); i++) {
							JSONObject obj = responceArray.getJSONObject(i);
							Long cid=Long.parseLong(obj.get(Properties.Cid.columnName).toString());
							Integer trackid=Integer.parseInt(obj.get(Properties.Trackid.columnName).toString());
							Fingerprint fp = new Fingerprint();
							fp.setId(cid);
							fp.setTrackid(trackid);
							fp.setStatus(FP_STATUS_TRACKID_RECEIVED);
							List<String> fields = new ArrayList<String>();
							fields.add(Properties.Trackid.columnName);
							fingerprintdao.updateColumn(fp, fields);
							fingerprintdao.refresh(fp);
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				@Override
				public void onError(Throwable t) {
				android.os.Debug.waitForDebugger();
					Log.e(TAG, "getnomatchfoundsongs"+t);
				}
			});
		}
	}
	
	class MetaDataWorker implements Runnable{

		@Override
		public void run() {
			// get all the status for
			QueryBuilder qb = fingerprintdao.queryBuilder();
			qb.where(Properties.Status.eq(FP_STATUS_TRACKID_RECEIVED));
			final List<Fingerprint> modellist = qb.list();
			String requestParam ="[";
			for (Fingerprint fingerprint : modellist) {
				requestParam = fingerprint.getId().toString()+",";
			}
			requestParam = requestParam.substring(0, requestParam.length()-1) +"]";
			 Map<String,String> param = new  HashMap<String,String>();
			 param.put("ids", requestParam);
			 
			 fingerPrintRepo.invokeStaticMethod("getmetadata", null, new Adapter.Callback() {
					@Override
					public void onSuccess(String response) {
						JSONArray responcearray = null;
						try {
							responcearray = new JSONArray(response);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						
						List<Metadata> list = new ArrayList<Metadata>();
				        if (response != null) {
				            for (int i = 0; i < response.length(); i++) {
				                list.add(metaDataRepo.createObject(JsonUtil.fromJson(
				                		responcearray.optJSONObject(i))));
				            }
				        }
				        for (Metadata metadata : list) {
				        	dbadapter.getGlobalDaoSession().insert(metadata);
						}
				        
				        for (Fingerprint fingerprint : modellist) {
							
						}
				       
						List<String> fields = new ArrayList<String>();
						fields.add(Properties.Trackid.columnName);
						fingerprintdao.updateColumnInTx(modellist, fields);
						fingerprintdao.refresh(modellist);
						
						Log.i(TAG, "getnomatchfoundsongs"+response);
					}
					
					@Override
					public void onError(Throwable t) {
					android.os.Debug.waitForDebugger();
						Log.e(TAG, "getnomatchfoundsongs"+t);
					}
				});
			
		}
		
	}
	
	class BackgroundFPWorker implements Runnable {

		public void run() {
			SongLoader as = new SongLoader();
			List<Song> allsonglist = as.loadInBackground(getApplicationContext());
			statusmap.put(ALLSONGLIST2, allsonglist.size()+"");
			Log.i(TAG, "::run:" + "all song list "+allsonglist.size());
			Map<Long,String> listofSongPathInLocalDb =dbadapter.getListOfSongsPathInLocalDb();
			statusmap.put(LISTOF_SONG_PATH_IN_LOCAL_DB, listofSongPathInLocalDb.size()+"");
			Log.i(TAG, "::run:" + "list of songs in local Db"+listofSongPathInLocalDb.size());
			listToFingerprint = getListOfSongsToFingerPrint(allsonglist,listofSongPathInLocalDb);
			Log.i(TAG, "::run:" + "list to fingerprint"+listToFingerprint.size());
			statusmap.put(LIST_TO_FINGERPRINT, listToFingerprint.size()+"");
			initialfpHandler= new Handler();
			initialfpHandler.postDelayed(initialFPTask, 1000L);
			if(allsonglist.size()<listofSongPathInLocalDb.size()){
				List<Long> songlisttodelete=getListOfSongsToDelete(allsonglist,listofSongPathInLocalDb);
				if(dbadapter.deleteSongsInLocalDb(songlisttodelete)){
					Log.i(TAG, "Deleted "+songlisttodelete.size()+" Songs in DB");
				};
			}
		} 
	}

	
	class FullFingerPrintWorker implements Runnable{

		@Override
		public void run() {
			if (listToFingerprint.size()>songCounter) {
				songCounter++;
				FullFingerPrintTask fptask = new FullFingerPrintTask(getApplication(), getApplicationContext(),dbadapter,new IInitialFingerPrintTaskListener() {
					
					@Override
					public void onFailure() {
						// TODO Auto-generated method stub
						initialfpHandler.postDelayed(initialFPTask, 1000L);
					}
					
					@Override
					public void onComplete() {
						// TODO Auto-generated method stub
						initialfpHandler.postDelayed(initialFPTask, 1000L);
					}
				});
				fptask.fingerprintMusic(listToFingerprint.get(songCounter));
			}else{
				 
			}
		}
	}

	class InitialFingerPrintWorker  implements Runnable{

		@Override
		public void run() {
			if (listToFingerprint.size()>songCounter) {
				songCounter++;
				InitialFingerPrintTask fptask = new InitialFingerPrintTask(getApplication(), getApplicationContext(),new IInitialFingerPrintTaskListener() {
					
					@Override
					public void onFailure() {
						// TODO Auto-generated method stub
						initialfpHandler.postDelayed(initialFPTask, 1000L);
					}
					
					@Override
					public void onComplete() {
						// TODO Auto-generated method stub
						initialfpHandler.postDelayed(initialFPTask, 1000L);
					}
				});
				fptask.fingerprintMusic(listToFingerprint.get(songCounter));
			}else{
				 
			}
		}
	}
 
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
		for (Long songid : listofSongPathInLocalDb.keySet()) {
			
			if (!(songlistinAndroid.contains(songid))) {
				resultSongid.add(songid);
			}
		}
		return resultSongid;
	}
	
	
}
