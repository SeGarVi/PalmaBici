/*
 * Copyright 2014 Sergio Garcia Villalonga (yayalose@gmail.com)
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
import java.util.concurrent.Semaphore;

import com.poguico.palmabici.DatabaseManager;
import com.poguico.palmabici.MainActivity;
import com.poguico.palmabici.R;
import com.poguico.palmabici.map.OpenStreetMapConstants;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NetworkStationAlarm extends IntentService 
									implements OpenStreetMapConstants{

	private static final String ONLY_ONE_ALARM = "only_one_alarm";
	private static final long WAIT_TIME = 30000;
	private static final long TIMEOUT = 60000;
	
	private static ArrayList<String> stationAlarmsId = null;
	private static boolean active = false;
	private static DatabaseManager dbManager;
	private static Context context = null;
	private static NetworkInformation networkInformation = null;
	
	private Semaphore semaphore;
	private SharedPreferences conf;
	
	public NetworkStationAlarm() {
		super("NetworkStationAlarm");
		Log.i("NetworkStationAlarm", "Initializing class");
		active = true;
		semaphore = new Semaphore(1);
	}

	public static synchronized void addAlarm(Context c, Station station) {
		if (stationAlarmsId == null) {
			Log.i("NetworkStationAlarm", "Initializing alarms list");
			networkInformation = NetworkInformation.getInstance(context);
			stationAlarmsId = new ArrayList<String>();
			dbManager = DatabaseManager.getInstance(c);
			context = c;
		}
		
		Log.i("NetworkStationAlarm", "Adding alarm for station " + station.getNEstacio());
		dbManager.setAlarm(station);
		stationAlarmsId.add(station.getNEstacio());
	}
	
	public static synchronized void removeAlarm(Station station) {
		if (stationAlarmsId.contains(station.getNEstacio())) {
			dbManager.removeAlarm(station);
			stationAlarmsId.remove(station.getNEstacio());
			Log.i("NetworkStationAlarm", "Alarm for station " + station.getNEstacio() + " removed");
		}
	}
	
	public static synchronized void removeAlarms() {
		for (String id : stationAlarmsId) {
			dbManager.removeAlarm(networkInformation.get(id));
		}
		stationAlarmsId.clear();
	}
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.i("NetworkStationAlarm", "Starting thread");
		long startTime = Calendar.getInstance().getTimeInMillis();
		long now = startTime;
		final NetworkInformation networkInformation =
				NetworkInformation.getInstance(context);
		conf=PreferenceManager
				.getDefaultSharedPreferences(context);
		
		while (!NetworkStationAlarm.stationAlarmsId.isEmpty() &&
				!(conf.getBoolean("alarm_timeout", false) && now - startTime > TIMEOUT)) {
			Log.i("NetworkStationAlarm", "Getting network info...");
			NetworkSynchronizerTask.synchronize(context, new NetworkSyncCallback() {
				
				@Override
				public void onNetworkSynchronized(long updateTime) {
					Log.i("NetworkStationAlarm", "Network synchronized");
					Station station;
					for (String nEstacio : stationAlarmsId) {
						station = networkInformation.get(nEstacio);
						Log.i("NetworkStationAlarm", "Station " + station.getNEstacio() + " has " + station.getBusySlots() + " bikes available");
						if (station.getBusySlots() > 0) {
							showNotification(station);
							
							if (conf.getBoolean(ONLY_ONE_ALARM, true)) {
								NetworkStationAlarm.removeAlarms();
							} else {							
								NetworkStationAlarm.removeAlarm(station);
							}
						}
					}
					semaphore.release();
				}
				
				@Override
				public void onNetworkError(String errorCode) {
					Log.i("NetworkStationAlarm", "Error when synchornizing");
					semaphore.release();
				}
			});
			
			try {
				semaphore.acquire();
				Thread.sleep(30000);
				now = Calendar.getInstance().getTimeInMillis();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (!NetworkStationAlarm.stationAlarmsId.isEmpty()) {
			NetworkStationAlarm.removeAlarms();
		}
		Log.i("NetworkStationAlarm", "Finishing thread");
	}

	@Override
	public void onDestroy() {
		Log.i("NetworkStationAlarm", "Destroying class");
		active = false;
		super.onDestroy();
	}
	
	public void showNotification(Station station) {
		SharedPreferences mPrefs;
    	SharedPreferences.Editor edit;
    	String message = Formatter.formatBikesAvailableMessage(context, station);
		Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.palmabici_bw);
		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
				new Intent(context, MainActivity.class),
				0);
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		                              .setSmallIcon(R.drawable.bike)
		                              .setLargeIcon(bigIcon)
		                              .setContentTitle("PalmaBici")
		                              .setContentText(message)
		                              .setLights(0x0000ff00, 1000, 1000)
		                              .setTicker(message)
		                              .setSound(uri)
		                              .setAutoCancel(true);
		
		NotificationManager mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		mBuilder.setContentIntent(pendingIntent);
		
    	mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    	edit = mPrefs.edit();
        edit.putString(PREFS_SHOWN_STATION, station.getNEstacio());
        edit.commit();
		
		
		mgr.notify(1234, mBuilder.build());
	}

	public static boolean isActive() {
		return active;
	}
	
	public static boolean hasAlarm(String id) {
		return stationAlarmsId != null && stationAlarmsId.contains(id);
	}
}
