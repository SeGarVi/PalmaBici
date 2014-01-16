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

package com.poguico.palmabici.synchronizers;

import java.util.ArrayList;

import com.poguico.palmabici.SynchronizableElement;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationSynchronizer {

	private static Sensor              sensor;
	private static SensorManager 	     manager;
	private static SensorEventListener listener;
	
	private static float orientation;
	
	private static ArrayList<SynchronizableElement> synchronizableElements;
	
	public static void init (Context context) {
		manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensor  = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		listener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				orientation = event.values[0];
				updateViews();
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		};
		
		manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		
		synchronizableElements = new ArrayList<SynchronizableElement>();
	}
	
	private static void updateViews () {
		for (SynchronizableElement activity : synchronizableElements) {
			activity.onLocationSynchronization();
		}
	}
	
	public static void addSynchronizableActivity(SynchronizableElement activity) {
		synchronizableElements.add(activity);
	}
	
	public static void detachSynchronizableActivity(SynchronizableElement activity) {
		synchronizableElements.remove(activity);
	}	
	
	public static float getOrientation () {
		return orientation;
	}
}
