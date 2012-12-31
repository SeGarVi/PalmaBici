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

import com.poguico.palmabici.DatabaseManager;
import com.poguico.palmabici.parsers.Parser;

public class NetworkInformation {
	private static ArrayList <Station> network;
	private static ArrayList <String>  favourites = null;
	
	public static void setNetwork(Context context, String stations) {
		if (favourites == null)
			favourites = DatabaseManager.getFavouriteStations();
		
		network = Parser.parseNetworkJSON(context, stations);
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
}
