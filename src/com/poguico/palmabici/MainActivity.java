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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.poguico.palmabici.notification.NotificationManager;
import com.poguico.palmabici.synchronizers.NetworkSynchronizer;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.widgets.CreditsDialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class MainActivity extends    SherlockFragmentActivity
                          implements SynchronizableActivity {
	private static final long update_time = 600000;
	
	private ProgressDialog      dialog;	
	private SharedPreferences   conf = null;
	private NetworkSynchronizer synchronizer;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	synchronizer = NetworkSynchronizer.getInstance(this);
    	conf = PreferenceManager.getDefaultSharedPreferences(this);
    		
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
            	dialog = ProgressDialog.show(this, "",
            			getString(R.string.refresh_ongoing), true);
            	NetworkSynchronizer synchronizer =
            			NetworkSynchronizer.getInstance(this);
            	synchronizer.synchronize(this);
                break;

            case R.id.menu_credits:
            	new CreditsDialog(this).show();
                break;

            case R.id.menu_preferences:
            	Intent preferences_activity =
            	new Intent(this, PreferencesActivity.class);
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
	protected void onResume() {
		super.onResume();
		
		checkUpdate();
	}
		
	@Override
	protected void onDestroy() {
		NetworkInformation.storeToDB(this);
		synchronizer.detachSynchronizableActivity(this);		
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
		NotificationManager.showMessage(updateTime,
				 Formatter.formatLastUpdated(NetworkInformation.getLastUpdate(), this),
				 3000,
				 this,
				 false);
	}

	@Override
	public void onLocationSynchronization() {}

	@Override
	public FragmentActivity getSynchronizableActivity() {
		return this;
	}
	
	private void checkUpdate() {		
		long now = Calendar.getInstance().getTimeInMillis();
		long lastUpdated = now - NetworkInformation.getLastUpdate();
		
		if (NetworkInformation.getNetwork() == null ||
				(conf.getBoolean("autoupdate", true) &&
				(lastUpdated) > update_time)) {
			dialog = ProgressDialog.show(this, "",
					getString(R.string.refresh_ongoing), true);
			synchronizer.synchronize(this);
		} else if ((lastUpdated/1000) % 60 > 0) {
			TextView updateTime = (TextView) findViewById(R.id.lastUpdatedLabel);
			NotificationManager.showMessage(updateTime,
					 Formatter.formatLastUpdated(NetworkInformation.getLastUpdate(), this),
					 3000,
					 this,
					 false);
		}
		
		/*if (!conf.getString("list_order", "distance").equals(list_ordering)) {
			list_ordering = conf.getString("list_order", "distance");
			station_list.refresh();
		}*/
	}
}
