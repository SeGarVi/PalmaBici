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

package com.poguico.palmabici;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class Synchronizer {
	
	private static Synchronizer instance = null;
	
	private static final String URL = "http://api.citybik.es/palma.json";
	private Long last_update = Calendar.getInstance().getTimeInMillis();
	
	public class SynchronizeTask extends AsyncTask <Void, Void, Void> {
        
		SynchronizableActivity activity;
		boolean connectivity = true;
		Synchronizer synchronizer = Synchronizer.getInstance();
		
		public SynchronizeTask (SynchronizableActivity activity) {
			this.activity = activity;
		}
		
    	protected Void doInBackground(Void... params) {    		
    		ConnectivityManager conMgr =  (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

    		NetworkInfo i = conMgr.getActiveNetworkInfo();
    		if (i == null || !i.isAvailable() || !i.isConnected())
    			connectivity = false;    			
    		else
    			NetworkInformation.setNetwork(synchronizer.getNetworkInfo());
    		
            return null;
        }

        protected void onPostExecute(Void params) {
        	if (connectivity) {
	        	synchronizer.last_update = Calendar.getInstance().getTimeInMillis();
	        	activity.successfulSynchronization();
        	} else {
        		activity.unsuccessfulSynchronization();
        	}
        }
    }
	
	protected static Synchronizer getInstance () {
		if (instance == null)
			instance = new Synchronizer();
		
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
				Log.e(Synchronizer.class.toString(), "Failed to download file");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return builder.toString();
	}
	
	public Long getLastUpdate () {
		return last_update;
	}
}