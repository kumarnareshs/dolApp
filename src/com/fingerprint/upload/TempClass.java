package com.fingerprint.upload;

import android.util.Log;

import com.fileupload.Customer;
import com.fileupload.CustomerRepository;
import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.VoidCallback;

public class TempClass {


	public static void loginMethod(RestAdapter restAdapter) {
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
	
					}

					@Override
					public void onError(Throwable t) {
	
					}
				});
	}
	private final static String TAG = "TempClass";
}
