/*
* Copyright 2012 Sergio Garcia Villalonga (yayalose@gmail.com)
*
* This file is part of PalmaBici.
*
* PalmaBici is free software: you can redistribute it and/or modify
* it under the terms of the Affero GNU General Public License version 3
* as published by the Free Software Foundation.
*
* PalmaBici is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* Affero GNU General Public License for more details
* (https://www.gnu.org/licenses/agpl-3.0.html).
*
*/

package com.poguico.palmabici.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osmdroid.util.GeoPoint;

import android.location.Location;
import android.location.LocationManager;

public class Station implements Comparable <Station> {
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
    private static final Map<String, GeoPoint> correct_locations;
    static {
        Map<String, GeoPoint> aMap = new HashMap<String, GeoPoint>();
        aMap.put("01", new GeoPoint(39.566129, 2.659499));
	    aMap.put("06", new GeoPoint(39.571255, 2.665796));
	    aMap.put("07", new GeoPoint(39.572946, 2.657238));
	    aMap.put("09", new GeoPoint(39.574468, 2.663871));
	    aMap.put("13", new GeoPoint(39.575682, 2.654789));
	    aMap.put("15", new GeoPoint(39.580040, 2.660169));
	    aMap.put("16", new GeoPoint(39.581745, 2.644123));
	    aMap.put("17", new GeoPoint(39.578755, 2.662571));
	    aMap.put("21", new GeoPoint(39.581119, 2.655465));
	    aMap.put("24", new GeoPoint(39.578097, 2.651191));
	    aMap.put("25", new GeoPoint(39.580656, 2.649190));
	    aMap.put("27", new GeoPoint(39.584213, 2.649210));
	    aMap.put("29", new GeoPoint(39.577311, 2.646439));
	    aMap.put("31", new GeoPoint(39.577507, 2.640839));
	    aMap.put("37", new GeoPoint(39.574400, 2.640703));
	    aMap.put("41", new GeoPoint(39.572710, 2.637534));
	    aMap.put("45", new GeoPoint(39.572505, 2.643112));
	    aMap.put("46", new GeoPoint(39.571035, 2.647043));
	    aMap.put("47", new GeoPoint(39.571295, 2.641806));
	    aMap.put("49", new GeoPoint(39.568560, 2.646257));
	    aMap.put("51", new GeoPoint(39.575189, 2.647993));
	    aMap.put("52", new GeoPoint(39.576453, 2.650509));
	    aMap.put("55", new GeoPoint(39.569171, 2.650747));
	    aMap.put("56", new GeoPoint(39.571149, 2.648555));
	    aMap.put("59", new GeoPoint(39.567688, 2.656039));
	    aMap.put("60", new GeoPoint(39.570273, 2.655827));
	    aMap.put("63", new GeoPoint(39.575190, 2.654070));
	    aMap.put("65", new GeoPoint(39.572426, 2.655438));
	    correct_locations = Collections.unmodifiableMap(aMap);
    }
    
    public Station(int id, String n_estacio, String name, double station_long,
    		        double station_lat, int free_slots, int busy_slots,
    		        int broken_slots, int broken_bikes, boolean favourite) {
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
            location.setLatitude(correct_locations.get(n_estacio).getLatitude());
            location.setLongitude(correct_locations.get(n_estacio).getLongitude());
        } else {
            location.setLatitude(station_lat);
            location.setLongitude(station_long);
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
            bearing = userLocation.bearingTo(location);
        } else {
            distance = (float)-1;
        }
    }
    
    @Override
    public int compareTo(Station altra) {
        Boolean inter_f = this.favourite;
        Boolean exter_f = altra.favourite;
        
        return exter_f.compareTo(inter_f);
    }
}