package com.convert.mp3;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import org.ffmpeg.android.Clip;
import org.ffmpeg.android.FfmpegController;
import org.ffmpeg.android.ShellUtils.ShellCallback;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.convert.mp3.FingerPrintGenerator.FingerPrintListener;
import com.database.Constants;
import com.fingerprint.upload.SoundUtility;

import edu.gvsu.masl.echoprint.Codegen;

public class FingerPrintGenerator implements Constants {

	private static final int noOfChannels = 1;
	private static final int Wavfrequency = 11025;
	private static final String Temp_WavFile = "temp.wav";
	private static final int duration = 20;
	private static final String startTime = "00:00:10";
	private String filename = null;
	private File fileToConvert = null;
	private Context context = null;
	private FingerPrintListener fpl = null;
	private String TAG = getClass().getName();
	private boolean isFullLength = false;
	public FingerPrintGenerator(Context context,File filename,Boolean isFullLength,FingerPrintListener fpl){
		 this.context=context;
		 this.fileToConvert=filename;
		 this.fpl= fpl;
		 this.isFullLength=isFullLength;
	 }


	ShellCallback sc = new ShellCallback() {

		@Override
		public void shellOut(String shellLine) {
			// TODO Auto-generated method stub
			//System.out.println(shellLine);
		}

		@Override
		public void processComplete(int exitValue) {
			// TODO Auto-generated method stub

			float[] wavbyte = null;
			try {
				File tempfile = new File(TEMP_DIR, Temp_WavFile);
				wavbyte = getFloatFromWav(tempfile.getCanonicalPath());
				String fingerprint = generateFingerPrint(wavbyte, filename);
				if(tempfile.exists()){
					tempfile.delete();
				}
				if (isFullLength == true) {
					fpl.onCompletedSuccess(fingerprint, songlength);
				} else {
					fpl.onCompletedSuccess(fingerprint);
				}
			} catch (Exception e) {
				fpl.onCompletedFailure(e.getMessage());
				e.printStackTrace();
			}
		}
	};
	private Double songlength;

	public interface FingerPrintListener {
		public void onCompletedSuccess(String fingerPrint);
		public void onCompletedSuccess(String fingerPrint,Double length);
		public void onCompletedFailure(String ExceptionMsg);
	}

	public void generate() {
		try {
		Log.i(TAG, "FP Started");
		Clip c = new Clip(fileToConvert.getCanonicalPath());
		
		FfmpegController ffc = new FfmpegController(context, TEMP_DIR);
		if(isFullLength== true){
			c.duration=SoundUtility.getDurationOfSound(context, fileToConvert.getCanonicalPath());
			
		}else{
			c.duration=duration;
		}
		songlength = c.duration;
		c.startTime=startTime;
		ffc.convertToWaveAudio(c, new File(TEMP_DIR,Temp_WavFile).getCanonicalPath(), Wavfrequency, noOfChannels,sc);	
		} catch (Exception e) {
			fpl.onCompletedFailure(e.getMessage());
		}
	}
	
	float[] getFloatFromWav(String file) throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

		int read;
		byte[] buff = new byte[1024];
		while ((read = in.read(buff)) > 0)
		{
		     out.write(buff, 0, read);
		}
		out.flush();
		byte[] audioBytes = out.toByteArray();
		
		ShortBuffer sbuf =
		ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		short[] audioShorts = new short[sbuf.capacity()];
		sbuf.get(audioShorts);

		float[] audioFloats = new float[audioShorts.length];
		for (int i = 0; i < audioShorts.length; i++) {
		    audioFloats[i] = ((float)audioShorts[i])/0x8000;
		}
		return audioFloats;
	}

	private String generateFingerPrint(float[] wavbyte, String filename)
			throws Exception {
		Codegen c = new Codegen();
		System.out.println(wavbyte.length);
		String fingerprint = c.generate(wavbyte, wavbyte.length);
		return fingerprint;
	}

}
