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

import java.util.ArrayList;

import com.poguico.palmabici.util.Station;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
	
	private static final int    DB_VERSION = 4;
	private static final String DB_NAME    = "palmabici";
	private static final String STATION_TABLE_NAME     = "station";
	private static final String LAST_UPDATE_TABLE_NAME = "last_update";
	private static final String FAVORITES_TABLE_NAME   = "favorites";
	
	private static final String STATION_TABLE_CREATE =
            "CREATE TABLE \"" + STATION_TABLE_NAME + "\" ("
            		+ "id           INTEGER, "
            		+ "n_estacio    TEXT, "
            		+ "name         TEXT, "
            		+ "station_long DECIMAL(2,6), "
            		+ "station_lat  DECIMAL(2,6), "
            		+ "free_slots   INTEGER, "
            		+ "busy_slots   INTEGER, "
            		+ "broken_slots INTEGER, "
            		+ "broken_bikes INTEGER"
            		+ ");";
	private static final String STATION_TABLE_V4UPDATE =
            "ALTER TABLE \"" + STATION_TABLE_NAME + "\" "
            		+ "ADD broken_slots   INTEGER DEFAULT 0, "
            		+ "ADD broken_bikes   INTEGER DEFAULT 0"
            		+ ";";
	private static final String LAST_UPDATE_TABLE_CREATE =
            "CREATE TABLE \"" + LAST_UPDATE_TABLE_NAME + "\" (time INTEGER);";
	private static final String FAVORITES_TABLE_CREATE =
            "CREATE TABLE \"" + FAVORITES_TABLE_NAME + "\" (id INTEGER, " +
            "nestacio TEXT);";
	
	private static final String GET_STATIONS =
			"SELECT * FROM \"" + STATION_TABLE_NAME + "\"";
	private static final String GET_LAST_UPDATE_TIME =
			"SELECT * FROM \"" + LAST_UPDATE_TABLE_NAME + "\"";
	private static final String GET_FAVOURITE_STATIONS =
			"SELECT * FROM \"" + FAVORITES_TABLE_NAME + "\"";
	
	private static final String DELETE_STATIONS =
			"DELETE FROM \"" + STATION_TABLE_NAME + "\"";
	private static final String DELETE_LAST_UPDATE_TIME =
			"DELETE FROM \"" + LAST_UPDATE_TABLE_NAME + "\"";
	
	
	private static DatabaseManager instance;
	
	private DatabaseManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(STATION_TABLE_CREATE);
		db.execSQL(LAST_UPDATE_TABLE_CREATE);
		db.execSQL(FAVORITES_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 2 ) {
			db.execSQL(STATION_TABLE_CREATE);
			db.execSQL(LAST_UPDATE_TABLE_CREATE);
		} else if (oldVersion == 3 ) {
			db.execSQL(STATION_TABLE_V4UPDATE);
		}
	}

	public static DatabaseManager getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseManager(context);
		}
		
		return instance;
	}
	
	public ArrayList<String> getFavouriteStations () {
		SQLiteDatabase db;
		Cursor c;
		int n_estacio_col;
		ArrayList<String> res = new ArrayList<String>();
		
		db = instance.getReadableDatabase();
		c = db.rawQuery(GET_FAVOURITE_STATIONS, null);
		n_estacio_col = c.getColumnIndex("nestacio");
		
		if (c.moveToFirst()) {
			do {
				res.add(c.getString(n_estacio_col));
			} while(c.moveToNext());
		}
		
		c.close();		
		if (db != null)
		    db.close();
		
		return res;
	}
	
	public ArrayList <Station> getLastStationNetworkState (Context context) {
		ArrayList<Station> stationList = new ArrayList<Station>();
		SQLiteDatabase db;
		Cursor c;
		int id_col, n_estacio_col, name_col, station_long_col, station_lat_col,
		    free_slots_col,  busy_slots_col, broken_slots_col, broken_bikes_col;
		
		db = instance.getReadableDatabase();
		c  = db.rawQuery(GET_STATIONS, null);
		
		id_col           = c.getColumnIndex("id");
		n_estacio_col    = c.getColumnIndex("n_estacio");
		name_col         = c.getColumnIndex("name");
		station_long_col = c.getColumnIndex("station_long");
		station_lat_col  = c.getColumnIndex("station_lat");
		free_slots_col   = c.getColumnIndex("free_slots");
		busy_slots_col   = c.getColumnIndex("busy_slots");
		broken_slots_col = c.getColumnIndex("broken_slots");
		broken_bikes_col = c.getColumnIndex("broken_bikes");
		
		if (c.moveToFirst()) {
			do {
				stationList.add(new Station(context,
											c.getInt(id_col), 
											c.getString(n_estacio_col),
											c.getString(name_col),
											c.getDouble(station_long_col),
											c.getDouble(station_lat_col),
											c.getInt(free_slots_col),
											c.getInt(busy_slots_col),
											c.getInt(broken_slots_col),
											c.getInt(broken_bikes_col),
											false));
			} while(c.moveToNext());
		}
		
		c.close();		
		if (db != null)
		    db.close();
		
		return stationList;
	}
	
	public void saveLastStationNetworkState (ArrayList <Station> stations) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues(7);
		
		db = instance.getWritableDatabase();
		
		db.execSQL(DELETE_STATIONS);
		for (Station station : stations) {
			values.put("id", station.getId());
			values.put("n_estacio", station.getN_estacio());
			values.put("name", station.getName());
			values.put("station_long", station.getLong());
			values.put("station_lat", station.getLat());
			values.put("free_slots", station.getFree_slots());
			values.put("busy_slots", station.getBusy_slots());
			values.put("broken_slots", station.getBroken_slots());
			values.put("broken_bikes", station.getBroken_bikes());
			db.insert(STATION_TABLE_NAME, null, values);
		}
		
		if (db != null)
		    db.close();
	}
	
	public long getLastUpdateTime () {
		long res = 0;
		SQLiteDatabase db;
		Cursor c;
		int time_col;
		
		db = instance.getReadableDatabase();
		c = db.rawQuery(GET_LAST_UPDATE_TIME, null);
		time_col = c.getColumnIndex("time");
		
		if (c.moveToFirst()) {
			res = c.getLong(time_col);
		}
		
		c.close();		
		if (db != null)
		    db.close();
		
		return res;
	}
	
	public void saveLastUpdateTime (long time) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues(1);
		
		db = instance.getWritableDatabase();
		
		db.execSQL(DELETE_LAST_UPDATE_TIME);
		values.put("time", time);
		db.insert(LAST_UPDATE_TABLE_NAME, null, values);
	}
	
	public void saveFavouriteStations (ArrayList <Station> stations) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues(2);
		
		db = instance.getWritableDatabase();
		
		for (Station station : stations) {
			if (station.isFavourite()) {
				values.put("id", station.getId());
				values.put("nestacio", station.getN_estacio());
				db.insert(FAVORITES_TABLE_NAME, null, values);
			} else {
				db.delete(FAVORITES_TABLE_NAME,"id='"+ station.getId() +"'", null);
			}
		}
		
		if (db != null)
		    db.close();
	}
}
