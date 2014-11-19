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

import com.poguico.palmabici.map.OpenStreetMapConstants;
import com.poguico.palmabici.map.StationMapFragment;
import com.poguico.palmabici.notification.NotificationManager;
import com.poguico.palmabici.navigationdrawer.NavigationDrawerFragment;
import com.poguico.palmabici.navigationdrawer.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.poguico.palmabici.network.synchronizer.NetworkSynchronizer;
import com.poguico.palmabici.network.synchronizer.NetworkSynchronizer.NetworkSynchronizationState;
import com.poguico.palmabici.synchronizers.LocationSynchronizer;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;
import com.poguico.palmabici.widgets.CreditsDialog;
import com.poguico.palmabici.widgets.FloatingActionButton;
import com.poguico.palmabici.widgets.FloatingActionButton.ButtonState;
import com.poguico.palmabici.widgets.NewFeaturesDialog;
import com.poguico.palmabici.widgets.StationInfoWidget.StationInfoWidgetListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends    ActionBarActivity
                          implements SynchronizableElement,
                          			  OpenStreetMapConstants,
                          			  NavigationDrawerCallbacks,
                          			StationInfoWidgetListener {
	
	private static final String EMAIL = "yayalose@gmail.com";
	private static final String PLAY_URL = "https://play.google.com/store/apps/details?id=com.poguico.palmabici";
	
	private ProgressDialog       dialog;
	private SharedPreferences    conf = null;
	private NetworkSynchronizer  synchronizer;
	private NetworkInformation   network;
	private NavigationDrawerFragment navigationDrawerFragment;
	private FloatingActionButton floatingActionButton;
	private StationMapFragment mapFragment;
	private Station activeStation;
	private LocationSynchronizer locationSynchronizer;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	int savedVersionNumber;
    	SharedPreferences sharedPref;
    	PackageInfo pi;
    	String initPrefs     = "palmabici_init_prefs";
    	String versionNumber = "version_number";
    	int currentVersionNumber        = 0;
    	super.onCreate(savedInstanceState);
    	synchronizer = NetworkSynchronizer.getInstance(this.getApplicationContext());
    	network = NetworkInformation.getInstance(this.getApplicationContext());
    	conf = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	synchronizer.addSynchronizableElement(this);
    	
    	sharedPref = getSharedPreferences(initPrefs, Context.MODE_PRIVATE);
    	savedVersionNumber = sharedPref.getInt(versionNumber, 0);
    	try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception e) {}
 
        if (currentVersionNumber > savedVersionNumber) {
        	new NewFeaturesDialog(this).show();
            Editor editor   = sharedPref.edit();
            editor.putInt(versionNumber, currentVersionNumber);
            editor.commit();
        }


		setContentView(R.layout.main);
        
        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);

		// Set up the drawer.
		navigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
		
		floatingActionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (floatingActionButton.getState() == ButtonState.UPDATE) {
					NetworkSynchronizationState syncState;
	            	/*dialog = ProgressDialog.show(getApplicationContext(), "",
	            			getString(R.string.refresh_ongoing), true);*/
	            	NetworkSynchronizer synchronizer =
	            			NetworkSynchronizer.getInstance(getApplicationContext());
	            	syncState = synchronizer.sync(true);
	            	floatingActionButton.toggleState(ButtonState.UPDATING);
	            	if (syncState == NetworkSynchronizationState.ERROR) {
	    				onUnsuccessfulNetworkSynchronization();
	    			}
				} else if (floatingActionButton.getState() == ButtonState.FAILED) {
					floatingActionButton.toggleState(ButtonState.UPDATE);
				} else if (floatingActionButton.getState() == ButtonState.GOTO) {
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
						    Uri.parse("http://maps.google.com/maps?saddr=" + locationSynchronizer.getLocation().getLatitude() +","+ locationSynchronizer.getLocation().getLongitude() + "&daddr=" + activeStation.getLat() +","+ activeStation.getLong()));
						startActivity(intent);
				} else if (floatingActionButton.getState() == ButtonState.ALARM_ENABLED) {
					synchronizer.removeAlarm(activeStation);
					floatingActionButton.toggleState(ButtonState.ALARM_DISABLED);
				} else if (floatingActionButton.getState() == ButtonState.ALARM_DISABLED) {
					synchronizer.addAlarm(activeStation);
					floatingActionButton.toggleState(ButtonState.ALARM_ENABLED);
				}
			}
		});
		
		mapFragment = (StationMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		mapFragment.attach(this);
		locationSynchronizer = LocationSynchronizer.getInstance(this);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	//getSupportMenuInflater().inflate(R.menu.main, menu);
    	return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;

            case R.id.menu_refresh:
            	NetworkSynchronizationState syncState;
            	dialog = ProgressDialog.show(this, "",
            			getString(R.string.refresh_ongoing), true);
            	NetworkSynchronizer synchronizer =
            			NetworkSynchronizer.getInstance(this);
            	syncState = synchronizer.sync(true);
            	if (syncState == NetworkSynchronizationState.ERROR) {
    				onUnsuccessfulNetworkSynchronization();
    			}
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	protected void onResume() {
    	NetworkSynchronizationState syncState;
		super.onResume();
		
		if (conf.getBoolean("autoupdate", true)) {
			syncState = synchronizer.sync(false);
			
			if (syncState == NetworkSynchronizationState.UPDATED) {
				showLastUpdateTime(false);
			} else if (syncState == NetworkSynchronizationState.ERROR) {
				onUnsuccessfulNetworkSynchronization();
			} else if (syncState == NetworkSynchronizationState.UPDATING) {
				dialog = ProgressDialog.show(this, "",
            			getString(R.string.refresh_ongoing), true);
			}
		}
	}
		
	@Override
	protected void onDestroy() {
		if (dialog != null) {
			dialog.dismiss();
		}
		
		synchronizer.detachSynchronizableElement(this);
		super.onDestroy();
	}
	
	@Override
	public void onSuccessfulNetworkSynchronization() {
    	TextView updateTime = (TextView) findViewById(R.id.lastUpdatedLabel);
    	
    	if (dialog != null)
    		dialog.hide();
    	NotificationManager.showMessage(updateTime,
    			 getString(R.string.refresh_succesful),
				 3000,
				 this,
				 false);
    	floatingActionButton.toggleState(ButtonState.SUCCESSFUL);
	}

	@Override
	public void onUnsuccessfulNetworkSynchronization() {
		if (dialog != null)
    		dialog.hide();
		
		TextView updateTime = (TextView) findViewById(R.id.lastUpdatedLabel);
		NotificationManager.showMessage(updateTime,
										 getString(R.string.connectivity_error),
										 3000,
										 this,
										 true);
		showLastUpdateTime(true);
		floatingActionButton.toggleState(ButtonState.FAILED);
	}

	@Override
	public void onLocationSynchronization() {}

	@Override
	public FragmentActivity getSynchronizableActivity() {
		return this;
	}
	
	private void showLastUpdateTime(boolean forceShow) {
		long lastUpdateTime = network.getLastUpdateTime();
		long delay = Calendar.getInstance().getTimeInMillis() - lastUpdateTime;
		
		if (forceShow || delay > 60000) {
			TextView updateTime = (TextView) findViewById(R.id.lastUpdatedLabel);
			NotificationManager.showMessage(updateTime,
					 Formatter.formatLastUpdated(lastUpdateTime, this),
					 3000,
					 this,
					 false);
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if (position == 0) {
			Intent preferencesActivity =
					new Intent(this, PreferencesActivity.class);
					this.startActivity(preferencesActivity);
		} else if (position == 1) {
			Intent creditsActivity =
					new Intent(this, CreditsDialog.class);
					this.startActivity(creditsActivity);
		} else if (position == 2) {
			Intent issueIntent = new Intent(Intent.ACTION_VIEW);
			issueIntent.setData(Uri.parse(EMAIL));
			startActivity(issueIntent);
		} else if (position == 3) {
			
		} else if (position == 4) {
			Intent issueIntent = new Intent(Intent.ACTION_VIEW);
			issueIntent.setData(Uri.parse(PLAY_URL));
			startActivity(issueIntent);
		}
	}

	@Override
	public void onOpen(Station station) {
		activeStation = station;
		if (synchronizer.hasAlarm(station.getId())) {
			floatingActionButton.toggleState(ButtonState.ALARM_ENABLED);
		} else if (station.getBusySlots() == 0) {
			floatingActionButton.toggleState(ButtonState.ALARM_DISABLED);
		} else {
			floatingActionButton.toggleState(ButtonState.GOTO);
		}
	}

	@Override
	public void onClose() {
		this.activeStation = null;
		floatingActionButton.toggleState(ButtonState.UPDATE);
	}
}
