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

package com.poguico.palmabici.parsers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.poguico.palmabici.network.synchronizer.NetworkStationAlarm;
import com.poguico.palmabici.util.Station;

public class Parser {
    
    public static ArrayList <Station> parseNetworkJSON (String data)    {
        ArrayList <Station> stations = new ArrayList<Station>();
        JSONArray jsonArray;
        JSONObject jsonObject;
        Long lngAcum=0L, latAcum=0L;
        int id;
        
        try {
            jsonArray = new JSONArray(data);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                
                lngAcum += jsonObject.getLong("lng");
                latAcum += jsonObject.getLong("lat");
                
                id = jsonObject.getInt("id");
                stations.add(new Station(id,
                                         jsonObject.getString("name"),
                                         jsonObject.getDouble("lng") / 1e6,
                                         jsonObject.getDouble("lat") / 1e6,
                                         jsonObject.getInt("free"),
                                         jsonObject.getInt("bikes"),
                                         false,
                                         NetworkStationAlarm.hasAlarm(id)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stations;
    }
}
