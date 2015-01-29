package com.fileupload;

import com.strongloop.android.loopback.User;

public class Customer extends User {
	String profilename;
	Object id;

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public String getprofileName() {
		return profilename;
	}

	public void setprofileName(String profileName) {
		this.profilename = profileName;
	}
}