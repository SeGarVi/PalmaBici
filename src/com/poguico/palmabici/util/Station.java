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
    private Integer id;
    private int freeSlots, busySlots, slots;
    private String name;
    private Location location;
    private Float distance;
    private boolean favourite;
    private boolean hasAlarm;
    private float bearing;
    
    /*
     * Ugly solution to keep nice names
     * (change it to a properties file)
     */
    private static final Map<Integer, String> niceNames;
    static {
        Map<Integer, String> aMap = new HashMap<Integer, String>();
    aMap.put(0, "Parc de ses Veles");
    aMap.put(1, "Manacor - Manuel Azaña");
    aMap.put(2, "Mateu Enric Lladó");
    aMap.put(3, "Travessera Ballester");
    aMap.put(4, "F. Manuel de los Herreros");
    aMap.put(5, "Aragó - Nuredduna");
    aMap.put(6, "Aragó - J. Balmes");
    aMap.put(7, "Pl. Alexander Flemimg");
    aMap.put(8, "Pl. Madrid");
    aMap.put(9, "Av. Argentina");
    aMap.put(10, "Cecili Metel");
    aMap.put(11, "Institut Balear");
    aMap.put(12, "Pl. Rei Joan Carles I");
    aMap.put(13, "Via Roma");
    aMap.put(14, "Fàbrica");
    aMap.put(15, "Jaume III");
    aMap.put(16, "Pl. Porta de Santa Catalina");
    aMap.put(17, "Pl. de la Reina");
    aMap.put(18, "Pl. Santa Eulàlia");
    aMap.put(19, "Parc de les Estacions");
    aMap.put(20, "J. Verdaguer - J. Balmes");
    aMap.put(21, "Pl. d'Espanya");
    aMap.put(22, "Pl. Alexandre Jaume");
    aMap.put(23, "Parc de sa Riera");
    aMap.put(24, "Blanquerna - C. de Sallent");
    aMap.put(25, "Pl. París");
    aMap.put(26, "Blanquerna - Bartolomé");
    aMap.put(27, "Metge Darder");
    aMap.put(28, "Sant Ferran");
    //aMap.put("56", "Pl. del Mercat");
    aMap.put(29, "Pl. del Pont");
    aMap.put(30, "Ctra. de Valldemossa");
    aMap.put(31, "Son Costa - Son Fortesa");
    niceNames = Collections.unmodifiableMap(aMap);
    }
    
    /*
     * Ugly solution to keep correct locations
     * (change it to a properties file)
     */
    private static final Map<Integer, GeoPoint> correctLocations;
    static {
        Map<Integer, GeoPoint> aMap = new HashMap<Integer, GeoPoint>();
            aMap.put(0, new GeoPoint(39.566129, 2.659499));
	    aMap.put(1, new GeoPoint(39.571255, 2.665796));
	    aMap.put(5, new GeoPoint(39.572946, 2.657238));
	    aMap.put(4, new GeoPoint(39.574468, 2.663871));
	    aMap.put(19, new GeoPoint(39.575682, 2.654789));
	    aMap.put(20, new GeoPoint(39.580040, 2.660169));
	    aMap.put(23, new GeoPoint(39.581745, 2.644123));
	    aMap.put(6, new GeoPoint(39.578755, 2.662571));
	    aMap.put(7, new GeoPoint(39.581119, 2.655465));
	    aMap.put(24, new GeoPoint(39.578097, 2.651191));
	    aMap.put(26, new GeoPoint(39.580656, 2.649190));
	    aMap.put(25, new GeoPoint(39.584213, 2.649210));
	    aMap.put(11, new GeoPoint(39.577311, 2.646439));
	    aMap.put(8, new GeoPoint(39.577507, 2.640839));
	    aMap.put(9, new GeoPoint(39.574400, 2.640703));
	    aMap.put(14, new GeoPoint(39.572710, 2.637534));
	    aMap.put(15, new GeoPoint(39.572505, 2.643112));
	    aMap.put(12, new GeoPoint(39.571035, 2.647043));
	    aMap.put(16, new GeoPoint(39.571295, 2.641806));
	    aMap.put(17, new GeoPoint(39.568560, 2.646257));
	    aMap.put(13, new GeoPoint(39.575189, 2.647993));
	    aMap.put(10, new GeoPoint(39.576453, 2.650509));
	    aMap.put(18, new GeoPoint(39.569171, 2.650747));
	    //aMap.put("56", new GeoPoint(39.571149, 2.648555));
	    aMap.put(2, new GeoPoint(39.567688, 2.656039));
	    aMap.put(3, new GeoPoint(39.570273, 2.655827));
	    aMap.put(21, new GeoPoint(39.575190, 2.654070));
	    aMap.put(22, new GeoPoint(39.572426, 2.655438));
	    correctLocations = Collections.unmodifiableMap(aMap);
    }
    
    public Station(Integer id, String name, double stationLong, double stationLat,
    		        int freeSlots, int busySlots, boolean favourite,
    		        boolean hasAlarm) {
        this.id = id;
        this.freeSlots = freeSlots;
        this.busySlots = busySlots;
        this.slots = freeSlots + busySlots;
        this.name = niceNames.get(id);
        this.favourite = favourite;
        this.hasAlarm = hasAlarm;
                
        location = new Location(LocationManager.NETWORK_PROVIDER);
        
        if (correctLocations.get(id) != null) {
            location.setLatitude(correctLocations.get(id).getLatitude());
            location.setLongitude(correctLocations.get(id).getLongitude());
        } else {
            location.setLatitude(stationLat);
            location.setLongitude(stationLong);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(int freeSlots) {
        this.freeSlots = freeSlots;
    }

    public int getBusySlots() {
        return busySlots;
    }

    public void setBusySlots(int busySlots) {
        this.busySlots = busySlots;
    }

    public int getSlots() {
        return slots;
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
        Boolean interF = this.favourite;
        Boolean exterF = altra.favourite;
        
        return exterF.compareTo(interF);
    }

	public boolean hasAlarm() {
		return hasAlarm;
	}
}
