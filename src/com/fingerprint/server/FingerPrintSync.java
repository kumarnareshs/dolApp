package com.fingerprint.server;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.database.Constants;
import com.fingerprint.database.DBAdapter;
import com.fingerprint.upload.Util;
import com.strongloop.android.remoting.adapters.Adapter;

import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.Metadata;
import de.greenrobot.daoexample.FingerprintDao.Properties;

public class FingerPrintSync implements IFingerPrintSync,Constants{

	
	private FingerPrintRepository fingerPrintRepo;
	private  final String TAG = getClass().getName();
	private DBAdapter dbadapter;

	public FingerPrintSync( FingerPrintRepository fingerPrintRepo, DBAdapter dbadapter) {
		this.fingerPrintRepo = fingerPrintRepo;
		this.dbadapter = dbadapter;
	}
	
	
	public void sendFullFingerPrint(final Long rowId, String fullfingerprint,Double length,Metadata md) {
		 
		 Map<String,   Object> parameters= new  HashMap<String,   Object>();
		 parameters.put(Properties.Cid.columnName, rowId);
		 parameters.put(Properties.Filelength.columnName, fullfingerprint);
		 parameters.put(Properties.Fulllengthfingerprint.columnName, fullfingerprint);
		 parameters.put("androidid", Util.getAndroidId());
		 parameters.put("metadata", md.toMap());
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
						dbadapter.setFingerprintStatus(rowId, FP_STATUS_FULLFPUPLOADED);
					}
				} catch (JSONException e) {
					e.printStackTrace();
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
