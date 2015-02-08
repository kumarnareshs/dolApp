package com.fingerprint.server;

import de.greenrobot.daoexample.Metadata;

public interface IFingerPrintSync {

	public void sendFullFingerPrint(Long rowId, String fullfingerprint,Double length,Metadata md);
}
