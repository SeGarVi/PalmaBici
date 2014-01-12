/*
 * Copyright 2013 Sergio Garcia Villalonga (yayalose@gmail.com)
 *
 * This file is part of PalmaBici.
 *
 *    PalmaBici is free software: you can redistribute it and/or modify
 *    it under the terms of the Affero GNU General Public License version 3
 *    as published by the Free Software Foundation.
 *
 *    PalmaBici is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    Affero GNU General Public License for more details
 *    (https://www.gnu.org/licenses/agpl-3.0.html).
 *    
 */

package com.poguico.palmabici.network.synchronizer;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.poguico.palmabici.DatabaseManager;
import com.poguico.palmabici.SynchronizableElement;

public class NetworkSynchronizer {
	private static final long UPDATE_TIME = 600000;
	public enum NetworkSynchronizationState {UPDATING, UPDATED, ERROR};
	
	private static NetworkSynchronizer instance = null;
	
	private ArrayList<SynchronizableElement> synchronizableElements;
	private Context context;
	private Long    lastUpdate = null;
	private NetworkSynchronizationState syncState = NetworkSynchronizationState.UPDATED;
	
	private NetworkSynchronizer(Context context) {
		synchronizableElements = new ArrayList<SynchronizableElement>();
		lastUpdate = DatabaseManager.getInstance(context).getLastUpdateTime();
		this.context = context;
	}
	
	public static NetworkSynchronizer getInstance(Context context) {
		if (instance == null) {
			instance = new NetworkSynchronizer(context);
		}
		return instance;
	}
	
	public NetworkSynchronizationState sync() {
		long now;
		long delay;
		
		if ( syncState  != NetworkSynchronizationState.UPDATING) {
			now   = Calendar.getInstance().getTimeInMillis();
			delay = now - lastUpdate;
			
			if (delay > UPDATE_TIME) {
				syncState = forceSync();
			}
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
		
		NetworkSynchronizerTask.synchronize(context, new NetworkSyncCallback() {
			
			@Override
			public void onNetworkSynchronized(long updateTime) {
				syncState  = NetworkSynchronizationState.UPDATED;
				lastUpdate = updateTime;
				for (SynchronizableElement activity : synchronizableElements) {
					activity.onSuccessfulNetworkSynchronization();
				}
			}
			
			@Override
			public void onNetworkError(String errorCode) {
				syncState = NetworkSynchronizationState.ERROR;
				for (SynchronizableElement activity : synchronizableElements) {
					activity.onUnsuccessfulNetworkSynchronization();
				}
			}
		});
		
		return NetworkSynchronizationState.UPDATING;
	}
	
	public synchronized void addSynchronizableActivity(SynchronizableElement activity) {
		if (!synchronizableElements.contains(activity)) { 
			synchronizableElements.add(activity);
		}
	}
	
	public synchronized void detachSynchronizableActivity(SynchronizableElement activity) {
		synchronizableElements.remove(activity);
	}
}
