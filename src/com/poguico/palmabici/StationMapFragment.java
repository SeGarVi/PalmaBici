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

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.poguico.palmabici.synchronizers.LocationSynchronizer;
import com.poguico.palmabici.synchronizers.NetworkSynchronizer;
import com.poguico.palmabici.util.BikeLane;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;
import com.poguico.palmabici.widgets.StationInfoWidget;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

public class StationMapFragment extends    SupportMapFragment
                                implements SynchronizableActivity {
	private static final String BIKE_LANE_OPTION = "show_bike_lane";
	
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
	private BitmapDescriptor marker100Alert = null;
	private BitmapDescriptor marker90Alert  = null;
	private BitmapDescriptor marker80Alert  = null;
	private BitmapDescriptor marker70Alert  = null;
	private BitmapDescriptor marker60Alert  = null;
	private BitmapDescriptor marker50Alert  = null;
	private BitmapDescriptor marker40Alert  = null;
	private BitmapDescriptor marker30Alert  = null;
	private BitmapDescriptor marker20Alert  = null;
	private BitmapDescriptor marker10Alert  = null;
	private BitmapDescriptor marker0Alert   = null;
	private HashMap <String, Marker> mapMarkers = null;
	private ArrayList<Polyline> bikeLane = null;
	
	private GoogleMap map;
	private SharedPreferences conf;
	private NetworkInformation network;
	
	private boolean bikeLaneState;
	
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
		bikeLane      = new ArrayList<Polyline>();
		bikeLaneState = conf.getBoolean(BIKE_LANE_OPTION, true);
		network       = NetworkInformation.getInstance();
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
			marker100Alert = BitmapDescriptorFactory.fromResource(R.drawable.marker100_alert);
			marker90Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker90_alert);
			marker80Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker80_alert);
			marker70Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker70_alert);
			marker60Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker60_alert);
			marker50Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker50_alert);
			marker40Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker40_alert);
			marker30Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker30_alert);
			marker20Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker20_alert);
			marker10Alert  = BitmapDescriptorFactory.fromResource(R.drawable.marker10_alert);
			marker0Alert   = BitmapDescriptorFactory.fromResource(R.drawable.marker0_alert);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
        initMap();
        if (network.getNetwork() != null) {
        	updateStations();
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		boolean showBikeLane = conf.getBoolean(BIKE_LANE_OPTION, true);
		if (showBikeLane != bikeLaneState) {
			bikeLaneState = showBikeLane;
			toggleBikeLane(bikeLaneState);
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
		map.setInfoWindowAdapter(new StationInfoWidget(this.getActivity()));
		map.setMyLocationEnabled(true);		
		Location my_location =
				LocationSynchronizer.getInstance(this).getLocation();
		
		if (my_location != null) {
			distance = new float[1];
			Location.distanceBetween(network.getCenter().latitude,
					                 network.getCenter().longitude,
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
		drawBikeLane(bikeLaneState);
	}
	
	private void updateStations() {		
		if (mapMarkers == null) {
			mapMarkers = new HashMap<String, Marker>();
		} else {
			for (Marker marker : mapMarkers.values()) {
				marker.remove();
			}
		}
		drawStationMarkers();
	}

	private void drawStationMarkers() {		
		float   percentage;
		float[] distance           = new float[1];
		String   formatted_distance = "";
		Location my_location        = LocationSynchronizer.getInstance(this)
                                                          .getLocation();
		
		for (Station station : network.getNetwork()) {
			percentage = station.getBusy_slots()*100 / station.getSlots();
			
			if (my_location != null) {
				Location.distanceBetween(station.getLat(),
										 station.getLong(),
										 my_location.getLatitude(),
										 my_location.getLongitude(), distance);
				formatted_distance = " (" + 
									 Formatter.formatDistance(distance[0], this.getActivity()) +
									 ")";
			}
			
			if (percentage > 95) {
				mapMarkers.put(station.getN_estacio(),
					map.addMarker(new MarkerOptions()
					   .position(new LatLng(station.getLat(), station.getLong()))
					   .title(station.getName() + formatted_distance)
					   .snippet(station.getN_estacio())
		    		   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
		    						   marker100 : marker100Alert)));
			} else if (percentage > 85) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker90 : marker90Alert)));
			} else if (percentage > 75) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker80 : marker80Alert)));
			} else if (percentage > 65) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker70 : marker70Alert)));
			} else if (percentage > 55) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker60 : marker60Alert)));
			} else if (percentage > 45) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker50 : marker50Alert)));
			} else if (percentage > 35) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker40 : marker40Alert)));
			} else if (percentage > 25) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker30 : marker30Alert)));
			} else if (percentage > 15) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker20 : marker20Alert)));
			} else if (percentage > 5) {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker10 : marker10Alert)));
			} else {
				mapMarkers.put(station.getN_estacio(),
						map.addMarker(new MarkerOptions()
						   .position(new LatLng(station.getLat(), station.getLong()))
						   .title(station.getName() + formatted_distance)
						   .snippet(station.getN_estacio())
						   .icon((station.getBroken_bikes() == 0 &&
		    				   station.getBroken_slots() == 0) ?
			    						   marker0 : marker0Alert)));
			} 
		}
	}
	
	@Override
	public void onSuccessfulNetworkSynchronization() {
		updateStations();
	}

	@Override
	public void onUnsuccessfulNetworkSynchronization() {
		updateStations();
	}

	@Override
	public void onLocationSynchronization() {}
	
	@Override
	public FragmentActivity getSynchronizableActivity() {
		return getActivity();
	}
	
	private void drawBikeLane (boolean visible) {
		Polyline polyline;
		for (PolylineOptions path : BikeLane.getPaths()) {
			polyline = this.getMap().addPolyline(path);
			polyline.setVisible(visible);
			bikeLane.add(polyline);
		}
	}
	
	private void toggleBikeLane(boolean visible) {
		for (Polyline polyline : bikeLane) {
			polyline.setVisible(visible);
		}
	}
}
