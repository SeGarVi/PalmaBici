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

import com.poguico.palmabici.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.IInterface;
import android.util.DisplayMetrics;

public class BikeLane {
	//private static ArrayList<ArrayList<GeoPoint>> paths = null;
	private static ArrayList<PathOverlay> osmPaths = null;
	
	private static void init (Activity activity, MapView mapView) {
		PathOverlay pathOverlay;
		osmPaths = new ArrayList<PathOverlay>();
		Resources res = activity.getResources();
		XmlResourceParser xpp = res.getXml(R.xml.carril_bici_palma);
		
		DisplayMetrics dpi = activity.getResources().getDisplayMetrics();
		float pathWidth = dpi.density*4;
		
		try {
		    xpp.next();
            int eventType = xpp.getEventType();
            

            while (eventType != XmlPullParser.END_DOCUMENT) {
            	if(eventType == XmlPullParser.START_TAG &&
            			xpp.getName().equals("coordinates")) {
            		eventType = xpp.next();

            		if(eventType == XmlPullParser.TEXT) {
            			String text = xpp.getText();

            			pathOverlay = new PathOverlay(0xffa0a0ff,
            					                      pathWidth,
            					                      mapView.getResourceProxy());
            			for (String coordinate : text.split("\n")) {
            				String[] coord_elements = coordinate.split(",");

                			pathOverlay.addPoint(
                				new GeoPoint(Double.valueOf(coord_elements[1]),
            						         Double.valueOf(coord_elements[0])));
            			}
            			
            			osmPaths.add(pathOverlay);
            		}
            	} else {
            		eventType = xpp.next();
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<PathOverlay> getOSMPaths(Activity activity, MapView mapView) {
		if (osmPaths == null) {
			init(activity, mapView);
		}
		
		return osmPaths;
	}
}
