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

package com.poguico.palmabici.widgets;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.views.MapView;

import com.poguico.palmabici.R;
import com.poguico.palmabici.SynchronizableElement;
import com.poguico.palmabici.network.synchronizer.NetworkStationAlarm;
import com.poguico.palmabici.network.synchronizer.NetworkSynchronizer;
import com.poguico.palmabici.synchronizers.LocationSynchronizer;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;

public class StationInfoWidget extends DefaultInfoWindow implements SynchronizableElement {

	private NetworkInformation   networkInformation;
	private NetworkSynchronizer  networkSynchronizer;
	private LocationSynchronizer locationSynchronizer;
	private Context              context;
	private ExtendedOverlayItem  eItem;
	private Station              station;
	private ImageButton          alarmButton;
	private boolean             active;
	
	public StationInfoWidget (MapView mapView, SynchronizableElement parentActivity) {
		super(R.layout.station_info, mapView);
		context = parentActivity.getSynchronizableActivity().getApplicationContext();
		networkInformation = NetworkInformation.getInstance(context);
		locationSynchronizer = LocationSynchronizer.getInstance(parentActivity);
		networkSynchronizer  = NetworkSynchronizer.getInstance(context);
		alarmButton = (ImageButton)mView.findViewById(R.id.alarmButton);
	}
	
	@Override
	public void onOpen(Object item) {
		//super.onOpen(item);
		eItem = (ExtendedOverlayItem)item;

		int    freeBikes, freeSlots;
		LinearLayout.LayoutParams layoutParams;
		String formattedDistance = "";
		
		float[] distance           = new float[1];
		Location myLocation         = locationSynchronizer.getLocation();
		station = networkInformation.get(eItem.getDescription());
		
		TextView title =
				(TextView)mView.findViewById(R.id.markerTitle);
		TextView tvFreeBikes =
				(TextView)mView.findViewById(R.id.freeBikes);
		TextView tvFreeSlots =
				(TextView)mView.findViewById(R.id.freeSlots);
		LinearLayout lyBrokenApparel =
				(LinearLayout)mView.findViewById(R.id.brokenApparel);
		
		freeBikes   = station.getBusySlots();
		freeSlots   = station.getFreeSlots();
		
		if (myLocation != null) {
			Location.distanceBetween(station.getLat(),
					 station.getLong(),
					 myLocation.getLatitude(),
					 myLocation.getLongitude(), distance);
			
			formattedDistance += " ("
			                   +  Formatter.formatDistance(distance[0], context)
			                   +  ")";
		}
		
		title.setText(eItem.getTitle() + formattedDistance);
		tvFreeBikes.setText(String.valueOf(freeBikes));
		tvFreeSlots.setText(String.valueOf(freeSlots));
		
		lyBrokenApparel.setLayoutParams(
		    new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,0));
		
		if (station.getBusySlots() == 0 &&
				station.getSlots() != station.getBrokenSlots()) {
			layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(5, 5, 5, 5);
			alarmButton.setLayoutParams(layoutParams);
			
			if (networkSynchronizer.hasAlarm(station.getNEstacio())) {
				alarmButton.setImageResource(R.drawable.bell_active);
				active = true;
			} else {
				alarmButton.setImageResource(R.drawable.bell);
				active = false;
			}
		} else {
			layoutParams = new LinearLayout.LayoutParams(0,0);
			layoutParams.setMargins(0, 0, 0, 0);
			alarmButton.setLayoutParams(layoutParams);
		}
		alarmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleAlarm();	
				String filename  = "marker0";
				filename +=  (station.getBrokenBikes() > 0 ||
						      station.getBrokenSlots() > 0) ?
						    	"_alert" : "";
				filename +=  (NetworkStationAlarm.hasAlarm(station.getNEstacio())) ?
					    	"_alarm" : "";
				try {
					eItem.setMarker(context.getResources().getDrawable(
							R.drawable.class.getDeclaredField(filename).getInt(null)));
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		locationSynchronizer.addSynchronizableElement(this);
	}
	
	@Override
	public void onClose() {
		locationSynchronizer.detachSynchronizableElement(this);
		super.onClose();
	}

	@Override
	public void onSuccessfulNetworkSynchronization() {}

	@Override
	public void onUnsuccessfulNetworkSynchronization() {}

	@Override
	public void onLocationSynchronization() {
		float[] distance           = new float[1];
		Location my_location        = locationSynchronizer.getLocation();
		Station  station = networkInformation.get(eItem.getDescription());
		
		Location.distanceBetween(station.getLat(),
				 station.getLong(),
				 my_location.getLatitude(),
				 my_location.getLongitude(), distance);
		
		String formatted_distance = " (" + 
				 Formatter.formatDistance(distance[0], context) +
				 ")";
		
		TextView title =
				(TextView)mView.findViewById(R.id.markerTitle);
		title.setText(station.getName() + formatted_distance);
	}

	@Override
	public FragmentActivity getSynchronizableActivity() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void toggleAlarm() {
		if (active) {
			networkSynchronizer.removeAlarm(station);
			active = false;
			alarmButton.setImageResource(R.drawable.bell);
		} else {
			networkSynchronizer.addAlarm(station);
			active = true;
			alarmButton.setImageResource(R.drawable.bell_active);
		}
	}
}
