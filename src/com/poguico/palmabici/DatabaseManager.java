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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
	
	private static final int    DB_VERSION = 2;
	private static final String DB_NAME    = "palmabici";
	private static final String FAVORITES_TABLE_NAME   = "favorites";
	private static final String FAVORITES_TABLE_CREATE =
            "CREATE TABLE \"" + FAVORITES_TABLE_NAME + "\" (id INTEGER, " +
            "nestacio TEXT);";
	
	private static final String GET_FAVOURITE_STATIONS =
			"SELECT * FROM \"" + FAVORITES_TABLE_NAME + "\""; 
	
	
	private static DatabaseManager instance;
	
	private DatabaseManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FAVORITES_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public static void initDB(Context context) {
		instance = new DatabaseManager(context);
	}
	
	public static ArrayList<String> getFavouriteStations () {
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
		
		if (db != null)
		    db.close();
		
		return res;
	}
	
	public static void saveFavouriteStations (ArrayList <Station> stations) {
		SQLiteDatabase db;
		
		db = instance.getWritableDatabase();
		
		for (Station station : stations) {
			if (station.isFavourite())
				db.execSQL("INSERT INTO \"" + FAVORITES_TABLE_NAME +
						   "\" VALUES ("+ station.getId() + ",'" +
						   station.getN_estacio() + "')");
			else
				db.delete(FAVORITES_TABLE_NAME,"id='"+ station.getId() +"'", null);
		}
		
		if (db != null)
		    db.close();
	}
}
