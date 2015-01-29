package com.fileupload;

import java.util.List;

import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;

public class MyModelRepository extends ModelRepository<MyModel>{

	 public MyModelRepository() {
	        super("mymodel", "mymodels", MyModel.class);
	    }

}
