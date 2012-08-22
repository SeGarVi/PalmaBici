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

public class Station implements Comparable <Station> {
	private int id, free_slots, busy_slots;
	private String n_estacio, name;
	private long station_long, station_lat;
	private boolean favourite;
	
	public Station(int id, String n_estacio, String name, long station_long, long station_lat,
				   int free_slots, int busy_slots, boolean favourite) {
		this.id = id;
		this.n_estacio = n_estacio;
		this.free_slots = free_slots;
		this.busy_slots = busy_slots;
		this.name = name;
		this.station_long = station_long;
		this.station_lat  = station_lat;
		this.favourite = favourite;
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

	public long getStation_long() {
		return station_long;
	}

	public void setStation_long(long station_long) {
		this.station_long = station_long;
	}

	public long getStation_lat() {
		return station_lat;
	}

	public void setStation_lat(long station_lat) {
		this.station_lat = station_lat;
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
	
	@Override
	public int compareTo(Station altra) {
		int res;
		
		Integer inter_n = Integer.valueOf(this.n_estacio);
		Integer exter_n = Integer.valueOf(altra.n_estacio);
		Boolean inter_f = this.favourite;
		Boolean exter_f = altra.favourite;
		
		if ((this.favourite && altra.favourite) ||
			(!this.favourite && !altra.favourite))
			res = inter_n.compareTo(exter_n);
		else
			res = exter_f.compareTo(inter_f);
		
		return res;
	}
}
