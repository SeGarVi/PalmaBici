package com.poguico.palmabici;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {
	
	public static ArrayList <Station> parse (String data) {
		ArrayList <Station> stations = new ArrayList<Station>();
		JSONArray json_array;
		JSONObject json_object;
		
		try {
			json_array = new JSONArray(data);
			
			for (int i = 0; i < json_array.length(); i++) {
				json_object = json_array.getJSONObject(i);
				
				stations.add(new Station(json_object.getInt("id"),
										 json_object.getString("name").substring(1, 3),
										 json_object.getString("name").substring(5),
										 json_object.getLong("lng"),
										 json_object.getLong("lat"),
										 json_object.getInt("free"),
										 json_object.getInt("bikes"),
										 false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stations;
	}
}