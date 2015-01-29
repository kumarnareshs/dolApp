package de.greenrobot.daoexample;

import com.strongloop.android.loopback.ModelRepository;

public class FingerPrintRepository extends ModelRepository<Fingerprint>{

	 public FingerPrintRepository() {
	        super("fingerprints", "fingerprints", Fingerprint.class);
	    }

}
