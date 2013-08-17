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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.poguico.palmabici.syncronizers.LocationSynchronizer;
import com.poguico.palmabici.syncronizers.NetworkSynchronizer;
import com.poguico.palmabici.util.BikeLane;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

public class StationMapFragment extends    SupportMapFragment
                                implements SynchronizableActivity {

	private BitmapDescriptor marker100 = null;
	private BitmapDescriptor marker90  = null;
	private BitmapDescriptor marker80  = null;
	private BitmapDescriptor marker70  = null;
	private BitmapDescriptor marker60  = null;
	private BitmapDescriptor marker50  = null;
	private BitmapDescriptor marker40  = null;
	private BitmapDescriptor marker30  = null;
	private BitmapDescriptor marker20  = null;
	private BitmapDescriptor marker10  = null;
	private BitmapDescriptor marker0   = null;
	
	private GoogleMap map;
	private SharedPreferences conf;
	
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
				
		try {
			MapsInitializer.initialize(this.getActivity());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
		
		conf=PreferenceManager
				.getDefaultSharedPreferences(this.getActivity());
		NetworkSynchronizer.getInstance(this);
        BikeLane.init(this.getActivity());
	}
	
	public void initMarkers () {
		if (marker100 == null) {
			marker100 = BitmapDescriptorFactory.fromResource(R.drawable.marker100);
			marker90  = BitmapDescriptorFactory.fromResource(R.drawable.marker90);
			marker80  = BitmapDescriptorFactory.fromResource(R.drawable.marker80);
			marker70  = BitmapDescriptorFactory.fromResource(R.drawable.marker70);
			marker60  = BitmapDescriptorFactory.fromResource(R.drawable.marker60);
			marker50  = BitmapDescriptorFactory.fromResource(R.drawable.marker50);
			marker40  = BitmapDescriptorFactory.fromResource(R.drawable.marker40);
			marker30  = BitmapDescriptorFactory.fromResource(R.drawable.marker30);
			marker20  = BitmapDescriptorFactory.fromResource(R.drawable.marker20);
			marker10  = BitmapDescriptorFactory.fromResource(R.drawable.marker10);
			marker0   = BitmapDescriptorFactory.fromResource(R.drawable.marker0);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
        initMap();
        if (NetworkInformation.getNetwork() != null) {
        	updateStations();
        }
	}
	
	@Override
	public void onDestroy() {
		LocationSynchronizer.getInstance(this).detachSynchronizableActivity(this);
		NetworkSynchronizer.getInstance(this).detachSynchronizableActivity(this);
		
		super.onDestroy();
	}
	
	private void initMap() {
		float[] distance = null;
		initMarkers();
		
		map = this.getMap();		
		
		map.clear();
		if (conf.getBoolean("show_bike_lane", true)) {
			drawBikeLane();
		}
		
		map.setMyLocationEnabled(true);		
		Location my_location = LocationSynchronizer.getInstance(this).getLocation();
		
		if (my_location != null) {
			distance = new float[1];
			Location.distanceBetween(NetworkInformation.getNetworkCenter().latitude,
										 NetworkInformation.getNetworkCenter().longitude,
										 my_location.getLatitude(),
										 my_location.getLongitude(), distance);
		}
		
		if (distance == null || distance[0] > 10000) {
			this.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.574689, 2.651332) , 15.0f));
		} else {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(my_location.getLatitude(),
																		my_location.getLongitude()),
																		(float)15.0));
		}
	}

	private void updateStations() {		
		float percentatge;
		float[] distance = new float[1];
		String formatted_distance = "";
		Location my_location = LocationSynchronizer.getInstance(this).getLocation();
		
		for (Station station : NetworkInformation.getNetwork()) {
			percentatge = station.getBusy_slots()*100 / station.getSlots();
			
			if (my_location != null) {
				Location.distanceBetween(station.getLat(),
										 station.getLong(),
										 my_location.getLatitude(),
										 my_location.getLongitude(), distance);
				formatted_distance = " (" + 
										 Formatter.formatDistance(distance[0], this.getActivity()) +
										 ")";
			}
			
			if (percentatge > 95) {
				map.addMarker(new MarkerOptions()
					.position(new LatLng(station.getLat(), station.getLong()))
					.title(station.getName() + formatted_distance)
					.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
		    		.icon(marker100));
			} else if (percentatge > 85) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker90));
			} else if (percentatge > 75) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker80));
			} else if (percentatge > 65) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker70));
			} else if (percentatge > 55) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker60));
			} else if (percentatge > 45) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker50));
			} else if (percentatge > 35) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker40));
			} else if (percentatge > 25) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker30));
			} else if (percentatge > 15) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker20));
			} else if (percentatge > 5) {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker10));
			} else {
				map.addMarker(new MarkerOptions()
				.position(new LatLng(station.getLat(), station.getLong()))
				.title(station.getName() + formatted_distance)
				.snippet(getString(R.string.free_slots) + ": " + station.getFree_slots() +
							   " - " + getString(R.string.bikes) + ": " + station.getBusy_slots())
	    		.icon(marker0));
			} 
		}
	}

	@Override
	public void onSuccessfulNetworkSynchronization() {
		if (map != null) {
			map.clear();
			if (conf.getBoolean("show_bike_lane", true)) {
				drawBikeLane();
			}
			updateStations();
		}
	}

	@Override
	public void onUnsuccessfulNetworkSynchronization() {}

	@Override
	public void onLocationSynchronization() {}
	
	@Override
	public FragmentActivity getSynchronizableActivity() {
		return getActivity();
	}
	
	private void drawBikeLane () {
		for (PolylineOptions path : BikeLane.getPaths()) {
			this.getMap().addPolyline(path);
		}
	}
}
