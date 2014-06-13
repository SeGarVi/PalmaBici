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
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.poguico.palmabici.map.OpenStreetMapConstants;
import com.poguico.palmabici.notification.NotificationManager;
import com.poguico.palmabici.network.synchronizer.NetworkSynchronizer;
import com.poguico.palmabici.network.synchronizer.NetworkSynchronizer.NetworkSynchronizationState;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.widgets.CreditsDialog;
import com.poguico.palmabici.widgets.NewFeaturesDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.System;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class MainActivity extends    SherlockFragmentActivity
                          implements SynchronizableElement,OpenStreetMapConstants{
	private static final String REPORT_URL = "https://github.com/SeGarVi/PalmaBici/issues/new";
	
	private ProgressDialog       dialog;	
	private SharedPreferences    conf = null;
	private NetworkSynchronizer  synchronizer;
	private NetworkInformation   network;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	int savedVersionNumber;
    	String language;
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
 
    	language = Locale.getDefault().getLanguage();
        if (currentVersionNumber > savedVersionNumber &&
        		(language.equals("de") ||
        		 language.equals("zh") ||
        		 language.equals("fr") ||
        		 language.equals("ru"))) {
        	
        	new NewFeaturesDialog(this).show();
            Editor editor   = sharedPref.edit();
            editor.putInt(versionNumber, currentVersionNumber);
            editor.commit();
        }
        
        setContentView(R.layout.main);
    }
    
    @Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.main, menu);
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

            case R.id.menu_credits:
            	new CreditsDialog(this).show();
                break;

            case R.id.menu_preferences:
            	Intent preferencesActivity =
            	new Intent(this, PreferencesActivity.class);
            	this.startActivity(preferencesActivity);
                break;
                
            case R.id.menu_report:
                Intent issueIntent = new Intent(Intent.ACTION_VIEW);
                issueIntent.setData(Uri.parse(REPORT_URL));
                startActivity(issueIntent);
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
}
