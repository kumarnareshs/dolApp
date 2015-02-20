package com.fingerprint.upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings.System;
import android.util.Log;

import com.database.Constants;
import com.fileupload.MyApplication;
import com.fingerprint.database.DBAdapter;
import com.fingerprint.server.ServerSyncAdapter;
import com.fingerprint.service.task.FullFingerPrintTask;
import com.fingerprint.service.task.IInitialFingerPrintTaskListener;
import com.fingerprint.service.task.InitialFingerPrintTask;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.adapters.Adapter;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.FingerprintDao.Properties;
import de.greenrobot.daoexample.Metadata;
import de.greenrobot.daoexample.MetadataDao;
import de.greenrobot.daoexample.MetadataRepository;

public class RemoteService extends Service implements Constants {
	
	
	private Handler serviceHandler = new Handler();
	private Handler trackIdHandler= new Handler();
	private Handler initialfpHandler= new Handler();
	private Handler nomatchfoundHandler= new Handler();
	private Handler MetaDataHandler= new Handler();
	private Handler fullfpHandler= new Handler();
	private Handler fullfpsubHandler= new Handler();
	private Handler fullfpuploadHandler= new Handler();
	private BackgroundFPWorker bgFPWorker = new BackgroundFPWorker();
	private NoMatchFoundWorker nomatchfoundworker = new NoMatchFoundWorker();
	private InitialFingerPrintWorker initialFPTask = new InitialFingerPrintWorker();
	private FullFingerPrintWorker fullfpworker = new FullFingerPrintWorker();
	private FullFingerPrintSubWorker fullfingersubworker ;
	private FullFingerPrintUploadWorker fullFingerPrintUploadWorker = new FullFingerPrintUploadWorker();
	private MetaDataWorker metadataworker= new MetaDataWorker();
	private TrackIdWorker trackidworker = new TrackIdWorker();
	private static int songCounter=0;
	private MyApplication app;
	private RestAdapter restAdapter;
	private FingerPrintRepository fingerPrintRepo;
	private MetadataRepository metaDataRepo;
	private List<Song> listToFingerprint = new ArrayList<Song> ();
	private Map<Long,String> mapToFullFingerprint= new HashMap<Long,String>();
	private String currentFingerPrintingSong;
	private DBAdapter dbadapter;
	private FingerprintDao fingerprintdao;
	private MetadataDao metadatadao;
	@SuppressWarnings("unused")
	private static final String TAG = "com.fingerprint.upload.RemoteService";
	
	@Override
	public IBinder onBind(Intent i) {
		Util.setDebuger();
		
		Log.d(getClass().getSimpleName(), "onBind()");
		String jobType = i.getStringExtra(JOB_TYPE);
		dbadapter = DBAdapter.getInstance(getApplication());
		app = (MyApplication) this.getApplication();
		restAdapter = app.getLoopBackAdapter();
		TempClass.loginMethod(restAdapter);
		fingerprintdao = dbadapter.getNewDaoSession().getFingerprintDao();
		metadatadao = dbadapter.getNewDaoSession().getMetadataDao();
		fingerPrintRepo = restAdapter.createRepository(FingerPrintRepository.class);
		metaDataRepo = restAdapter.createRepository(MetadataRepository.class);
		if (jobType.equalsIgnoreCase( BACKGROUND_FINGERPRINT_JOB) ){
			serviceHandler = new Handler();
			serversyncadapter = new ServerSyncAdapter(app);
			serviceHandler.postDelayed(bgFPWorker, 1000L);
			serversyncadapter.initialSync(FingerprintDao.TABLENAME);			
		}
		return remoteServiceStub;
	}


