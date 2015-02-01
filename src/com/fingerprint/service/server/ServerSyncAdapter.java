package com.fingerprint.service.server;

import java.util.List;

import android.R.integer;
import android.app.Application;

public class ServerSyncAdapter extends  ServerSync{

	private Application app;
	public ServerSyncAdapter(Application app) {
		super(app);
		this.app=app;
		syncAllTables();
	}
	private void syncAllTables() {
		
	}
	public ServerSyncAdapter(String tablename,List<integer> ids){
		super(tablename,ids);
		if(ids.size()==0){
			tableSync(tablename);
		}else{
			tableSyncWithIds(tablename,  ids);
		}
	}
	 
	 
	public void startSync(String tablename) {
		 
		tableSync(tablename);
	}
	public void startSync(String tablename, Long rowId) {
		// TODO Auto-generated method stub
		tableSync(tablename,  rowId);
	}
	
	
	
	/*Testing Finger*/
	public String sendToFPServer(String fingerPrint) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public void sendFullFingerPrint(Long rowId, String fingerPrint,
			Double length) {
		 FingerPrintUpload fpu = new FingerPrintUpload(app);
		 fpu.sendFullFingerPrint(rowId, fingerPrint, length);
	}
}
