package com.fingerprint.server;

import java.util.List;

public interface ICommomSync {

	void sendToServer(List<Long> ids,final String tablename);
	void sendAllToServer(final String tablename);
	void sendToServer(Long id,final String tablename);
    void initialSync(String tablename);
}
