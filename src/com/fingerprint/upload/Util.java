package com.fingerprint.upload;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.database.Constants;
import com.fileupload.MyApplication;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;

import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;

public class Util implements Constants {

	private Context _context;

	public Util(Context context) {
		this._context = context;
	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	

	public static List<Long> ConvertStringToList(String cids) {
		String[] cidarray = cids.split(",");
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < cidarray.length; i++) {
			result.add(Long.valueOf(cidarray[i]));
		}
		return result;
	}

	public static String getAndroidId() {
		SharedPreferences prefs = MyApplication.getContext()
				.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		// use a default value using new Date()
		// Handle the default value- fetch userId--TODO
		String id = prefs.getString(ANDROIDID, "");
		return id;
	}

	public static void setDebuger(){
		android.os.Debug.waitForDebugger();
	}
	
}