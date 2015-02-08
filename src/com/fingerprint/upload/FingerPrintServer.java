package com.fingerprint.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.database.Constants;
import com.fileupload.Customer;
import com.fileupload.CustomerRepository;
import com.fileupload.MyApplication;
import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.VoidCallback;

public class FingerPrintServer implements Constants{

	String URL =HOST+":"+"37760";
	Context _context;
	Application app;
	@SuppressWarnings("unused")
	private static final String TAG = "com.fingerprint.upload.FingerPrintServer";
	public void Query(String code,Application app){
		URL+="/query?&version=4.12&code="+code;
		new HttpGetDemo().execute(URL);
		this.app=app;
	}
	public void Ingest(String code,String songname,Application app){
		URL+="/ingest";
		new HttpPostDemo().execute(URL,code,songname);
		this.app=app;
		
	}
	public class HttpGetDemo extends AsyncTask<String, Void, String> {
		String url;
		String result = "fail";
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			this.url = params[0];
			return GetSomething(params);
		}
		
		final String GetSomething(String[] params)
		{
			String url = params[0];
			BufferedReader inStream = null;
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpRequest = new HttpGet(url);
				HttpResponse response = httpClient.execute(httpRequest);
				Util.setDebuger();
				inStream = new BufferedReader(
					new InputStreamReader(
						response.getEntity().getContent()));

				StringBuffer buffer = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = inStream.readLine()) != null) {
					buffer.append(line + NL);
				}
				inStream.close();

				result = buffer.toString();			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Log.i("FingerPrint Server Query Results :",    result );
			return result;
		}
		
		protected void onPostExecute(String page)
		{    	
	    	   	
		}	
	}
	
	
	public class HttpPostDemo extends AsyncTask<String, Void, String> 
	{
		String url;
		
		@Override
		protected String doInBackground(String... params) 	
		{
			this.url = params[0];
			String code=params[1];
			BufferedReader inBuffer = null;
			String url = params[0];
			String result = "fail";
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				
				
			    String contentType =
                		"application/x-www-form-urlencoded;";
			   Header[] headers = {
	                    new BasicHeader("Accept", "application/json"),
	            };
			   
			    String charset = "utf-8";
			    List<NameValuePair> nameValuePairs =
                		new ArrayList<NameValuePair>();
			    
                    nameValuePairs.add(new BasicNameValuePair("code",code));
                    nameValuePairs.add(new BasicNameValuePair("version","4.12"));
                    nameValuePairs.add(new BasicNameValuePair("length","20"));
                    nameValuePairs.add(new BasicNameValuePair("track",params[2]));
                    nameValuePairs.add(new BasicNameValuePair("artist","test"));
                
			   StringEntity body = new UrlEncodedFormEntity(nameValuePairs,
                		charset);
			   
			   request.setHeaders(headers);
			    request.setEntity(body);
			    HttpResponse httpResponse = httpClient.execute(request );
				inBuffer = new BufferedReader(
					new InputStreamReader(
						httpResponse.getEntity().getContent()));

				StringBuffer stringBuffer = new StringBuffer("");
				String line = "";
				String newLine = System.getProperty("line.separator");
				while ((line = inBuffer.readLine()) != null) {
					stringBuffer.append(line + newLine);
				}
				inBuffer.close();

				result = stringBuffer.toString();
				Log.i("FingerPrint Server Ingest Results :", result);
			} catch(Exception e) {
				// Do something about exceptions
				result = e.getMessage();
			} finally {
				if (inBuffer != null) {
					try {
						inBuffer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}
		
		protected void onPostExecute(String page)
		{    	
		}	
	}
	private void loginMethod() {
		MyApplication myapp = (MyApplication) app;
		RestAdapter restAdapter = myapp.getLoopBackAdapter();
		CustomerRepository  customerRepo = restAdapter.createRepository(CustomerRepository.class);
		Customer cus = customerRepo.getCachedCurrentUser();
		if (cus != null) {
			Log.i(TAG,
					"::loginMethod:" + "All ready Logged in with user"
							+ cus.getEmail());
			customerRepo.logout(new VoidCallback() {

				@Override
				public void onSuccess() {

					Log.i(TAG, "::onSuccess:" + "LogOUt successfull");
				}

				@Override
				public void onError(Throwable t) {

					Log.e(TAG, "::onError:" + "Logout Error" + t.getMessage());
				}
			});
		} else {
			Log.i(TAG, "::loginMethod:" + "No User Logged in");
		}

		customerRepo.loginUser("test@nareshkumar.me", "123",
				new CustomerRepository.LoginCallback() {
					@Override
					public void onSuccess(AccessToken token, Customer customer) {
						showToast("Successfull");
					}

					@Override
					public void onError(Throwable t) {
						showToast("failure" + t.getMessage());
					}
				});
	}
	
	public void showToast(String msg) {
		Toast.makeText(_context, msg, Toast.LENGTH_LONG).show();
		Log.i(TAG, "ToastMsg : " + msg);
	}
}
