package com.fingerprint.server;

public interface IFingerPrintSync {

	public void sendFullFingerPrint(Long rowId, String fullfingerprint,Double length);
}
