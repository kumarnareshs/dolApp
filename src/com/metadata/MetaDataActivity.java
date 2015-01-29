package com.metadata;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.database.Constants;
import com.fingerprint.upload.R;

public class MetaDataActivity extends Activity implements Constants,OnClickListener {

	 
	TextView tv;
	 protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.finger);
			Button b1 = (Button) findViewById(R.id.click);
			b1.setOnClickListener(this);
			Button b2 = (Button) findViewById(R.id.click1);
			b2.setOnClickListener(this);
			Button b3 = (Button) findViewById(R.id.click2);
			b3.setOnClickListener(this);
			tv = (TextView)findViewById(R.id.gettext);
		}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.click:
			getMusicInfo();
			break;
		case R.id.click1:
			try {
				SetMusicInfo();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.click2:
			
			break;
		
		}
	}
	private void SetMusicInfo() throws JSONException {
		// TODO Auto-generated method stub
		   File src =new File(Environment.getExternalStorageDirectory()+"/dol/naan.mp3");
		   MusicMetadataSet src_set = null;
           try {
               src_set = new MyID3().read(src);
           } catch (IOException e1) {
               e1.printStackTrace();
           } // read metadata

	        MusicMetadata meta = new MusicMetadata("name");
	        meta.setAlbum("asf");
	        meta.setArtist("asd");
	        
	        JSONObject appObject = new JSONObject();
	        appObject.put(FINGERPRINT_STATUS, "fingerprint status");
	        JSONObject appobject = new JSONObject();
	        appobject.put(APP_NAME, appObject);
	        appobject.toString();
	        
	        meta.setUserText(appobject.toString());
	        try {
	            new MyID3().update(src,  src_set, meta);
	        } catch (UnsupportedEncodingException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (ID3WriteException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } 
	}
	private void getMusicInfo() {
		File src = new File(Environment.getExternalStorageDirectory()+"/dol/naan.mp3");
        MusicMetadataSet src_set = null;
        try {
            src_set = new MyID3().read(src);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } // read metadata

        if (src_set == null) // perhaps no metadata
        {
            Log.i("NULL", "NULL");
        }
        else
        {
        try{
        IMusicMetadata metadata = src_set.getSimplified();
        String artist = metadata.getArtist();  
        String album = metadata.getAlbum();  
        String usertext = metadata.getUserText(); 
          
        JSONObject jsonObj = new JSONObject(usertext);
        
        // Getting JSON Array node
       JSONObject contacts = jsonObj.getJSONObject(APP_NAME);
       String fingerprintstatus=contacts.getString(FINGERPRINT_STATUS);
        String song_title = metadata.getSongTitle(); 
        Number track_number = metadata.getTrackNumber(); 
        tv.setText("Artist"+artist+ " "+fingerprintstatus);
        Log.i("artist", artist);
        Log.i("album", album);
        }catch (Exception e) {
            e.printStackTrace();
        }
	}
        }
}
