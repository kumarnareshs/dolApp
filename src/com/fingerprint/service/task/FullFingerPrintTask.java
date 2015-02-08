package com.fingerprint.service.task;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.convert.mp3.FingerPrintGenerator;
import com.database.Constants;
import com.fingerprint.database.DBAdapter;
import com.fingerprint.server.ServerSyncAdapter;
import com.fingerprint.upload.Song;
import com.fingerprint.upload.Util;

import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.FingerprintDao.Properties;
import de.greenrobot.daoexample.Metadata;



public class FullFingerPrintTask implements Constants{


 
	private Util util;
	private  final String TAG = getClass().getName();
	private Application app;
	private Context _context;
	private IInitialFingerPrintTaskListener fplistener;
	private DBAdapter dbadapter;
	private DaoSession daoSession;
	
	public FullFingerPrintTask(Application app,Context context,DBAdapter dbadapter,IInitialFingerPrintTaskListener fplistener){
		this._context=context;
		this.app=app;
		this.fplistener=fplistener;
		this.dbadapter=dbadapter;
		daoSession = dbadapter.getGlobalDaoSession();
		util = new Util(app.getApplicationContext());
	}
	
	private Metadata updateFingerPrintedSongInDB(String songpath,String fingerprint, Long RowId,Double length) throws JSONException {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		Fingerprint fp = new Fingerprint();
		fp.setId(RowId);
		fp.setFilelength(length.toString());
		fp.setFulllengthfingerprint(fingerprint);
		fp.setIsfulllengthfingerprintgenerated(true);
		fp.setIsfulllengthfingerprintuploaded(false);
		fp.setLastmodifieddate(now);
		fp.setStatus(FP_STATUS_FULLFPGENERATED);
		FingerprintDao fpdao=dbadapter.getGlobalDaoSession().getFingerprintDao();
		List<String> fields = new ArrayList<String>();
		fields.add(Properties.Fulllengthfingerprint.columnName);
		fields.add(Properties.Lastmodifieddate.columnName);
		fields.add(Properties.Tempmetadatarowid.columnName);
		Metadata md = getMusicInfo(songpath);
		Long id =daoSession.getMetadataDao().insert(md);
		fp.setTempmetadatarowid(Integer.parseInt(id.toString()));
		fpdao.updateColumn(fp, fields);
		fpdao.refresh(fp);
		return md;
	}
	
	private Metadata getMusicInfo(String songPath) throws JSONException {
		   File src =new File(songPath);
		   MusicMetadataSet src_set = null;
           try {
               src_set = new MyID3().read(src);
           } catch (IOException e1) {
               e1.printStackTrace();
           } // read metadata
           IMusicMetadata metadata = src_set.getSimplified();
           Metadata md = new Metadata();
           md.setAlbum(metadata.getAlbum());
           md.setArtist(metadata.getArtist());
           md.setComment(metadata.getComment());
           md.setCompilation(metadata.getCompilation());
           md.setComposer(metadata.getComposer());
           md.setComposer_2(metadata.getComposer2());
           md.setDuration_seconds(metadata.getDurationSeconds());
           md.setFilename(songPath.substring(songPath.lastIndexOf('/')+1, songPath.length()));
           md.setGenre(metadata.getGenre());
           md.setProducer(metadata.getProducer());
           md.setTitle(metadata.getSongTitle());
           md.setYear(metadata.getYear());
           return md;
         
	}
	public void fingerprintMusic(final String songPath,final Long RowId) {
		
		File filename = new File(songPath);
		Log.i(TAG, "::fingerprintMusic:" + songPath);
		FingerPrintGenerator fpg = new FingerPrintGenerator(_context, filename,true,
				new FingerPrintGenerator.FingerPrintListener() {
	
					 
					@Override
					public void onCompletedSuccess(String fingerPrint) {
						//Util.setDebuger();
						//TODO remove it
					}
	
					@Override
					public void onCompletedFailure(String ExceptionMsg) {
						Util.setDebuger();
						//TODO:: log the request
							Log.e(TAG, "FingerPrint Error" +ExceptionMsg);
							fplistener.onFailure();
					}

					@Override
					public void onCompletedSuccess(String fingerPrint,
							Double length) {
						Metadata md= null;
						try {
							md = updateFingerPrintedSongInDB(songPath,fingerPrint,RowId,length);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if(util.isConnectingToInternet() && md != null){
							ServerSyncAdapter ssa = new ServerSyncAdapter(app);
							ssa.sendFullFingerPrint(RowId, fingerPrint, length,md);
						}
						fplistener.onComplete();	
					}
				});
		fpg.generate();
	
	}	
}
