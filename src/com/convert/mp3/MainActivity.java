package com.convert.mp3;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fingerprint.upload.FingerPrintServer;
import com.fingerprint.upload.R;

public class MainActivity extends Activity implements OnClickListener {

	 
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
			tv.setText("");
			generateFingerPrint();
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
	};
	private TextView tv;
}
