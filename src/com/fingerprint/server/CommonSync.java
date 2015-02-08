package com.fingerprint.server;

import java.util.List;

import android.app.Application;
import android.util.Log;

import com.database.Constants;
import com.fingerprint.database.DBAdapter;
import com.fingerprint.upload.DaoUtil;
import com.fingerprint.upload.Util;
import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.VirtualObject;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao.Properties;

public class CommonSync<T extends Model> implements ICommomSync,Constants {

	
	private RestAdapter restAdapter;
	private DBAdapter dbadapter;
	private final String TAG = getClass().getName();

	private CommonSync() {

	}

	public CommonSync(RestAdapter restAdapter, DBAdapter dbadapter) {
		this.restAdapter = restAdapter;
		this.dbadapter = dbadapter;
	}

	public void send(List rows, final String tablename) {

		// ModelRepository<T extends Model> extends RestRepository<T>
		// = getRepository(tablename);
		ModelRepository repository = DaoUtil.getRepository(tablename, restAdapter);
		repository.updateOrInsertList(rows, new ListCallback<VirtualObject>() {

			@Override
			public void onSuccess(List objects) {
				if (objects.size() != 0) {
					List<T> modelobjects = (List<T>) DaoUtil.ConvertRawObjectsToModels(objects);
					for (T model : modelobjects) {
						dbadapter.setInitialUploadedStatus((Long) model.getId(),tablename);
					}
				}
			}

			@Override
			public void onError(Throwable t) {
				Util.setDebuger();
				Log.e(TAG, "::onError:" + t);
			}
		});
	}

	@Override
	public void sendAllToServer(String tablename) {

		QueryBuilder qb = DaoUtil.getQueryBuilder(tablename, dbadapter);
		qb.where(Properties.Uploadeddate.isNull());
		List modellist = qb.list();
		send(modellist, tablename);
	}

	@Override
	public void sendToServer(Long id, String tablename) {

		QueryBuilder qb = DaoUtil.getQueryBuilder(tablename, dbadapter);
		qb.where(Properties.Id.eq(id));
		List modellist = qb.list();
		send(modellist, tablename);

	}

	@Override
	public void sendToServer(List<Long> ids, String tablename) {
		QueryBuilder qb = DaoUtil.getQueryBuilder(tablename, dbadapter);
		qb.where(Properties.Id.in(ids));
		List modellist = qb.list();
		send(modellist, tablename);
	}
	
	public void initialSync(String tablename){
		QueryBuilder qb = DaoUtil.getQueryBuilder(tablename, dbadapter);
		qb.where(Properties.Status.eq(SERVER_STATUS_READY_TO_UPLOAD),Properties.Uploadeddate.isNull());
		List modellist = qb.list();
		if(modellist.size()>0){			
			send(modellist, tablename);
		}
	}
}
