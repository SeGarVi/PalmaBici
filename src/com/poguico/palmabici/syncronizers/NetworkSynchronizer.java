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

package com.poguico.palmabici.syncronizers;

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

import com.poguico.palmabici.SynchronizableActivity;
import com.poguico.palmabici.util.NetworkInformation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class NetworkSynchronizer {
	
	private static NetworkSynchronizer instance = null;
	private ArrayList<SynchronizableActivity> synchronizable_activities;
	
	private static final String URL = "http://api.citybik.es/palma.json";
	private Long last_update = Calendar.getInstance().getTimeInMillis();
	
	private class SynchronizeTask extends AsyncTask <Void, Void, Void> {
        
		SynchronizableActivity activity;
		NetworkSynchronizer    synchronizer;
		boolean connectivity = true;
		
		public SynchronizeTask (SynchronizableActivity activity) {
			this.activity     = activity;
			this.synchronizer = NetworkSynchronizer.getInstance(activity);
		}
		
    	protected Void doInBackground(Void... params) {    		
    		ConnectivityManager conMgr =
    				(ConnectivityManager)activity.getSynchronizableActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

    		NetworkInfo i = conMgr.getActiveNetworkInfo();
    		if (i == null || !i.isAvailable() || !i.isConnected()) {
    			connectivity = false;    			
    		} else {
    			NetworkInformation.setNetwork(activity.getSynchronizableActivity(), synchronizer.getNetworkInfo());
    		}
    		
            return null;
        }

        protected void onPostExecute(Void params) {
        	if (connectivity) {
	        	synchronizer.last_update = Calendar.getInstance().getTimeInMillis();
	        	successfulNetworkSynchronization();
        	} else {
        		unSuccessfulNetworkSynchronization();
        	}
        }
    }
	
	private NetworkSynchronizer() {
		synchronizable_activities = new ArrayList<SynchronizableActivity>();
	}
	
	public static NetworkSynchronizer getInstance (SynchronizableActivity activity) {
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
			if (status_line.getStatusCode() == 200) {
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
	
	private void successfulNetworkSynchronization () {
		for (SynchronizableActivity activity : synchronizable_activities) {
			activity.onSuccessfulNetworkSynchronization();
		}
	}
	
	private void unSuccessfulNetworkSynchronization () {
		for (SynchronizableActivity activity : synchronizable_activities) {
			activity.onUnsuccessfulNetworkSynchronization();
		}
	}
	
	public void addSynchronizableActivity(SynchronizableActivity activity) {
		if (!synchronizable_activities.contains(activity)) { 
			synchronizable_activities.add(activity);
		}
	}
	
	public void detachSynchronizableActivity(SynchronizableActivity activity) {
		synchronizable_activities.remove(activity);
	}
	
	public Long getLastUpdate () {
		return last_update;
	}
	
	public void synchronize(SynchronizableActivity activity) {
		new SynchronizeTask(activity).execute((Void [])null);
	}
}
