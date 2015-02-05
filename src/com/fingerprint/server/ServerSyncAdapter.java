package com.fingerprint.server;

import java.util.List;

import android.app.Application;
import android.webkit.WebIconDatabase.IconListener;

import com.database.Constants;
import com.fileupload.MyApplication;
import com.fingerprint.database.DBAdapter;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;

import de.greenrobot.daoexample.FingerPrintRepository;

public class ServerSyncAdapter  implements Constants,IFingerPrintSync,ICommomSync{

	private Application app;
	private RestAdapter restAdapter;
	private IFingerPrintSync IFPSync;
	private ICommomSync IComSync;
	
	public void sendToServer(List<Long> ids, String tablename) {
		IComSync.sendToServer(ids, tablename);
	}
	
	public void sendAllToServer(String tablename) {
		IComSync.sendAllToServer(tablename);
	}
	
	public void sendToServer(Long id, String tablename) {
		IComSync.sendToServer(id, tablename);
	}
	
	 

	public void sendFullFingerPrint(Long rowId, String fullfingerprint,
			Double length) {
		IFPSync.sendFullFingerPrint(rowId, fullfingerprint, length);
	}
	
	public ServerSyncAdapter(Application app) {
		this.app=app;
		DBAdapter dbadapter=DBAdapter.getInstance(app);
		restAdapter = ((MyApplication) app).getLoopBackAdapter();
		IFPSync = new FingerPrintSync(restAdapter.createRepository(FingerPrintRepository.class));
		IComSync = new CommonSync(restAdapter,dbadapter);
	}
 
}
