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

import java.util.Calendar;

import com.poguico.palmabici.syncronizers.LocationSynchronizer;
import com.poguico.palmabici.syncronizers.NetworkSynchronizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StationListActivity extends ActionBarActivity {
	private static final long update_time = 600000;
	
	private ProgressDialog dialog;
	private NetworkSynchronizer synchronizer;
	private StationList station_list;
	private String list_ordering;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        SharedPreferences conf=PreferenceManager
				.getDefaultSharedPreferences(this);
        
        LocationSynchronizer.addSynchronizableActivity(this);
        synchronizer = NetworkSynchronizer.getInstance();
        station_list = new StationList(this, NetworkInformation.getNetwork());        
        LinearLayout main_layout_panel = (LinearLayout) findViewById(R.id.main_layout_panel);        
        main_layout_panel.addView(station_list);
        list_ordering = conf.getString("list_order", "distance");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;

            case R.id.menu_refresh:
            	dialog = ProgressDialog.show(this, "", getString(R.string.refresh_ongoing), true);
            	synchronizer.new SynchronizeTask(this).execute((Void [])null);
                break;

            case R.id.menu_credits:
            	new CreditsDialog(this).show();
                break;

            case R.id.menu_preferences:
            	Intent preferences_activity = new Intent(this, PreferencesActivity.class);
            	this.startActivity(preferences_activity);
                break;
                
            case R.id.menu_report:
                Intent issue_intent = new Intent(Intent.ACTION_VIEW);
                issue_intent.setData(Uri.parse("https://github.com/SeGarVi/PalmaBici/issues/new"));
                startActivity(issue_intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onRestart() {
		super.onRestart();
		
		SharedPreferences conf=PreferenceManager
				.getDefaultSharedPreferences(this);

		long now = Calendar.getInstance().getTimeInMillis();
		
		if (conf.getBoolean("autoupdate", true) &&
				(now - synchronizer.getLastUpdate()) > update_time) {
			dialog = ProgressDialog.show(this, "",getString(R.string.refresh_ongoing), true);
			synchronizer.new SynchronizeTask(this).execute((Void [])null);
		}
		
		if (!conf.getString("list_order", "distance").equals(list_ordering)) {
			list_ordering = conf.getString("list_order", "distance");
			station_list.refresh();
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		DatabaseManager.saveFavouriteStations(NetworkInformation.getNetwork());
	}
	
	@Override
	public void onSuccessfulNetworkSynchronization() {
		if (dialog != null)
    		dialog.hide();
    	
    	Toast.makeText(this, R.string.refresh_succesful, Toast.LENGTH_SHORT).show();
    	StationList station_list = new StationList(this, NetworkInformation.getNetwork());        
    	LinearLayout main_layout_panel = (LinearLayout) findViewById(R.id.main_layout_panel);        
        main_layout_panel.addView(station_list);
	}

	@Override
	public void onUnsuccessfulNetworkSynchronization() {
		Toast.makeText(this, R.string.connectivity_error, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationSynchronization() {
		station_list.refresh();
	}
}
