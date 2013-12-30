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

import android.app.Activity;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.views.MapView;

import com.poguico.palmabici.R;
import com.poguico.palmabici.SynchronizableActivity;
import com.poguico.palmabici.synchronizers.LocationSynchronizer;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;

public class StationInfoWidget extends DefaultInfoWindow implements SynchronizableActivity {

	private NetworkInformation     networkInformation;
	private SynchronizableActivity parentActivity;
	private LocationSynchronizer   locationSynchronizer;
	private ExtendedOverlayItem    eItem;
	private Station station;
	
	public StationInfoWidget (MapView mapView, SynchronizableActivity parentActivity) {
		super(R.layout.station_info, mapView);
		networkInformation = NetworkInformation.getInstance();
		this.parentActivity = parentActivity;
		locationSynchronizer = LocationSynchronizer.getInstance(parentActivity);
	}
	
	@Override
	public void onOpen(Object item) {
		//super.onOpen(item);
		eItem = (ExtendedOverlayItem)item;
		
		int    freeBikes, freeSlots, brokenBikes, brokenSlots;
		station = networkInformation.get(eItem.getDescription());
		
		float[] distance           = new float[1];
		Location my_location        = locationSynchronizer.getLocation();
		Station  station = networkInformation.get(eItem.getDescription());
		
		Location.distanceBetween(station.getLat(),
				 station.getLong(),
				 my_location.getLatitude(),
				 my_location.getLongitude(), distance);
		
		String formatted_distance = " (" + 
				 Formatter.formatDistance(distance[0],
						 parentActivity.getSynchronizableActivity()) +
				 ")";
		
		
		TextView title =
				(TextView)mView.findViewById(R.id.markerTitle);
		TextView tvFreeBikes =
				(TextView)mView.findViewById(R.id.freeBikes);
		TextView tvFreeSlots =
				(TextView)mView.findViewById(R.id.freeSlots);
		LinearLayout lyBrokenApparel =
				(LinearLayout)mView.findViewById(R.id.brokenApparel);
		
		freeBikes   = station.getBusy_slots();
		freeSlots   = station.getFree_slots();
		brokenBikes = station.getBroken_bikes();
		brokenSlots = station.getBroken_slots();
		
		
		title.setText(eItem.getTitle() + formatted_distance);
		tvFreeBikes.setText(String.valueOf(freeBikes));
		tvFreeSlots.setText(String.valueOf(freeSlots));
		
		if (brokenBikes > 0 || brokenSlots > 0) {
			TextView tvBrokenBikes =
					(TextView)mView.findViewById(R.id.brokenBikes);
			TextView tvBrokenSlots =
					(TextView)mView.findViewById(R.id.brokenSlots);
			tvBrokenBikes.setText(String.valueOf(brokenBikes));
			tvBrokenSlots.setText(String.valueOf(brokenSlots));
			//lyBrokenApparel.setVisibility(View.VISIBLE);
			lyBrokenApparel.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
												  LayoutParams.WRAP_CONTENT));
		} else {
			//lyBrokenApparel.setVisibility(View.INVISIBLE);
			lyBrokenApparel.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,0));
		}
		locationSynchronizer.addSynchronizableActivity(this);
	}
	
	@Override
	public void onClose() {
		locationSynchronizer.detachSynchronizableActivity(this);
		super.onClose();
	}

	@Override
	public void onSuccessfulNetworkSynchronization() {
		
	}

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
				 Formatter.formatDistance(distance[0],
						 parentActivity.getSynchronizableActivity()) +
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
}
