package com.convert.mp3;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javazoom.jl.converter.Converter;

import org.ffmpeg.android.Clip;
import org.ffmpeg.android.FfmpegController;
import org.ffmpeg.android.ShellUtils.ShellCallback;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pro.R;
import com.google.code.mp3fenge.Mp3Fenge;

import edu.gvsu.masl.echoprint.Codegen;

public class convert extends Activity implements OnClickListener {

	 String DirPath = "naresh";
	 String filename = "naan";
	 String FileExtension = "mp3";
	 String SplitPrefix ="split";
	 File   sdCardRoot = Environment.getExternalStorageDirectory();

	 String TempDir = "split";	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button b1 = (Button) findViewById(R.id.click);
		b1.setOnClickListener(this);
		Button b2 = (Button) findViewById(R.id.click1);
		b2.setOnClickListener(this);
		Button b3 = (Button) findViewById(R.id.click2);
		b3.setOnClickListener(this);
	}

	/*@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Converter myConverter = new Converter();
		try {
			List<String> filelist = new ArrayList<String>();
			File sdCardRoot = Environment.getExternalStorageDirectory();
			File yourDir = new File(sdCardRoot, "naresh");
			for (File f : yourDir.listFiles()) {
				if (f.isFile())
					filelist.add(f.getName());

			}

			for (String filename : filelist) {
				try {

					String filepathinput = Environment
							.getExternalStorageDirectory().toString()
							+ "/naresh/" + filename;
					String filepathoutput = Environment
							.getExternalStorageDirectory().toString()
							+ "/nareshoutput/"
							+ filename.replaceAll(".mp3", "") + ".wav";
					System.out.println("Started for file" + filename);
					System.out.println(new SimpleDateFormat("hh:mm:ss:SSS a")
							.format(new Date()));
					String splitedfile = splitMp3File(filename);
					myConverter.convert(splitedfile, filepathoutput);
					System.out.println("finished");
					float[] wavbyte = null;
					wavbyte = getByteFromWav(filepathoutput);
					System.out.println("wavebyte length " + wavbyte.length);
					generateFingerPrint(wavbyte, filename);
					System.out.println(new SimpleDateFormat("hh:mm:ss:SSS a")
							.format(new Date()));
					System.out.println("Completed for file" + filename);

				} catch (Exception e) {
					e.printStackTrace();
					TextView tv = (TextView) findViewById(R.id.textView1);
					tv.append(e.getMessage());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
	}
*/
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()== R.id.click2){
			try {
				fileinfo();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{if(v.getId()== R.id.click1){
			try {
				Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
				buttiontwoclick();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
		Converter myConverter = new Converter();
		try {
			
			System.out.println("Started for file" + filename);
			System.out.println(new SimpleDateFormat("hh:mm:ss:SSS a")
					.format(new Date()));
			boolean isSuccess= splitMp3File(sdCardRoot.toString()+"/"+DirPath,filename);
			if(isSuccess){
				myConverter.convert(sdCardRoot.toString()+"/"+DirPath,filename);
			}else{
				
			}
			myConverter.convert(sdCardRoot.toString()+"/"+DirPath+"/"+TempDir+"/"+filename,
					sdCardRoot.toString()+"/"+DirPath+"/"+TempDir+"/"+filename.replaceAll("mp3", "wav"));
			System.out.println("finished");
			float[] wavbyte = null;
			wavbyte = getByteFromWav(sdCardRoot.toString()+"/"+DirPath+"/"+TempDir+"/"+filename.replaceAll("mp3", "wav"));
			System.out.println("wavebyte length " + wavbyte.length);
			//generateFingerPrint(wavbyte, filename);
			System.out.println(new SimpleDateFormat("hh:mm:ss:SSS a").format(new Date()));
			System.out.println("Completed for file" + filename);

		} catch (Exception e) {
			e.printStackTrace();
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.append(e.getMessage());
		}
		}}
	}

	public void fileinfo() throws FileNotFoundException, IOException {
		InputStream in = new FileInputStream(new File(sdCardRoot.toString()+"/"+DirPath+"/"+filename+"."+"wav"));
		WaveHeader wh = new WaveHeader();
		wh.read(in);
		wh.setSampleRate(11025);
		wh.setNumChannels((short)1);
/*		OutputStream os = new FileOutputStream(sdCardRoot.toString() + "/" + DirPath + "/" 
				+ filename+"11025.wav");
		wh.write(os);*/
		System.out.println(wh);
		
	}

	public void buttiontwoclick() throws Exception {
		System.out.println("Conversion Start "+new SimpleDateFormat("hh:mm:ss:SSS a").format(new Date()));
		Clip c = new Clip(sdCardRoot.toString()+"/"+DirPath+"/"+filename+"."+FileExtension);
		final FfmpegController ffc = new FfmpegController(getApplicationContext(), new File(sdCardRoot.toString()+"/"+DirPath+"/"+"temp"));
		c.startTime="00:00:10";
		c.duration=20;
		final Clip clips =ffc.convertToWaveAudio(c, sdCardRoot.toString()+"/"+DirPath+"/"+filename+"."+"wav", 11025 	, 1,new ShellCallback() {
			
			@Override
					public void shellOut(String shellLine) {
						// TODO Auto-generated method stub
						System.out.println(shellLine);
					}

					@Override
					public void processComplete(int exitValue) {
						// TODO Auto-generated method stub
						 
							float[] wavbyte = null;
							try {
								wavbyte = getFloatFromWav(sdCardRoot.toString()
										+ "/" + DirPath + "/" + filename + "."
										+ "wav");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("wavebyte length "
									+ wavbyte.length);
							try {
								 generateFingerPrint(wavbyte, filename);
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
					 			e.printStackTrace();
							}  
						}
					 
				});
		
		Clip info = ffc.getInfo(clips);
		
	}
	
	float[] getFloatFromWav(String file) throws IOException{
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

	private void generateFingerPrint(float[] wavbyte, String filename)
			throws IOException {
		Codegen c = new Codegen();
		System.out.println(wavbyte.length);
		String fingerprint = c.generate(wavbyte, wavbyte.length);
		System.out.println("finger print " + fingerprint);
		FileWriter fw = new FileWriter(Environment
				.getExternalStorageDirectory().toString()+"/"
				+ DirPath+ "/"
				+ filename + ".txt");
		fw.append(filename + ":" + fingerprint);
		fw.append('\n');
		fw.flush();
	}

	static float[] getByteFromWav(String file) throws Exception {

		
	/*	byte[] audioBytes;

		ShortBuffer sbuf = ByteBuffer.wrap(audioBytes)
				.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		short[] audioShorts = new short[sbuf.capacity()];
		sbuf.get(audioShorts);
		float[] audioFloats = new float[audioShorts.length];
		for (int i = 0; i < audioShorts.length; i++) {
			audioFloats[i] = ((float) audioShorts[i]) / 0x8000;
		}
*/
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				file));

		int read;
		byte[] buff = new byte[1024];
		for (int i = 0; i <= 2; i++) {
			if ((read = in.read(buff)) > 0) {
				System.out.println("read" + read);
				System.out.println("buff" + buff);
				out.write(buff, 0, read);
			}
		}
		out.flush();
		byte[] audioBytes = out.toByteArray();
		System.out.println("length " + audioBytes.length);
		float[] audioFloats = new float[audioBytes.length];
		for (int i = 0; i < audioBytes.length; i++) {
			audioFloats[i] = ((float) audioBytes[i]) / 0x80;
		}

		return audioFloats;
	}

	public static String splitFile(String filename) throws IOException,
			NoSuchAlgorithmException {
		String outfilename = Environment.getExternalStorageDirectory()
				.toString() + "/nareshoutput/" + filename;
		String infilename = Environment.getExternalStorageDirectory()
				.toString() + "/naresh/" + filename;
		File file = new File(outfilename);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		long filesize = file.length();
		long filesizeActual = 0L;
		int splitval = 5;
		int splitsize = (int) (filesize / splitval)
				+ (int) (filesize % splitval);
		byte[] b = new byte[400000];

		System.out.println(infilename + "            " + filesize + " bytes");
		try {
			fis = new FileInputStream(infilename);
			fos = new FileOutputStream(outfilename);
			int i = fis.read(b);
			fos.write(b, 0, i);
			fos.close();
			fos = null;
			System.out.println(infilename + "    " + i + " bytes");
			filesizeActual += i;
			return outfilename;
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	public  boolean splitMp3File(String filepath,String filename) {
	
		String splitFileName=filepath+"/"+TempDir +"/"+filename.replaceAll(".mp3", "")+"_"+SplitPrefix+".mp3";
		System.out.println("Split Start "+new SimpleDateFormat("hh:mm:ss:SSS a").format(new Date()));
		Mp3Fenge helper = new Mp3Fenge(new File(filepath+"/"+filename));
		System.out.println("Split Ends "+new SimpleDateFormat("hh:mm:ss:SSS a").format(new Date()));
		helper.generateNewMp3ByTime(new File(splitFileName), 20000, 40000);
		File file = new File(splitFileName);
			if (file.isFile()){
				return true;
			}else{
				return false;
				
			}
	}
}
