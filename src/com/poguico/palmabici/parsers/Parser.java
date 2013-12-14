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

package com.poguico.palmabici.parsers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.poguico.palmabici.util.Station;



import android.content.Context;

public class Parser {
	
	public static ArrayList <Station> parseNetworkJSON (Context context,
														String  data)	{
		ArrayList <Station> stations = new ArrayList<Station>();
		JSONArray json_array;
		JSONObject json_object;
		Long lngAcum=0L, latAcum=0L;
		
		try {
			json_array = new JSONArray(data);
			
			for (int i = 0; i < json_array.length(); i++) {
				json_object = json_array.getJSONObject(i);
				
				lngAcum += json_object.getLong("lng");
				latAcum += json_object.getLong("lat");
				
				stations.add(new Station(context,
										 json_object.getInt("id"),
										 json_object.getString("name").substring(1, 3),
										 json_object.getString("name").substring(5),
										 json_object.getDouble("lng") / 1e6,
										 json_object.getDouble("lat") / 1e6,
										 json_object.getInt("free"),
										 json_object.getInt("bikes"),
										 json_object.getInt("free_fck"),
										 json_object.getInt("bikes_fck"),
										 false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stations;
	}
}