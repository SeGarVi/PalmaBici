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

package com.poguico.palmabici;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.poguico.palmabici.syncronizers.LocationSynchronizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;

public class Station implements Comparable <Station> {
	private Context context;
	private int id, free_slots, busy_slots;
	private String n_estacio, name;
	private Location location;
	private Float distance;
	private boolean favourite;
	private float bearing;
	
	/*
	 * Ugly solution to keep nice names
	 */
	private static final Map<String, String> myMap;
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
        myMap = Collections.unmodifiableMap(aMap);
    }
	
	public Station(Context context, int id, String n_estacio, String name, long station_long, long station_lat,
				   int free_slots, int busy_slots, boolean favourite) {
		this.context = context;
		this.id = id;
		this.n_estacio = n_estacio;
		this.free_slots = free_slots;
		this.busy_slots = busy_slots;
		this.name = myMap.get(n_estacio);
		this.favourite = favourite;
		
		location = new Location(LocationManager.NETWORK_PROVIDER);
		location.setLatitude((double)station_lat / 1e6);
		location.setLongitude((double)station_long / 1e6);
		
		if (LocationSynchronizer.getLocation() != null) {
			distance = location.distanceTo(LocationSynchronizer.getLocation());
			bearing  = LocationSynchronizer.getLocation().bearingTo(location);
		} else {
			distance = (float)-1;
		}
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		
		if (favourite)
			NetworkInformation.setFavourite(n_estacio);
		else
			NetworkInformation.unSetFavourite(n_estacio);
	}
	
	public void updatePosition() {
		if (LocationSynchronizer.getLocation() != null) {
			distance = location.distanceTo(LocationSynchronizer.getLocation());
			bearing  = LocationSynchronizer.getLocation().bearingTo(location);
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
