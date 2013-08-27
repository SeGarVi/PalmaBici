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

package com.poguico.palmabici.util;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class NetworkInformation {
	private static NetworkInformation instance = null;
	
	private LatLng			     center;
	private ArrayList <Station> network;
	private ArrayList <String>  favourites;
	private long				 lastUpdateTime;
	
	private NetworkInformation () {
		center         = new LatLng(39.574689, 2.651332);
		network        = null;
		favourites     = null;
		lastUpdateTime = 0;
	}
	
	public static synchronized NetworkInformation getInstance () {
		if (instance == null) {
			instance = new NetworkInformation();
		}
		return instance;
	}
	
	public LatLng getCenter() {
		return center;
	}

	public synchronized void setCenter(LatLng center) {
		this.center = center;
	}

	public ArrayList<String> getFavourites() {
		return favourites;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public synchronized void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public synchronized void setNetwork(ArrayList<Station> network) {
		this.network = network;
	}

	public ArrayList <Station> getNetwork() {
		return network;
	}
	
	public synchronized void setFavourite(String id) {
		favourites.add(id);
	}
	
	public synchronized void unSetFavourite(String id) {
		favourites.remove(id);
	}
	
	public synchronized void setFavourites(ArrayList<String> favourites) {
		this.favourites = favourites;
		for (Station station : network) {
			if (favourites.contains(station.getN_estacio())) {
				station.changeFavouriteState();
			}
		}
	}
	
	public boolean isFavourite(String id) {
		return favourites.contains(id);
	}
}
