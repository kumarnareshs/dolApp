package com.convert.mp3;

import java.io.File;

import org.ffmpeg.android.Clip;
import org.ffmpeg.android.FfmpegController;
import org.ffmpeg.android.ShellUtils.ShellCallback;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.database.Constants;
import com.fingerprint.upload.R;

public class MainActivity extends Activity implements OnClickListener ,Constants{

	 
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
			/*tv.setText("");
			generateFingerPrint();*/
			testFFPGenerator(getApplicationContext());
			break;
		
		}
	}
	private void generateFingerPrint() {
		// TODO Auto-generated method stub
		File filename = new File(Environment.getExternalStorageDirectory()+"/dol/naan.mp3");
		tv.setText(filename.getName());
		
		FingerPrintGenerator fpg = new FingerPrintGenerator(getApplicationContext(),filename,false,listener);
		fpg.generate();
	}
	
	FingerPrintGenerator.FingerPrintListener listener = new FingerPrintGenerator.FingerPrintListener() {
		
		@Override
		public void onCompletedSuccess(String fingerPrint) {
			//new FingerPrintServer().Query(fingerPrint);
			// TODO Auto-generated method stub
			tv.setText("Success"+fingerPrint);
			System.out.println(fingerPrint);
		//	new FingerPrintServer().Ingest(fingerPrint);
		}
		
		@Override
		public void onCompletedFailure(String ExceptionMsg) {
			// TODO Auto-generated method stub
			tv.setText("Failed"+ExceptionMsg);
		}

		@Override
		public void onCompletedSuccess(String fingerPrint, Double length) {
			// TODO Auto-generated method stub
			
		}
	};
	public void showToast(String msg) {
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
		Log.i(TAG, "ToastMsg : " + msg);
	}
	private  final String TAG = getClass().getName();
	private static final int noOfChannels = 1;
	private static final int Wavfrequency = 11025;
	
	private static final int duration = 20;
	private static final String startTime = "00:00:10";
	
	void testFFPGenerator(Context context){
		try {
			Log.i(TAG, "FP Started");
			File fileToConvert = new File(Environment.getExternalStorageDirectory()+ "/dol/naan.mp3");
			Clip c = new Clip(fileToConvert.getCanonicalPath());
			
			FfmpegController ffc = new FfmpegController(context, TEMP_DIR);
				c.duration=duration;
			c.startTime=startTime;
			Clip cli=ffc.convertToWaveAudio(c, new File(TEMP_DIR,Temp_WavFile).getCanonicalPath(), Wavfrequency, noOfChannels,sc);	
			
			} catch (Exception e) {
				
			}
	}
	ShellCallback sc = new ShellCallback() {

		@Override
		public void shellOut(String shellLine) {
			// TODO Auto-generated method stub
			System.out.println(shellLine);
		}

		@Override
		public void processComplete(int exitValue) {
			// TODO Auto-generated method stub
			showToast("Exit value "+exitValue);
			
		}
	};
	private TextView tv;
}
