package com.fingerprint.upload;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.convert.mp3.FingerPrintGenerator;

public class FPUpload extends Activity implements OnClickListener {

	 
	 protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.generateandupload);
			Button b1 = (Button) findViewById(R.id.audiofiles);
			b1.setOnClickListener(this);
			Button b2 = (Button) findViewById(R.id.fingerprint);
			b2.setOnClickListener(this);

			tv = (TextView)findViewById(R.id.textView1);
		}
	@Override
	public void onClick(View v) {

		SongLoader as = new SongLoader();
		 songlist = as.loadInBackground(getApplicationContext());
		
		switch(v.getId()){
		case R.id.audiofiles:
			listAllSongs(songlist);
			break;
		case R.id.fingerprint:
			tv.setText("");
			generateFingerPrint(songlist);
		   Thread thread = new Thread(new MyRunnable());
		    thread.run(); //in current thread
		break;
		}
	}



	FingerPrintGenerator.FingerPrintListener listener = new FingerPrintGenerator.FingerPrintListener() {

	 

		@Override
		public void onCompletedSuccess(String fingerPrint) {
			// TODO Auto-generated method stub
			tv.setText("Success" + fingerPrint);
			System.out.println(fingerPrint);
		}

		@Override
		public void onCompletedFailure(String ExceptionMsg) {
			// TODO Auto-generated method stub
			tv.setText("Failed" + ExceptionMsg);
		}

		@Override
		public void onCompletedSuccess(String fingerPrint, Double length) {
			// TODO Auto-generated method stub
			
		}
	};
	private TextView tv;
	void listAllSongs(List<Song> songlist){

		ListView resultList = (ListView)findViewById(R.id.audiolist);
		ItemAdapter m_adapter = new ItemAdapter(this, R.layout.list_item, songlist);  
		resultList.setAdapter(m_adapter);
 

	}
	
	


	private class ItemAdapter extends ArrayAdapter<Song>{

	        private  List<Song> items;

	        public ItemAdapter(Context context, int textViewResourceId,
	                List<Song> songlist) {
	            super(context, textViewResourceId, songlist);
	            this.items = songlist;
	        }

	        public View getView(int position, View convertView, ViewGroup parent){
	            View v = convertView;

	            if(v == null){
	                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	                v = vi.inflate(R.layout.list_item, null);
	            }

	            Song item = (Song) items.get(position);

	            if (item != null){
	                TextView resName = (TextView) v.findViewById(R.id.filename);
	                TextView resDistance = (TextView) v.findViewById(R.id.path);  

	                if (resName != null){
	                    resName.setText(item.mSongName);
	                }

	                if (resDistance != null){
	                    resDistance.setText(item.mpath);
	                }           
	            }       
	            return v;           
	        }           
	    }

 


	private List<Song> songlist;
	 private class MyRunnable implements Runnable {


	@Override
	  public void run() {
	   // check if it's run in main thread, or background thread
		  for (Song song : songlist) {
				File filename = new File(song.mpath);
				FingerPrintGenerator fpg = new FingerPrintGenerator(getApplicationContext(), filename,false, listener);
				fpg.generate();
			}
	   if(Looper.getMainLooper().getThread()==Thread.currentThread()){
	    //in main thread
	    tv.setText("in main thread");
	   }else{
	    //in background thread

	    runOnUiThread(new Runnable(){

	     @Override
	     public void run() {
	      tv.setText("in background thread");
	     }
	     
	    });
	   }
	  }
	  
	 }
		private void generateFingerPrint(List<Song> songlist) {
			
		}
}
