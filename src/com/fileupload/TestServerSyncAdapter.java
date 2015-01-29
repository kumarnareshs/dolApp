package com.fileupload;

import java.util.List;

import android.R.integer;
import android.app.Application;

public class TestServerSyncAdapter extends TestServerSync{

	public TestServerSyncAdapter(Application app) {
		super(app);
		syncAllTables();
	}
	private void syncAllTables() {
		
	}
	public TestServerSyncAdapter(String tablename,List<integer> ids){
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
	
}
