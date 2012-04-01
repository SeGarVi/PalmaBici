package com.poguico.palmabici;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {
	private static JSONParser parser = null;
	
	public static JSONParser getInstance() {
		if (parser == null)
			parser = new JSONParser();
		
		return parser;
	}
	
	private JSONParser () {
		
	}
	
	public ArrayList <Station> parse (String data) {
		ArrayList <Station> stations = new ArrayList<Station>();
		JSONArray json_array;
		JSONObject json_object;
		
		try {
			json_array = new JSONArray(data);
			
			for (int i = 0; i < json_array.length(); i++) {
				json_object = json_array.getJSONObject(i);
				
				stations.add(new Station(json_object.getInt("id"),
										 json_object.getString("name"),
										 json_object.getLong("long"),
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
	
	//TODO update only number of bikes
	public ArrayList <Station> parseUpdate (String data) {
		return null;
	}
}