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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;



import android.content.Context;

public class Parser {
	
	public static ArrayList <Station> parseNetworkJSON (Context context,
														String  data)	{
		ArrayList <Station> stations = new ArrayList<Station>();
		JSONArray json_array;
		JSONObject json_object;
		
		try {
			json_array = new JSONArray(data);
			
			for (int i = 0; i < json_array.length(); i++) {
				json_object = json_array.getJSONObject(i);
				
				stations.add(new Station(context,
										 json_object.getInt("id"),
										 json_object.getString("name").substring(1, 3),
										 json_object.getString("name").substring(5),
										 json_object.getLong("lng"),
										 json_object.getLong("lat"),
										 json_object.getInt("free"),
										 json_object.getInt("bikes"),
										 NetworkInformation.isFavourite(json_object.getString("name").substring(1, 3))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stations;
	}
	
	public static String parseDistance(Float distance, Context context) {
		Locale current_local = context.getResources().getConfiguration().locale;
		NumberFormat format = NumberFormat.getNumberInstance(current_local);
		
		format.setMaximumFractionDigits(2);
		return format.format(distance);
	}
}