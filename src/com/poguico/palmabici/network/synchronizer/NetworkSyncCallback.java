package com.poguico.palmabici.network.synchronizer;

public interface NetworkSyncCallback {
	public void onNetworkSynchronized(long updateTime);
	public void onNetworkError(String errorCode);
}
