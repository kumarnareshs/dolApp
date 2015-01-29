package de.greenrobot.daoexample;

import com.strongloop.android.loopback.ModelRepository;

public class MetadataRepository extends ModelRepository<Metadata>{

	 public MetadataRepository() {
	        super("metadata", "metadata", Metadata.class);
	    }

}
