package com.poguico.palmabici.network.synchronizer;

import java.util.Calendar;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.poguico.palmabici.DatabaseManager;

public class NetworkSynchronizerHelper {
	private static final long UPDATE_TIME = 600000;
	public enum NetworkSynchronizationState {PENDING, DONE, ERROR};
	
	private static NetworkSynchronizerHelper instance = null;
	
	private Context context;
	private Long    lastUpdate = null;
	private NetworkSynchronizationState syncState = null;
	
	private NetworkSynchronizerHelper() {
		lastUpdate = DatabaseManager.getInstance(context).getLastUpdateTime();
	}
	
	public NetworkSynchronizerHelper getInstance(Context context) {
		if (instance == null) {
			instance = new NetworkSynchronizerHelper();
		}
		this.context = context;
		return instance;
	}
	
	public NetworkSynchronizationState sync() {
		long now   = Calendar.getInstance().getTimeInMillis();
		long delay = now - lastUpdate;
		
		if ( syncState  != NetworkSynchronizationState.PENDING &&
		    (delay/1000) > UPDATE_TIME) {
			syncState = forceSync();
		}
		
		return syncState;
	}
	
	public NetworkSynchronizationState forceSync() {
		ConnectivityManager conMgr =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null || !i.isAvailable() || !i.isConnected()) {
			return NetworkSynchronizationState.ERROR;
		}
		
		return NetworkSynchronizationState.PENDING;
	}
}
