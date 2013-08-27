/*
 * Copyright 2012 Sergio Garcia Villalonga (yayalose@gmail.com)
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

package com.poguico.palmabici.synchronizers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.poguico.palmabici.DatabaseManager;
import com.poguico.palmabici.SynchronizableActivity;
import com.poguico.palmabici.parsers.Parser;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class NetworkSynchronizer {
	private static final int HTTP_STATUS_OK = 200;
	
	private static NetworkSynchronizer instance = null;
	private NetworkInformation network;
	private long lastDBTime;
	private ArrayList<SynchronizableActivity> synchronizable_activities;
	
	private static final String URL = "http://api.citybik.es/palma.json";
	
	private class SynchronizeTask extends AsyncTask <Void, Void, Void> {
        
		private SynchronizableActivity activity;
		private NetworkSynchronizer    synchronizer;
		private NetworkInformation     network;
		private boolean               connectivity;
		
		
		public SynchronizeTask (SynchronizableActivity activity) {
			this.activity     = activity;
			this.synchronizer = NetworkSynchronizer.getInstance(activity);
			this.network      = NetworkInformation.getInstance();
			this.connectivity = true;
		}
		
    	protected Void doInBackground(Void... params) {
    		String jsonNetwork;
    		ArrayList <Station> parsedNetowrk = null;
    		long   lastUpdateTime = 0;
    		Context context = activity.getSynchronizableActivity();
    		ConnectivityManager conMgr =
    				(ConnectivityManager)activity.getSynchronizableActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    		DatabaseManager dbManager =
    				DatabaseManager.getInstance(context);

    		NetworkInfo i = conMgr.getActiveNetworkInfo();
    		if (i == null || !i.isAvailable() || !i.isConnected()) {
    			connectivity = false;
    			if (network.getNetwork() == null) {
    				lastUpdateTime = dbManager.getLastUpdateTime();
        			parsedNetowrk  = dbManager.getLastStationNetworkState(context);
        			
            		network.setNetwork(parsedNetowrk);
            		network.setLastUpdateTime(lastUpdateTime);
            		lastDBTime = lastUpdateTime;
    			}
    		} else {
    			jsonNetwork    = synchronizer.getNetworkInfo();
    			lastUpdateTime = Calendar.getInstance().getTimeInMillis();
    			parsedNetowrk  = Parser.parseNetworkJSON(context, jsonNetwork);
    			
        		network.setNetwork(parsedNetowrk);
        		network.setLastUpdateTime(lastUpdateTime);
    		}
    		
            return null;
        }

        protected void onPostExecute(Void params) {
        	if (connectivity) {
	        	successfulNetworkSynchronization();
        	} else {
        		unSuccessfulNetworkSynchronization();
        	}
        }
    }
	
	private NetworkSynchronizer() {
		synchronizable_activities = new ArrayList<SynchronizableActivity>();
		network = NetworkInformation.getInstance();
		lastDBTime = 0;
	}
	
	public synchronized static NetworkSynchronizer getInstance (SynchronizableActivity activity) {
		if (instance == null) {
			instance = new NetworkSynchronizer();
		}		
		instance.addSynchronizableActivity(activity);
		
		return instance;
	}
	
	private String getNetworkInfo() {
		StringBuilder builder = new StringBuilder();
		HttpClient    client  = new DefaultHttpClient();
		HttpGet 	  request = new HttpGet(URL);
		String		  line;
		
		try {
			HttpResponse response = client.execute(request);
			StatusLine   status_line = response.getStatusLine();
			if (status_line.getStatusCode() == HTTP_STATUS_OK) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				InputStreamReader content_reader = new InputStreamReader(content);
				BufferedReader reader = new BufferedReader(content_reader);
				
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(NetworkSynchronizer.class.toString(),
						"Failed to download file");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return builder.toString();
	}
	
	public void storeToDB (Context context) {
		if (network.getLastUpdateTime() > lastDBTime) {
			DatabaseManager dbManager = DatabaseManager.getInstance(context);
			dbManager.saveLastStationNetworkState(network.getNetwork());
			dbManager.saveLastUpdateTime(network.getLastUpdateTime());
			lastDBTime = network.getLastUpdateTime();
		}
	}
	
	private synchronized void successfulNetworkSynchronization () {
		for (SynchronizableActivity activity : synchronizable_activities) {
			activity.onSuccessfulNetworkSynchronization();
		}
	}
	
	private synchronized void unSuccessfulNetworkSynchronization () {
		for (SynchronizableActivity activity : synchronizable_activities) {
			activity.onUnsuccessfulNetworkSynchronization();
		}
	}
	
	public synchronized void addSynchronizableActivity(SynchronizableActivity activity) {
		if (!synchronizable_activities.contains(activity)) { 
			synchronizable_activities.add(activity);
		}
	}
	
	public synchronized void detachSynchronizableActivity(SynchronizableActivity activity) {
		synchronizable_activities.remove(activity);
	}
	
	public synchronized void synchronize(SynchronizableActivity activity) {
		new SynchronizeTask(activity).execute((Void [])null);
	}
}
