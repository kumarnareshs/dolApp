package com.fileupload;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;

import com.database.Constants;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.remoting.adapters.RestContractItem;

public class MyApplication extends Application implements Constants {
    
	RestAdapter adapter;
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
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        SharedPreferences  sharedpreferences = getSharedPreferences(MyPREFERENCES, getApplicationContext().MODE_PRIVATE);
        String android_id = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);
        sharedpreferences.edit().putString(ANDROIDID, android_id).apply();
    }

   
    public static Context getContext() {
    	return mContext;
    }
    

   

}
