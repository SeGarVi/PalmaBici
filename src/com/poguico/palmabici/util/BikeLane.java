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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    Affero GNU General Public License for more details
 *    (https://www.gnu.org/licenses/agpl-3.0.html).
 *    
 */

package com.poguico.palmabici.util;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.poguico.palmabici.R;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;

public class BikeLane {
	private static ArrayList<PolylineOptions> paths;

	public static void init (Activity activity) {
		paths = new ArrayList<PolylineOptions>();
		Resources res = activity.getResources();
		XmlResourceParser xpp = res.getXml(R.xml.carril_bici_palma);
		try {
		    xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
            	if(eventType == XmlPullParser.START_TAG &&
            			xpp.getName().equals("coordinates")) {
            		eventType = xpp.next();

            		if(eventType == XmlPullParser.TEXT) {
            			String text = xpp.getText();

            			PolylineOptions path = new PolylineOptions();
            			for (String coordinate : text.split("\n")) {
            				String[] coord_elements = coordinate.split(",");
            				path.color(Color.parseColor("#ffa0a0ff"));
            				path.width(4);
            				path.add(new LatLng(Double.valueOf(coord_elements[1]),
            						            Double.valueOf(coord_elements[0])));
            			}
            			paths.add(path);
            		}
            	} else {
            		eventType = xpp.next();
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<PolylineOptions> getPaths() {
		return paths;
	}
}