	private IRemoteService.Stub remoteServiceStub = new IRemoteService.Stub() {
		public String getCurrentSong() throws RemoteException {
			return currentFingerPrintingSong;
		}

		@Override
		public Map getStatus() throws RemoteException {
			
			return null;
		}
	};
	private ServerSyncAdapter serversyncadapter;
	

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(getClass().getSimpleName(),"onCreate()");
	}
	@Override
	public void onDestroy() {
		 
		if (serviceHandler != null) {
			serviceHandler.removeCallbacks(bgFPWorker);
		}
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
	
	class BackgroundFPWorker implements Runnable {

		public void run() {
			SongLoader as = new SongLoader();
			List<Song> allsonglist = as.loadInBackground(getApplicationContext());
			Log.i(TAG, "::run:" + "all song list "+allsonglist.size());
			Map<Long,String> listofSongPathInLocalDb =dbadapter.getListOfSongsPathInLocalDb();
			Log.i(TAG, "::run:" + "list of songs in local Db"+listofSongPathInLocalDb.size());
			listToFingerprint = getListOfSongsToFingerPrint(allsonglist,listofSongPathInLocalDb);
			Log.i(TAG, "::run:" + "list to fingerprint"+listToFingerprint.size());
			initialfpHandler= new Handler();
			initialfpHandler.postDelayed(initialFPTask, 1000L);
			trackIdHandler.postDelayed(trackidworker, 1000L);
			nomatchfoundHandler.postDelayed(nomatchfoundworker, 1000L);
			fullfpuploadHandler.postDelayed(fullFingerPrintUploadWorker, 1000L);
			fullfpHandler.postDelayed(fullfpworker, 1000L);
			if(allsonglist.size()<listofSongPathInLocalDb.size()){
				List<Long> songlisttodelete=getListOfSongsToDelete(allsonglist,listofSongPathInLocalDb);
				if(dbadapter.deleteSongsInLocalDb(songlisttodelete)){
					Log.i(TAG, "Deleted "+songlisttodelete.size()+" Songs in DB");
				};
			}
		} 
	}
	
	class NoMatchFoundWorker implements Runnable{
		
		@Override
		public void run() {
			Log.i(TAG, "::NomatchFound:" + "Started");
			QueryBuilder qb = fingerprintdao.queryBuilder();
			qb.where(Properties.Trackid.isNull(),Properties.Uploadeddate.isNotNull());
			List<Fingerprint> modellist = qb.list();
			Log.i(TAG, "::NomatchFound: " +modellist.size()+ " rows with no track id");
			if(modellist.size()>0){
				 Map<String,String> param = new  HashMap<String,String>();
				 param.put("androidid", Util.getAndroidId());
			 Util.setDebuger();
			 fingerPrintRepo.invokeStaticMethod("getnomatchfoundsongs", param, new Adapter.Callback() {
				@Override
				public void onSuccess(String response) {
					/*{"cids":"1,2,3,4,5"}*/
					Log.i(TAG, "::getnomatchfoundsongs method success :" + "Responce "+response);
					JSONObject jObject;
					try {
						jObject = new JSONObject(response);
						String cids = jObject.getString("cids");
						if(cids!=""){							
                        List<Long> listOfnomatchFoundSongs = Util.ConvertStringToList(cids);
						dbadapter.setFingerprintStatus(listOfnomatchFoundSongs,FP_STATUS_NOMATCHFOUND);
						fullfpHandler.postDelayed(fullfpworker, 1000L);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onError(Throwable t) {
				Util.setDebuger();
					Log.e(TAG, "NoMatchFoundWorker"+t);
				}
			});
			}
		}
	}
	

	class TrackIdWorker implements Runnable{
		@Override
		public void run() {
			//build not included
			QueryBuilder qb = fingerprintdao.queryBuilder();
			qb.or(Properties.Status.eq(SERVER_STATUS_UPLOADED), Properties.Status.eq(FP_STATUS_FULLFPUPLOADED),
					Properties.Trackid.isNull());
			qb.build();
			List<Fingerprint> modellist = qb.list();
			String requestParam ="[";
			for (Fingerprint fingerprint : modellist) {
				requestParam += fingerprint.getId().toString()+",";
			}
			if(requestParam.length()>1){
			requestParam = requestParam.substring(0, requestParam.length()-1) ;
			}
			requestParam+="]";
			 Map<String,String> param = new  HashMap<String,String>();
			 param.put("ids", requestParam);
			 param.put("androidid", Util.getAndroidId());
			 Log.i(TAG, " TrackIdWorker " + "Get TrackId for"+ requestParam);
			 fingerPrintRepo.invokeStaticMethod("getTrackIds", param, new Adapter.Callback() {

				@Override
				public void onSuccess(String response) {
					Util.setDebuger();
					Log.i(TAG, "::TrackId success" +
							":" + "responce "+response);
					JSONObject jsosobj;
					JSONArray responceArray;
					try {
						jsosobj = new JSONObject(response);
						Object isnullresponce=jsosobj.get("trackids");
						if(!isnullresponce.toString().equalsIgnoreCase("")){
						responceArray = jsosobj.getJSONArray("trackids");
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
						MetaDataHandler.postDelayed(metadataworker, 1000L);
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
				
				@Override
				public void onError(Throwable t) {
				Util.setDebuger();
					Log.e(TAG, "getnomatchfoundsongs"+t);
				}
			});
		}
	}

	class MetaDataWorker implements Runnable {

		@Override
		public void run() {
			// get all the status for
			QueryBuilder qb = fingerprintdao.queryBuilder();
			qb.where(Properties.Status.eq(FP_STATUS_TRACKID_RECEIVED),Properties.Trackid.isNotNull());
			final List < Fingerprint > modellist = qb.list();
			String requestParam = "[";
			for (Fingerprint fingerprint: modellist) {
				requestParam = fingerprint.getId().toString() + ",";
			}
			requestParam = requestParam.substring(0, requestParam.length() - 1) + "]";
			Map < String, String > param = new HashMap < String, String > ();
			param.put("ids", requestParam);
			Log.i(TAG, "::run:" + "MetaData Params "+requestParam);
			String filter = "{\"where\": {\""+Properties.Trackid.columnName+"\": {\"inq\": "+ requestParam+" }}}";
			metaDataRepo.findByFilter(filter, new ListCallback<Metadata>() {

				@Override
				public void onSuccess(List<Metadata> objects) {
					if(objects.size()>0){
						List<Metadata> models=DaoUtil.ConvertRawObjectsToModels(objects);
						dbadapter.getGlobalDaoSession().insert(models);
						for (Fingerprint fingerprint : modellist) {
							fingerprint.setStatus(FP_STATUS_METADATARECEIVED);
						}
					} else {
					}
				}

				@Override
				public void onError(Throwable t) {

				}
			});

		}

	}

	class FullFingerPrintWorker implements Runnable {

		@Override
		public void run() {
			QueryBuilder qb = fingerprintdao.queryBuilder();
			qb.where(Properties.Status.eq(FP_STATUS_NOMATCHFOUND),Properties.Fulllengthfingerprint.isNull());
			final List<Fingerprint> modellist = qb.list();
			for (Fingerprint fingerprint : modellist) {
				mapToFullFingerprint.put(fingerprint.getId(),fingerprint.getFilepath());
			}
			
			fullfingersubworker = new FullFingerPrintSubWorker(mapToFullFingerprint);
			fullfpsubHandler.postDelayed(fullfingersubworker, 1000L);
		}
	}

	class FullFingerPrintSubWorker implements Runnable{

		Map<Long, String> mapToFullFingerprint = new HashMap<Long, String>();
		public FullFingerPrintSubWorker(Map<Long, String> mapToFullFingerprint) {
			this.mapToFullFingerprint= mapToFullFingerprint;
		}

		@Override
		public void run() {
			
			if (mapToFullFingerprint.size()>0) {
				FullFingerPrintTask fptask = new FullFingerPrintTask(getApplication(), getApplicationContext(),dbadapter,
						new IInitialFingerPrintTaskListener() {
					
					@Override
					public void onFailure() {
						fullfpsubHandler.postDelayed(fullfingersubworker, 1000L);
					}
					
					@Override
					public void onComplete() {
						fullfpsubHandler.postDelayed(fullfingersubworker, 1000L);
					}
				});
				
				Util.setDebuger();
				for (Entry<Long, String> entry : mapToFullFingerprint.entrySet()) {
					mapToFullFingerprint.remove(entry.getKey());
					fptask.fingerprintMusic(entry.getValue(),entry.getKey());
					break;
				}
			}else{
				trackIdHandler.postDelayed(trackidworker, 1000L);
			}
		}
		
	}
	
	class FullFingerPrintUploadWorker implements Runnable {

		

		@Override
		public void run() {
			QueryBuilder qb = fingerprintdao.queryBuilder();
			QueryBuilder metadataqb = metadatadao.queryBuilder();
			qb.where(Properties.Status.eq(FP_STATUS_FULLFPGENERATED),Properties.Fulllengthfingerprint.isNotNull());
			final List<Fingerprint> modellist = qb.list();
			Log.i(TAG, "::FullFpUploader is " + "Uploading "+modellist.size()+" full fingerprints");
			for (Fingerprint fingerprint : modellist) {
				de.greenrobot.daoexample.MetadataDao.Properties d = null;
				metadataqb.where(d.Id.eq(fingerprint.getTempmetadatarowid())).limit(1);
				List<Metadata> metadata = metadataqb.list();
				if(Util.isConnectingToInternet() && metadata.size()>0){
					ServerSyncAdapter ssa = new ServerSyncAdapter(app);
					ssa.sendFullFingerPrint(fingerprint.getId(), fingerprint.getFulllengthfingerprint(), Double.valueOf(fingerprint.getFilelength()),metadata.get(0));
				}
			}
		}
	}
	
	class InitialFingerPrintWorker  implements Runnable{

		@Override
		public void run() {
			if (listToFingerprint.size()>songCounter) {
				
				InitialFingerPrintTask fptask = new InitialFingerPrintTask(getApplication(), dbadapter,
						new IInitialFingerPrintTaskListener() {
					
					@Override
					public void onFailure() {
						
						initialfpHandler.postDelayed(initialFPTask, 1000L);
					}
					
					@Override
					public void onComplete() {
						
						initialfpHandler.postDelayed(initialFPTask, 1000L);
					}
				});
				fptask.fingerprintMusic(listToFingerprint.get(songCounter));
				songCounter++;
			}else{
				trackIdHandler.postDelayed(trackidworker, 1000L);
				nomatchfoundHandler.postDelayed(nomatchfoundworker, 1000L);	 
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
