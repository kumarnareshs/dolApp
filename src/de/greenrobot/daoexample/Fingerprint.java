package de.greenrobot.daoexample;

import java.util.Map;

import com.database.Constants;
import com.fingerprint.upload.Util;
import com.strongloop.android.loopback.Model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table FINGERPRINT.
 */
public class Fingerprint extends Model implements Constants{

	@Override
	public Map<String, ? extends Object> toMap() {
		// TODO Auto-generated method stub
		put(ANDROIDID, Util.getAndroidId());
		return super.toMap();
	}
    private Long id;
    private String filename;
    private String filepath;
    private String filelength;
    private Long androidmusicid;
    private String fileformat;
    private Float filesize;
    private Integer filebitrate;
    private Integer filefrequency;
    private Integer filenoofchannels;
    private String fingerprint;
    private String fulllengthfingerprint;
    private java.util.Date fingerprintcreateddate;
    private Boolean isdeleted;
    private Integer trackid;
    private Boolean isSongAvailableInServer;
    private Boolean isfulllengthfingerprintgenerated;
    private Boolean isfulllengthfingerprintuploaded;
    private String status;
    private Integer tempmetadatarowid;
    private Integer cid;
    private java.util.Date uploadeddate;
    private java.util.Date lastuploadeddate;
    private java.util.Date createddate;
    private java.util.Date lastmodifieddate;

    public Fingerprint() {
    }

    public Fingerprint(Long id) {
        this.id = id;
    }

    public Fingerprint(Long id, String filename, String filepath, String filelength, Long androidmusicid, String fileformat, Float filesize, Integer filebitrate, Integer filefrequency, Integer filenoofchannels, String fingerprint, String fulllengthfingerprint, java.util.Date fingerprintcreateddate, Boolean isdeleted, Integer trackid, Boolean isSongAvailableInServer, Boolean isfulllengthfingerprintgenerated, Boolean isfulllengthfingerprintuploaded, String status, Integer tempmetadatarowid, Integer cid, java.util.Date uploadeddate, java.util.Date lastuploadeddate, java.util.Date createddate, java.util.Date lastmodifieddate) {
        this.id = id;
        this.filename = filename;
        this.filepath = filepath;
        this.filelength = filelength;
        this.androidmusicid = androidmusicid;
        this.fileformat = fileformat;
        this.filesize = filesize;
        this.filebitrate = filebitrate;
        this.filefrequency = filefrequency;
        this.filenoofchannels = filenoofchannels;
        this.fingerprint = fingerprint;
        this.fulllengthfingerprint = fulllengthfingerprint;
        this.fingerprintcreateddate = fingerprintcreateddate;
        this.isdeleted = isdeleted;
        this.trackid = trackid;
        this.isSongAvailableInServer = isSongAvailableInServer;
        this.isfulllengthfingerprintgenerated = isfulllengthfingerprintgenerated;
        this.isfulllengthfingerprintuploaded = isfulllengthfingerprintuploaded;
        this.status = status;
        this.tempmetadatarowid = tempmetadatarowid;
        this.cid = cid;
        this.uploadeddate = uploadeddate;
        this.lastuploadeddate = lastuploadeddate;
        this.createddate = createddate;
        this.lastmodifieddate = lastmodifieddate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilelength() {
        return filelength;
    }

    public void setFilelength(String filelength) {
        this.filelength = filelength;
    }

    public Long getAndroidmusicid() {
        return androidmusicid;
    }

    public void setAndroidmusicid(Long androidmusicid) {
        this.androidmusicid = androidmusicid;
    }

    public String getFileformat() {
        return fileformat;
    }

    public void setFileformat(String fileformat) {
        this.fileformat = fileformat;
    }

    public Float getFilesize() {
        return filesize;
    }

    public void setFilesize(Float filesize) {
        this.filesize = filesize;
    }

    public Integer getFilebitrate() {
        return filebitrate;
    }

    public void setFilebitrate(Integer filebitrate) {
        this.filebitrate = filebitrate;
    }

    public Integer getFilefrequency() {
        return filefrequency;
    }

    public void setFilefrequency(Integer filefrequency) {
        this.filefrequency = filefrequency;
    }

    public Integer getFilenoofchannels() {
        return filenoofchannels;
    }

    public void setFilenoofchannels(Integer filenoofchannels) {
        this.filenoofchannels = filenoofchannels;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getFulllengthfingerprint() {
        return fulllengthfingerprint;
    }

    public void setFulllengthfingerprint(String fulllengthfingerprint) {
        this.fulllengthfingerprint = fulllengthfingerprint;
    }

    public java.util.Date getFingerprintcreateddate() {
        return fingerprintcreateddate;
    }

    public void setFingerprintcreateddate(java.util.Date fingerprintcreateddate) {
        this.fingerprintcreateddate = fingerprintcreateddate;
    }

    public Boolean getIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(Boolean isdeleted) {
        this.isdeleted = isdeleted;
    }

    public Integer getTrackid() {
        return trackid;
    }

    public void setTrackid(Integer trackid) {
        this.trackid = trackid;
    }

    public Boolean getIsSongAvailableInServer() {
        return isSongAvailableInServer;
    }

    public void setIsSongAvailableInServer(Boolean isSongAvailableInServer) {
        this.isSongAvailableInServer = isSongAvailableInServer;
    }

    public Boolean getIsfulllengthfingerprintgenerated() {
        return isfulllengthfingerprintgenerated;
    }

    public void setIsfulllengthfingerprintgenerated(Boolean isfulllengthfingerprintgenerated) {
        this.isfulllengthfingerprintgenerated = isfulllengthfingerprintgenerated;
    }

    public Boolean getIsfulllengthfingerprintuploaded() {
        return isfulllengthfingerprintuploaded;
    }

    public void setIsfulllengthfingerprintuploaded(Boolean isfulllengthfingerprintuploaded) {
        this.isfulllengthfingerprintuploaded = isfulllengthfingerprintuploaded;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTempmetadatarowid() {
        return tempmetadatarowid;
    }

    public void setTempmetadatarowid(Integer tempmetadatarowid) {
        this.tempmetadatarowid = tempmetadatarowid;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public java.util.Date getUploadeddate() {
        return uploadeddate;
    }

    public void setUploadeddate(java.util.Date uploadeddate) {
        this.uploadeddate = uploadeddate;
    }

    public java.util.Date getLastuploadeddate() {
        return lastuploadeddate;
    }

    public void setLastuploadeddate(java.util.Date lastuploadeddate) {
        this.lastuploadeddate = lastuploadeddate;
    }

    public java.util.Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(java.util.Date createddate) {
        this.createddate = createddate;
    }

    public java.util.Date getLastmodifieddate() {
        return lastmodifieddate;
    }

    public void setLastmodifieddate(java.util.Date lastmodifieddate) {
        this.lastmodifieddate = lastmodifieddate;
    }

}
