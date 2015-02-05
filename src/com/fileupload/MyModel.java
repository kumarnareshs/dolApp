package com.fileupload;

import java.util.Date;

import com.strongloop.android.loopback.Model;

public class MyModel extends Model {
	String modelname;
	boolean isavailable;
	int luckynumber;
	String geopoint;
	Date date;
	private Long id;
	private Long serverid;
 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getServerid() {
		return serverid;
	}

	public void setServerid(Long serverid) {
		this.serverid = serverid;
	}

	public String getModelname() {
		return modelname;
	}

	public void setModelname(String modelname) {
		this.modelname = modelname;
	}

	public boolean isIsavailable() {
		return isavailable;
	}

	public void setIsavailable(boolean isavailable) {
		this.isavailable = isavailable;
	}

	public int getLuckynumber() {
		return luckynumber;
	}

	public void setLuckynumber(int luckynumber) {
		this.luckynumber = luckynumber;
	}

	public String getGeopoint() {
		return geopoint;
	}

	public void setGeopoint(String geopoint) {
		this.geopoint = geopoint;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	

}
