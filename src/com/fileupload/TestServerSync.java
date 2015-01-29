package com.fileupload;

import java.util.List;

import android.R.integer;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.RestRepository;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.VirtualObject;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.FingerprintDao.Properties;
import de.greenrobot.daoexample.SDcardOpenHelper;

public class TestServerSync<T,D,R extends RestRepository> {
	
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private FingerprintDao fingerPrintDao;
	private MyApplication app;
	private RestAdapter restAdapter;
	
	public TestServerSync(Application application) {
		SDcardOpenHelper sdhelper = new SDcardOpenHelper();
		db = sdhelper.open();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		app = (MyApplication)application;
		restAdapter = app.getLoopBackAdapter();
	}

	

	public TestServerSync(String tablename, List<integer> ids) {
		// TODO Auto-generated constructor stub
	}

	public void tableSyncWithIds(String tablename, List<integer> ids) {
		if(fingerPrintDao==null){
		fingerPrintDao = daoSession.getFingerprintDao();
		}
		QueryBuilder qb = fingerPrintDao.queryBuilder();
		List youngJoes = qb.list();
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
			qb.and(Properties.Isuploaded.eq("false"), Properties.Id.eq(rowId));
			List fp =fingerPrintDao.loadAll();
			sendToServer(fp,tablename);
	}
	public void sendToServer(List rows,String tablename){
		
		//ModelRepository<T extends Model> extends RestRepository<T>
		ModelRepository fingerPrintRepository = getRepository(tablename);
		fingerPrintRepository.updateOrInsertList(rows,new ListCallback<VirtualObject>() {

			@Override
			public void onSuccess(List objects) {
				FingerprintDao fpd=new FingerprintDao(null);
				fpd.updateInTx(objects);
				
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
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
	
	public void TesttableSyncWithIds(String tablename, List<integer> ids,D daoclass) {
		/*if(fingerPrintDao==null){
		fingerPrintDao = daoSession.getFingerprintDao();
		}*/
		QueryBuilder qb = ((AbstractDao<T, Long>) daoclass).queryBuilder();
		List youngJoes = qb.list();
		try {
			TestsendToServer(youngJoes,tablename,  daoclass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void TestsendToServer(List rows,String tablename,final D daoclass) throws ClassNotFoundException{
		
		//ModelRepository<T extends Model> extends RestRepository<T>
		 Class<R > repo = null;
		 Class<R > repo1 = (Class<R>) Class.forName(repo.getName().toString()) ;
		ModelRepository fingerPrintRepository = (ModelRepository) restAdapter.createRepository(repo1);
		fingerPrintRepository.updateOrInsertList(rows,new ListCallback<VirtualObject>() {

			@Override
			public void onSuccess(List objects) {
				 
				((AbstractDao<T,Long>) daoclass).updateInTx(objects);
				
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
}
