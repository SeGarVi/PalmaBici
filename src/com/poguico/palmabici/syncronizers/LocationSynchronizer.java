package com.poguico.palmabici.syncronizers;

import java.util.ArrayList;

import com.poguico.palmabici.SynchronizableActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationSynchronizer {

	private static Location 		location;
	private static LocationManager  manager;
	private static LocationListener listener;
	
	private static ArrayList<SynchronizableActivity> synchronizable_activities;
	
	public static void init(Context context) {
		manager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		listener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				LocationSynchronizer.location = location;
				updateViews();
			}
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {}
			
			@Override
			public void onProviderDisabled(String provider) {}
		};
		
		//manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000L, 0, listener);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
		
		location =  manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location == null)
			location =  manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		synchronizable_activities = new ArrayList<SynchronizableActivity>();
	}
	
	private static void updateViews () {
		for (SynchronizableActivity activity : synchronizable_activities)
			activity.onLocationSynchronization();
	}
	
	public static void addSynchronizableActivity(SynchronizableActivity activity) {
		synchronizable_activities.add(activity);
	}
	
	public static Location getLocation () {
		return location;
	}
}
