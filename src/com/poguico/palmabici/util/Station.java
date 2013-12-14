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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;

public class Station implements Comparable <Station> {
	private Context context;
	private int id, free_slots, busy_slots, broken_slots, broken_bikes, slots;
	private String n_estacio, name;
	private Location location;
	private Float distance;
	private boolean favourite;
	private float bearing;
	
	/*
	 * Ugly solution to keep nice names
	 * (change it to a properties file)
	 */
	private static final Map<String, String> nice_names;
    static {
    	Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("01", "Parc de ses Veles");
        aMap.put("06", "Manacor - Manuel Azaña");
        aMap.put("07", "Aragó - Nuredduna");
        aMap.put("09", "F. Manuel de los Herreros");
        aMap.put("13", "Parc de les Estacions");
        aMap.put("15", "J. Verdaguer - J. Balmes");
        aMap.put("16", "Parc de sa Riera");
        aMap.put("17", "Aragó - J. Balmes");
        aMap.put("21", "Pl. Alexander Flemimg");
        aMap.put("24", "Blanquerna - C. de Sallent");
        aMap.put("25", "Blanquerna - Bartolomé");
        aMap.put("27", "Pl. París");
        aMap.put("29", "Institut Balear");
        aMap.put("31", "Pl. Madrid");
        aMap.put("37", "Av. Argentina");
        aMap.put("41", "Fàbrica");
        aMap.put("45", "Jaume III");
        aMap.put("46", "Pl. Rei Joan Carles I");
        aMap.put("47", "Pl. Porta de Santa Catalina");
        aMap.put("49", "Pl. de la Reina");
        aMap.put("51", "Via Roma");
        aMap.put("52", "Cecili Metel");
        aMap.put("55", "Pl. Santa Eulàlia");
        aMap.put("56", "Pl. del Mercat");
        aMap.put("59", "Mateu Enric Lladó");
        aMap.put("60", "Travessera Ballester");
        aMap.put("63", "Pl. d'Espanya");
        aMap.put("65", "Pl. Alexandre Jaume");
        nice_names = Collections.unmodifiableMap(aMap);
    }
    
    /*
	 * Ugly solution to keep correct locations
	 * (change it to a properties file)
	 */
	private static final Map<String, LatLng> correct_locations;
    static {
    	Map<String, LatLng> aMap = new HashMap<String, LatLng>();
        aMap.put("27", new LatLng(39.584213, 2.649210));
        aMap.put("29", new LatLng(39.577311, 2.646439));
        aMap.put("51", new LatLng(39.575189, 2.647993)); 
        correct_locations = Collections.unmodifiableMap(aMap);
    }
    
	public Station(Context context, int id, String n_estacio,
					String name, double station_long, double station_lat,
				    int free_slots, int busy_slots, int broken_slots,
				    int broken_bikes, boolean favourite) {
		this.context = context;
		this.id = id;
		this.n_estacio = n_estacio;
		this.free_slots = free_slots;
		this.busy_slots = busy_slots;
		this.broken_slots = broken_slots;
		this.broken_bikes = broken_bikes;
		this.slots = free_slots + busy_slots + broken_slots;
		this.name = nice_names.get(n_estacio);
		this.favourite = favourite;
				
		location = new Location(LocationManager.NETWORK_PROVIDER);
		
		if (correct_locations.get(n_estacio) != null) { 
			location.setLatitude(correct_locations.get(n_estacio).latitude);
			location.setLongitude(correct_locations.get(n_estacio).longitude);
		} else {
			location.setLatitude(station_lat);
			location.setLongitude(station_long);
		}
		
		/*if (userLocation != null) {
			distance = location.distanceTo(userLocation);
			bearing  = userLocation.bearingTo(location);
		} else {
			distance = (float)-1;
		}*/
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getN_estacio() {
		return n_estacio;
	}

	public void setN_estacio(String n_estacio) {
		this.n_estacio = n_estacio;
	}

	public int getFree_slots() {
		return free_slots;
	}

	public void setFree_slots(int free_slots) {
		this.free_slots = free_slots;
	}

	public int getBusy_slots() {
		return busy_slots;
	}

	public void setBusy_slots(int busy_slots) {
		this.busy_slots = busy_slots;
	}

	public int getSlots() {
		return slots;
	}

	public int getBroken_slots() {
		return broken_slots;
	}
	
	public int getBroken_bikes() {
		return broken_bikes;
	}

	public String getName() {
		return name;
	}

	public Float getDistance () {
		return distance;
	}
	
	public float getBearing () {
		return bearing;
	}
	
	public boolean isFavourite() {
		return favourite;
	}

	public void changeFavouriteState() {
		favourite = !favourite;
		
		/*if (favourite)
			NetworkInformation.setFavourite(n_estacio);
		else
			NetworkInformation.unSetFavourite(n_estacio);*/
	}
	
	public double getLat() {
		return location.getLatitude();
	}
	
	public double getLong() {
		return location.getLongitude();
	}
	
	public void updatePosition(Location userLocation) {
		if (userLocation != null) {
			distance = location.distanceTo(userLocation);
			bearing  = userLocation.bearingTo(location);
		} else {
			distance = (float)-1;
		}
	}
	
	@Override
	public int compareTo(Station altra) {
		int res;
		
		SharedPreferences conf=PreferenceManager
				.getDefaultSharedPreferences(context);
		
		Integer inter_n = Integer.valueOf(this.n_estacio);
		Integer exter_n = Integer.valueOf(altra.n_estacio);
		Float inter_d = this.distance;
		Float exter_d = altra.distance;
		Boolean inter_f = this.favourite;
		Boolean exter_f = altra.favourite;
		
		if (conf.getString("list_order", "distance").equals("distance") ||
			(this.favourite && altra.favourite) ||
			(!this.favourite && !altra.favourite))
			if (distance >= 0)
				res = inter_d.compareTo(exter_d);
			else
				res = inter_n.compareTo(exter_n);
		else
			res = exter_f.compareTo(inter_f);
		
		return res;
	}
}
