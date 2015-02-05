package com.fingerprint.upload;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.database.Constants;



public class RemoteServiceClientDemo extends Activity implements Constants{
    
	private IRemoteService remoteService;
	private boolean started = false;
	private RemoteServiceConnection conn = null;
	private TextView text;
	private TextView database;
	private TextView servicerequest;
	private TextView songsprocessed;
	@SuppressWarnings("unused")
	private static final String TAG = "com.fingerprint.upload.RemoteServiceClientDemo";
	private Timer invokeTimer;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main1);
       
        Button dbreset = (Button)findViewById(R.id.DBreset);
        Button start = (Button)findViewById(R.id.startButton);
        Button stop = (Button)findViewById(R.id.stopButton);
        Button bind = (Button)findViewById(R.id.bindButton);
        Button release = (Button)findViewById(R.id.releaseButton);
        Button invoke = (Button)findViewById(R.id.invokeButton);
        text = (TextView)findViewById(R.id.notApplicable);
        database = (TextView)findViewById(R.id.database);
        servicerequest = (TextView)findViewById(R.id.servicerequest);
        songsprocessed = (TextView)findViewById(R.id.songsprocessed);
        
        start.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		startService();
        	}
        });
        dbreset.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		dbreset();
        	}

			private void dbreset() {
				File f = new File(SDCARD_DIRECTORY+"/"+DATABASE_DIRECTORY,DATABASE_NAME);	
				if(f.exists()){f.delete();showToast("Db deleted");}
			}
        });
        
        
        stop.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		stopService();
        	}
        });       
        
        bind.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		bindService();
        	}
        });  
        
        release.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		releaseService();
        	}
        });          
        
        invoke.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		invokeService();
        	}
        });          
    }
    public void showToast(String msg) {
		Toast.makeText(RemoteServiceClientDemo.this, msg, Toast.LENGTH_LONG)
				.show();
		Log.i(TAG, "ToastMsg : " + msg);
	}
    private void startService(){
    	if (started) {
    		Toast.makeText(RemoteServiceClientDemo.this, 
    				"Service already started", Toast.LENGTH_SHORT).show();
    	} else {
    		 Intent i  = new Intent(getApplicationContext(),
                     RemoteService.class);
    		 i.putExtra(JOB_TYPE, BACKGROUND_FINGERPRINT_JOB);
    		startService(i);
    		started = true;
    		updateServiceStatus();
    		Log.d( getClass().getSimpleName(), "startService()" );
    	}    		
    }
       
    private void stopService() {
    	if (!started) {
       		Toast.makeText(RemoteServiceClientDemo.this, 
       		"Service not yet started", Toast.LENGTH_SHORT).show();
      	} else {
      		Intent i  = new Intent(getApplicationContext(),
                    RemoteService.class);
       		stopService(i);
       	 
       		started = false;
       		updateServiceStatus();
       		Log.d( getClass().getSimpleName(), "stopService()" );
      	}
    }
      
    private void bindService() {
    	if(conn == null) {
        	conn = new RemoteServiceConnection();
        	try{
        		Intent i  = new Intent(getApplicationContext(),
                        RemoteService.class);
        	System.out.println(bindService(i, conn, 0));
        	
        	updateServiceStatus();
        	Log.d( getClass().getSimpleName(), "bindService()" );
        	
        	}catch(Exception e){}
    	} else {
    		Toast.makeText(RemoteServiceClientDemo.this, 
    			"Cannot bind - service already bound", Toast.LENGTH_SHORT).show();
    	}
    }
        
    private void releaseService() {
        if(conn != null) {
        	unbindService(conn);
			conn = null;
			if (invokeTimer != null) {
				invokeTimer.cancel();
				invokeTimer = null;
			}
			updateServiceStatus();
        	Log.d( getClass().getSimpleName(), "releaseService()" );
        } else {
        	Toast.makeText(RemoteServiceClientDemo.this, 
        		"Cannot unbind - service not bound", Toast.LENGTH_SHORT).show();
        }
    }
        
    private void invokeService() {
        if(conn == null) {
        	Toast.makeText(RemoteServiceClientDemo.this, 
        		"Cannot invoke - service not bound", Toast.LENGTH_SHORT).show();
        } else {
        	 
        	invokeTimer = new Timer();
        		//Set the schedule function and rate
			invokeTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					try {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								String currentSong;
								try {

									currentSong = remoteService
											.getCurrentSong();
									Map<String, String> statusmap = remoteService.getStatus();

									statusmap.get("allsonglist");
									statusmap.get("listToFingerprint");
									statusmap.get("listofSongPathInLocalDb");
									statusmap.get("NoOffingerprintOperationDone");	
									
									database.setText("Database: All/Processed"+statusmap.get(LISTOF_SONG_PATH_IN_LOCAL_DB) +"/"+statusmap.get(NO_OF_DATABASE_OPERATION_DONE));
									songsprocessed.setText("SongProcessed Now :"+statusmap.get(NO_OFFINGERPRINT_OPERATION_DONE)+":"+ currentSong);
									servicerequest.setText("Server Request: "+statusmap.get(NO_OF_SONG_PROCESSED));
									} catch ( RemoteException e) {
									Log.i(TAG,"::invokeService:"+ "invoke Remote exception "+ e.getMessage());
								}

							}
						});
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			},
			// Set how long before to start calling the TimerTask (in
			// milliseconds)
					0,
					// Set the amount of time between each execution (in
					// milliseconds)
					1000);

			Log.d(getClass().getSimpleName(), "invokeService()");

		}
	}

    class RemoteServiceConnection implements ServiceConnection {
    	public void onServiceConnected(ComponentName className, 
  			IBinder boundService ) {
            remoteService = IRemoteService.Stub.asInterface((IBinder)boundService);
            
            Log.d( getClass().getSimpleName(), "onServiceConnected()" );
    	}

    	public void onServiceDisconnected(ComponentName className) {
            remoteService = null;
            updateServiceStatus();
            Log.d( getClass().getSimpleName(), "onServiceDisconnected" );
    	}
    };
      
      private void updateServiceStatus() {
    	  String bindStatus = conn == null ? "unbound" : "bound";
    	  String startStatus = started ? "started" : "not started";
    	  String statusText = "Service status: "+
    							bindStatus+ ","+ startStatus;
    	  TextView t = (TextView)findViewById( R.id.serviceStatus);
    	  t.setText( statusText );	  
    	}
      
      protected void onDestroy() {
    	  super.onDestroy();
   	      releaseService();
    	  Log.d( getClass().getSimpleName(), "onDestroy()" );
      } 
}