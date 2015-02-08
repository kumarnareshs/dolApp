package com.fingerprint.database;

import java.util.List;
import java.util.Map;

public interface IFingerprintDB {

	public Map<Long,String> getListOfSongsPathInLocalDb();
	public boolean deleteSongsInLocalDb(List<Long> songlisttodelete);
	public void setFingerprintStatus(List<Long> listOfnomatchFoundSongs,String fpStatusNomatchfound);
	public void setFingerprintStatus(Long listOfnomatchFoundSongs,String fpStatusNomatchfound);
	
}
