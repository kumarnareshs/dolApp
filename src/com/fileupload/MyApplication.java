package com.fileupload;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings.Secure;

import com.database.Constants;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.remoting.adapters.RestContractItem;

import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;

public class MyApplication extends Application implements Constants {
    
	RestAdapter adapter;
	private static Context mContext;
	private DaoMaster daoMaster;
	private DaoSession daoSession;

	@SuppressWarnings("unused")
	private static final String TAG = "com.fileupload.MyApplication";

    public  RestAdapter getLoopBackAdapter() {
        if (adapter == null) {
            // Instantiate the shared RestAdapter. In most circumstances,
            // you'll do this only once; putting that reference in a singleton
            // is recommended for the sake of simplicity.
            // However, some applications will need to talk to more than one
            // server - create as many Adapters as you need.
            adapter = new RestAdapter(getApplicationContext(), HOST+":"+PORT+"/api");
            //adapter = new RestAdapter(getApplicationContext(), "http://192.168.43.70:3000/api");
        	
        	
            //adapter = new RestAdapter(getApplicationContext(), "http://192.168.42.121:3000/api",db);
            //adapter = new RestAdapter(getApplicationContext(), "http://10.0.0.26:3000/api");
          
            // This boilerplate is required for Lesson Three.
            adapter.getContract().addItem(
                    new RestContractItem("locations/nearby", "GET"),
                    "location.nearby");
        }
        return adapter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        SharedPreferences  sharedpreferences = getSharedPreferences(MyPREFERENCES, getApplicationContext().MODE_PRIVATE);
        String android_id = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);
		sharedpreferences.edit().putString(ANDROIDID, android_id).apply();
		SQLiteDatabase db = SDcardOpenHelper.open();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		File temp = new File(TEMP_DIR,".nomedia");
		if(!temp.exists()){
			try {
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static Context getContext() {
		return mContext;
	}

	public DaoSession getGlobalDaoSession() {
		if(daoSession == null){
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}
	
	public DaoSession getNewDaoSession() {
		DaoSession dao = daoMaster.newSession();
		return dao;
	}

}
