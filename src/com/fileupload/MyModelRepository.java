package com.fileupload;

import com.strongloop.android.loopback.ModelRepository;

public class MyModelRepository extends ModelRepository<MyModel>{

	 public MyModelRepository() {
	        super("mymodel", "mymodels", MyModel.class);
	    }

}
