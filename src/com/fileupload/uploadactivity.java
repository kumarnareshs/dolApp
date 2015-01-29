package com.fileupload;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import android.app.Application;
import android.app.Activity;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fingerprint.upload.R;
import com.google.common.collect.ImmutableMap;
import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.JSONCallBack;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.SDcardOpenHelper;
import de.greenrobot.daoexample.FingerprintDao;

public class uploadactivity extends Activity {

	private MyApplication app;
	private RestAdapter restAdapter;
	private CustomerRepository customerRepo;
	private ListView lv;
	private List<String> arrayList;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	public enum selectedlist {
		Login
	};

	@SuppressWarnings("unused")
	private static final String TAG = "com.fileupload.uploadactivity";
	private MyModelRepository mymodelrepository;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);
		app = (MyApplication) uploadactivity.this.getApplication();
		restAdapter = app.getLoopBackAdapter();
		customerRepo = restAdapter.createRepository(CustomerRepository.class);
		mymodelrepository = restAdapter.createRepository(MyModelRepository.class);
		createListView();
		listener();
		SDcardOpenHelper sdhelper = new SDcardOpenHelper();
		db = sdhelper.open();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
	}

	public void showToast(String msg) {
		Toast.makeText(uploadactivity.this, msg, Toast.LENGTH_LONG).show();
		Log.i(TAG, "ToastMsg : " + msg);
	}

	private void listener() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String selected = (arrayList.get(position).toString());
				if (selected.equalsIgnoreCase("Login")) {
					loginMethod();
				} else if (selected.equalsIgnoreCase("CurrentUser")) {
					currentUser();
				} else if (selected.equalsIgnoreCase("Insert Record & Update by id")) {
					InsertServerRecord();
				} else if (selected.equalsIgnoreCase("Update by query")) {
					UpdatebyqueryInServer();
				} else if (selected.equalsIgnoreCase("Update or Insert a list")) {
					UpdateOrInsertList();
				} else if (selected.equalsIgnoreCase("Fetch All")) {
					FetchAllInServer();
				} else if (selected.equalsIgnoreCase("Fetch By ID")) {
					FetchByIDInServer();
				} else if (selected.equalsIgnoreCase("Fetch By Query")) {
					FetchByQueryInServer();
				} else if (selected.equalsIgnoreCase("Delete Record")) {
					DeleteServerRecord();
			} else if (selected.equalsIgnoreCase("Sync Test")) {
				Application  app= uploadactivity.this.getApplication();
				TestServerSyncAdapter sync = new TestServerSyncAdapter(app);
				sync.startSync(FingerprintDao.TABLENAME);
			}
			}
		});
	}

	private void UpdateOrInsertList() {
		 MyModel mymodel= new MyModel();
		mymodel.setDate(new Date());
		mymodel.setGeopoint("123,123");
		mymodel.setIsavailable(true);
		mymodel.setModelname("android");
		mymodel.setLuckynumber(12);
		
		MyModel mymodel2= new MyModel();
		mymodel2.setDate(new Date());
		mymodel2.setGeopoint("111,111");
		mymodel2.setIsavailable(true);
		mymodel2.setModelname("android");
		mymodel2.setLuckynumber(12);
		
		MyModel mymodel3= new MyModel();
		mymodel3.setDate(new Date());
		mymodel3.setGeopoint("100,100");
		mymodel3.setIsavailable(true);
		mymodel3.setModelname("android");
		mymodel3.setLuckynumber(12);
		
		List<MyModel> modellist = new ArrayList<MyModel>();
		modellist.add(mymodel3);
		modellist.add(mymodel2);
		modellist.add(mymodel);
		
		mymodelrepository.updateOrInsertList(modellist,new ListCallback<MyModel>() {
			
		

			@Override
			public void onSuccess(List<MyModel> objects) {
				FingerprintDao fp=daoSession.getFingerprintDao();
				
			}
			
			@Override
			public void onError(Throwable t) {
				
			}
		});
		
		/*fingerprint fp = new fingerprint();
		fp.setId((long) 1);
		fp.setFilename("one");
		fingerprint fp1 = new fingerprint();
		fp1.setId((long) 2);
		fp1.setFilename("two");
		fingerprint fp2 = new fingerprint();
		fp2.setId((long) 3);
		fp2.setFilename("three");
		
		List<fingerprint> fplist = new ArrayList<fingerprint>();
		fplist.add(fp);
		fplist.add(fp1);
		fplist.add(fp2);
		FingerPrintRepository fingerPrintRepository = restAdapter.createRepository(FingerPrintRepository.class);
		fingerPrintRepository.updateOrInsertList(fplist,new ListCallback<fingerprint>() {
			
		

			@Override
			public void onSuccess(List<fingerprint> objects) {
				fingerprintDao fp=daoSession.getFingerprintDao();
			fp.insertOrReplaceInTx(objects);	
			}
			
			@Override
			public void onError(Throwable t) {
				
			}
		});
		*/
	}

	private void createListView() {
		lv = (ListView) findViewById(R.id.audiolist);

		arrayList = new ArrayList<String>();
		arrayList.add("Login");
		arrayList.add("CurrentUser");
		arrayList.add("Server Request");
		arrayList.add("Insert Record & Update by id");
		arrayList.add("Update or Insert a list");
		arrayList.add("Update by query");
		arrayList.add("Fetch All");
		arrayList.add("Fetch By ID");
		arrayList.add("Fetch By Query");
		arrayList.add("Delete Record");
		arrayList.add("Sync Test");
		// This is the array adapter, it takes the context of the activity as a
		// first parameter, the type of list view as a second parameter and your
		// array as a third parameter.
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arrayList);

		lv.setAdapter(arrayAdapter);
	}
	private void DeleteServerRecord() {

		
		mymodelrepository.findAll(new ListCallback<MyModel>() {

			@Override
			public void onSuccess(List<MyModel> objects) {

				showToast("Total models "+objects.size());
				if(objects.size()>0){
					
				objects.get(0).destroy(new VoidCallback() {
					
					@Override
					public void onSuccess() {
						
						showToast("Deleted one record");
					}
					
					@Override
					public void onError(Throwable t) {
						showToast("Deleted Error"+t.getMessage());
						
					}
				});
				}
			}

			@Override
			public void onError(Throwable t) {
				showToast("Error "+t.getMessage());
			}
		});
	}

	private void FetchByQueryInServer() {


		String filter = "{\"where\": {\"name\": {\"like\": \"%and%\"}}}";
		String filter2 = "{\"where\": {\"and\": [{\"isavailable\": true}, {\"list\": [1,2]}]}}";
		mymodelrepository.findByFilter(filter, new ListCallback<MyModel>() {

			@Override
			public void onSuccess(List<MyModel> objects) {
				if(objects.size()!=0){
					
					showToast("Fetched First model Name "+objects.get(0).getModelname());
				}else{
					showToast("No Date found for given query");
				}

			}

			@Override
			public void onError(Throwable t) {
				showToast(t.getMessage());
			}
		});

	}

	private void FetchByIDInServer() {

		mymodelrepository.findById("1",new ObjectCallback<MyModel>() {

			@Override
			public void onSuccess(MyModel objects) {

				showToast("Fetched  "+objects.getModelname());
			}

			@Override
			public void onError(Throwable t) {
				showToast("Error "+t.getMessage());
			}
		});
	}

	private void FetchAllInServer() {


		mymodelrepository.findAll(new ListCallback<MyModel>() {

			@Override
			public void onSuccess(List<MyModel> objects) {
				
				showToast("Total models "+objects.size());
			}

			@Override
			public void onError(Throwable t) {
				showToast("Error "+t.getMessage());
			}
		});
	}

	private void UpdatebyqueryInServer() {

		String filter = "{\"name\": {\"like\": \"%and%\"}}";
		String filter2 = "{\"where\": {\"and\": [{\"isavailable\": true}, {\"list\": [1,2]}]}}";

		final MyModel mymodel= mymodelrepository.createObject(ImmutableMap.of("name",
				"mymodel1"));
		mymodel.setDate(new Date());
		mymodel.setGeopoint("123,123");
		mymodel.setIsavailable(true);
		mymodel.setModelname("android");
		mymodel.setLuckynumber(12);
		List<String> fieldstoupdate = new ArrayList<String>();
		fieldstoupdate.add("geopoint");
		fieldstoupdate.add("isavailable");
		mymodel.Update(fieldstoupdate,filter,new VoidCallback() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void InsertServerRecord() {

		final MyModel mymodel= mymodelrepository.createObject(ImmutableMap.of("name",
				"mymodel1"));
		mymodel.setDate(new Date());
		mymodel.setGeopoint("13,13");
		mymodel.setIsavailable(true);
		mymodel.setModelname("android");
		mymodel.setLuckynumber(12);
		mymodel.insert(new JSONCallBack() {

			@Override
			public void onSuccess(JSONObject responce) {

				showResult("saved");
				mymodel.setLuckynumber(3);
				mymodel.update(new JSONCallBack()  {

					@Override
					public void onSuccess( JSONObject responce) {

						showResult("Updated again");
					}

					@Override
					public void onError(Throwable t) {

						showResult("Updated Error" + t.getMessage());
					}
				});
			}

			@Override
			public void onError(Throwable t) {

				showResult("not saved" + t.getMessage());
			}
		});
	}

	private void currentUser() {
		Customer cus = customerRepo.getCachedCurrentUser();
		if (cus!= null) {
			
			showToast("LoggedIn User Email" + cus.getEmail());
		} else {
			showToast("User Not LoggedIn");

		}
	}

	private void loginMethod() {
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
 
	public void userLogin() {
		customerRepo.loginUser("test@nareshkumar.me", "123",
				new CustomerRepository.LoginCallback() {
					@Override
					public void onSuccess(AccessToken token, Customer customer) {
						Toast.makeText(uploadactivity.this,
								"Login Successfully", Toast.LENGTH_SHORT)
								.show();
						Log.d("", "Login success");
					}

					@Override
					public void onError(Throwable t) {
						// login failed
						Toast.makeText(uploadactivity.this,
								"Login Failed " + t.getMessage(),
								Toast.LENGTH_SHORT).show();
						Log.d("", "Login Failure");
					}
				});
	}

	/**
	 * This custom subclass of Model is the closest thing to a "schema" the Note
	 * model has.
	 * 
	 * When we save an instance of NoteModel, LoopBack uses the property getters
	 * and setters of the subclass to customize the request it makes to the
	 * server. The server handles this freeform request appropriately, saving
	 * our freeform model to the database just as we expect.
	 * 
	 * Note: in a regular application, this class would be defined as top-level
	 * (non-static) class in a file of its own. We are keeping it as a static
	 * nested class only to make it easier to follow this guide.
	 */
	public static class NoteModel extends Model {
		private String user;
		private String comment;
		private Boolean reviewed;

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public Boolean getReviewed() {
			return reviewed;
		}

		public void setReviewed(Boolean reviewed) {
			this.reviewed = reviewed;
		}

	}

	/**
	 * The ModelRepository provides an interface to the Model's "type" on the
	 * server. For instance, we'll (SPOILER!) see in Lessons Two how the
	 * ModelRepository is used for queries; in Lesson Three we'll use it for
	 * custom, collection-level behaviour: those locations within the collection
	 * closest to the given coordinates.
	 * 
	 * This subclass, however, provides an additional benefit: it acts as glue
	 * within the LoopBack interface between a RestAdapter representing the
	 * _server_ and a named collection or type of model within it. In this case,
	 * that type of model is named "note", and it contains NoteModel instances.
	 * 
	 * Note: in a regular application, this class would be defined as top-level
	 * (non-static) class in a file of its own. We are keeping it as a static
	 * nested class only to make it easier to follow this guide.
	 */
	public static class NoteRepository extends ModelRepository<NoteModel> {
		public NoteRepository() {
			super("note", "notes", NoteModel.class);
		}
	}

	
	void showResult(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}
}
