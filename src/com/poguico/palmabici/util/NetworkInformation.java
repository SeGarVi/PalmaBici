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

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.poguico.palmabici.DatabaseManager;
import com.poguico.palmabici.parsers.Parser;

public class NetworkInformation {
	private static LatLng			     center     = new LatLng(39.574689, 2.651332);
	private static ArrayList <Station> network    = null;
	private static ArrayList <String>  favourites = null;
	private static long				 lastUpdateTime;
	
	public static void setNetwork(Context context,
									String  stations,
									long	lastUpdateTime) {
		if (favourites == null) {
			favourites = DatabaseManager.getInstance(context).getFavouriteStations();
		}
		
		network = Parser.parseNetworkJSON(context, stations);
		NetworkInformation.lastUpdateTime = lastUpdateTime;
	}
	
	public static void loadFromDB(Context context) {
		DatabaseManager dbManager = DatabaseManager.getInstance(context);
		network = dbManager.getLastStationNetworkState(context);
		lastUpdateTime = dbManager.getLastUpdateTime();
	}
	
	public static void storeToDB (Context context) {
		DatabaseManager dbManager = DatabaseManager.getInstance(context);
		dbManager.saveLastStationNetworkState(network);
		dbManager.saveLastUpdateTime(lastUpdateTime);
		dbManager.saveFavouriteStations(network);
	}
	
	public static ArrayList <Station> getNetwork() {
		return network;
	}
	
	public static void setFavourite(String id) {
		favourites.add(id);
	}
	
	public static void unSetFavourite(String id) {
		favourites.remove(id);
	}
	
	public static boolean isFavourite(String id) {
		return favourites.contains(id);
	}
	
	public static LatLng getNetworkCenter() {
		return center;
	}
	
	public static long getLastUpdate() {
		return lastUpdateTime;
	}
}
