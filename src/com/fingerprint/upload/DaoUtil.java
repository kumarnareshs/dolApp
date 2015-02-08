package com.fingerprint.upload;

import java.util.ArrayList;
import java.util.List;

import com.fingerprint.database.DBAdapter;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.FingerPrintRepository;
import de.greenrobot.daoexample.Fingerprint;
import de.greenrobot.daoexample.FingerprintDao;
import de.greenrobot.daoexample.Metadata;
import de.greenrobot.daoexample.MetadataDao;

public class DaoUtil {

	public static ModelRepository getRepository(String tablename,
			RestAdapter restAdapter) {
		ModelRepository mr = null;
		if (tablename.equalsIgnoreCase(FingerprintDao.TABLENAME)) {
			mr = restAdapter.createRepository(FingerPrintRepository.class);
		}
		return mr;
	}
	
	public static List ConvertRawObjectsToModels(List objects) {
		if (objects.size() == 0) {
			return null;
		}
		String classname = objects.get(0).getClass().getSimpleName();

		if (classname.equalsIgnoreCase(FingerprintDao.TABLENAME)) {
			List<Fingerprint> fplist = new ArrayList<Fingerprint>();
			for (Fingerprint obj : (List<Fingerprint>) objects) {
				if (obj.getCid() != null) {
					obj.setId(Long.valueOf(obj.getCid()));
				}
				fplist.add(obj);

			}
			return fplist;
		} else
			if (classname.equalsIgnoreCase(MetadataDao.TABLENAME)) {
				List<Metadata> metalist = new ArrayList<Metadata>();
				for (Metadata obj : (List<Metadata>) objects) {
					if (obj.getCid() != null) {
						obj.setId(Long.valueOf(obj.getCid()));
					}
					metalist.add(obj);

				}
				return metalist;
			} 
			else {
			return null;
		}
	}

	public static QueryBuilder getQueryBuilder(String tablename, DBAdapter dbadapter) {
		QueryBuilder qb= null;
		if (tablename.equalsIgnoreCase(FingerprintDao.TABLENAME)) {
			qb=dbadapter.getGlobalDaoSession().getFingerprintDao().queryBuilder();
		}
		return qb;
	}
	
}
