package com.fingerprint.server;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.fingerprint.upload.Util;
import com.strongloop.android.remoting.adapters.Adapter;

import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.FingerprintDao.Properties;

public class FingerPrintSync implements IFingerPrintSync{

	
	private FingerPrintRepository fingerPrintRepo;
	private  final String TAG = getClass().getName();

	public FingerPrintSync( FingerPrintRepository fingerPrintRepo) {
		this.fingerPrintRepo = fingerPrintRepo;
	}
	
	
	public void sendFullFingerPrint(Long rowId, String fullfingerprint,Double length) {
		 Map<String,   Object> parameters= new  HashMap<String,   Object>();
		 parameters.put(Properties.Cid.columnName, rowId);
		 parameters.put(Properties.Filelength.columnName, fullfingerprint);
		 parameters.put(Properties.Fulllengthfingerprint.columnName, fullfingerprint);
		 parameters.put("androidid", Util.getAndroidId());
		 
		fingerPrintRepo.invokeStaticMethod("ingestfullfingerprint", parameters, new Adapter.Callback() {
			@Override
			public void onSuccess(String response) {
				Log.i(TAG, "ingestfullfingerprint"+response);
				JSONObject jObject;
				try {
					jObject = new JSONObject(response);
					String status = jObject.getString("status");
					if(status=="Success"){
						Log.i(TAG, "ingesting fullfingerprint success");
					}
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
