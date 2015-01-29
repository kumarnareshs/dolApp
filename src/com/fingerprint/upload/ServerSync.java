package com.fingerprint.upload;

import java.util.List;

import android.R.integer;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fileupload.MyApplication;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.VirtualObject;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.FingerprintDao.Properties;
import de.greenrobot.daoexample.SDcardOpenHelper;

public class ServerSync {
	
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private FingerprintDao fingerPrintDao;
	private MyApplication app;
	private RestAdapter restAdapter;
	@SuppressWarnings("unused")
	private static final String TAG = "com.fingerprint.upload.ServerSync";
	public  ServerSync(Application application) {
		SDcardOpenHelper sdhelper = new SDcardOpenHelper();
		db = sdhelper.open();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		app = (MyApplication)application;
		restAdapter = app.getLoopBackAdapter();
	}

	public  ServerSync(String tablename, List<integer> ids) {
		// TODO Auto-generated constructor stub
	}

	public void tableSyncWithIds(String tablename, List<integer> ids) {
		if(fingerPrintDao==null){
		fingerPrintDao = daoSession.getFingerprintDao();
		}
		QueryBuilder qb = fingerPrintDao.queryBuilder();
		qb.where(Properties.Id.in(ids));
		List fingerprintlist = qb.list();
		sendToServer(fingerprintlist,fingerPrintDao.getTablename());
	}
	
	public void tableSync (String tablename ) {
		if(fingerPrintDao==null){
			fingerPrintDao = daoSession.getFingerprintDao();
			}
			QueryBuilder qb = fingerPrintDao.queryBuilder();
			qb.where(Properties.Isuploaded.eq("false"));
			List fp =fingerPrintDao.loadAll();
			sendToServer(fp,tablename);
	}
	protected void tableSync(String tablename, Long rowId) {
		// TODO Auto-generated method stub
		if(fingerPrintDao==null){
			fingerPrintDao = daoSession.getFingerprintDao();
			}
			QueryBuilder qb = fingerPrintDao.queryBuilder();
			qb.where(Properties.Id.eq(rowId));
			
			List fp =qb.list();
			sendToServer(fp,tablename);
	}
	public void sendToServer(List rows,String tablename){
		
		//ModelRepository<T extends Model> extends RestRepository<T>
		ModelRepository fingerPrintRepository = getRepository(tablename);
		fingerPrintRepository.updateOrInsertList(rows,new ListCallback<VirtualObject>() {

			@Override
			public void onSuccess(List objects) {
				/*FingerprintDao fpd=daoSession.getFingerprintDao();
				fpd.updateInTx(objects);*/
				List<Fingerprint> finalobjects= Util.ConvertListObjects(objects);
				for(Fingerprint fingerprint:finalobjects){
					
					new DB(db).setUploadedStatus(fingerprint.getId());
				}
				
			}

			@Override
			public void onError(Throwable t) {
				  android.os.Debug.waitForDebugger();
				Log.e(TAG, "::onError:" + t);
				
			}
		});
		
	}

	private ModelRepository getRepository(String tablename) {
		ModelRepository mr = null;
		if (tablename.equalsIgnoreCase(FingerprintDao.TABLENAME)) {
			mr = restAdapter.createRepository(FingerPrintRepository.class);
		} 
		return mr;
	}
}
