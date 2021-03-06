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

import java.util.ArrayList;
import java.util.HashMap;

import org.osmdroid.util.GeoPoint;

import android.content.Context;

import com.poguico.palmabici.DatabaseManager;
import com.poguico.palmabici.network.synchronizer.NetworkSynchronizer;

public class NetworkInformation {
    private static NetworkInformation instance = null;
    
    private GeoPoint                 center;
    private ArrayList <Station>      network;
    private HashMap<Integer, Station> mappedNetwork;
    private ArrayList <Integer>       favourites;
    private long                     lastUpdateTime;
    private NetworkSynchronizer      networkSynchronizer;
    
    private NetworkInformation (Context context) {
    	DatabaseManager dbManager = DatabaseManager.getInstance(context);
    	
    	center         = new GeoPoint(39.574689, 2.651332);
        favourites     = dbManager.getFavouriteStations();
        lastUpdateTime = dbManager.getLastUpdateTime();
        mappedNetwork  = new HashMap<Integer, Station>();
        setNetwork(dbManager.getLastStationNetworkState(context));
        
        networkSynchronizer = NetworkSynchronizer.getInstance(context);
        for (Station station : network) {
        	if (station.hasAlarm()) {
        		networkSynchronizer.addAlarm(station);
        	}
        }
    }
    
    public static synchronized NetworkInformation getInstance (Context context) {
        if (instance == null) {
            instance = new NetworkInformation(context);
        }
        return instance;
    }
    
    public GeoPoint getCenter() {
        return center;
    }

    public synchronized void setCenter(GeoPoint center) {
        this.center = center;
    }

    public ArrayList<Integer> getFavourites() {
        return favourites;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public synchronized void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public synchronized void setNetwork(ArrayList<Station> network) {
        this.network = network;
        for (Station station : network) {
            mappedNetwork.put(station.getId(), station);
        }
    }

    public ArrayList <Station> getNetwork() {
        return network;
    }
    
    public synchronized void setFavourite(Integer id) {
        favourites.add(id);
    }
    
    public synchronized void unSetFavourite(Integer id) {
        favourites.remove(id);
    }
    
    public synchronized void setFavourites(ArrayList<Integer> favourites) {
        this.favourites = favourites;
        for (Station station : network) {
            if (favourites.contains(station.getId())) {
                station.changeFavouriteState();
            }
        }
    }
    
    public boolean isFavourite(Integer id) {
        return favourites.contains(id);
    }
    
    public Station get(Integer id) {
        return mappedNetwork.get(id);
    }
    
    public int getNumberStations() {
        return (network == null)? 0 : network.size();
    }
}
