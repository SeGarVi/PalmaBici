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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.poguico.palmabici.syncronizers.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class WelcomeActivity extends SherlockFragmentActivity implements SynchronizableActivity  {

	NetworkSynchronizer synchronizer;	
	String stations;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar action_bar = getSupportActionBar();
        if (action_bar != null)
        	action_bar.hide();
        
        DatabaseManager.initDB(this);
        LocationSynchronizer.init(this);
        OrientationSynchronizer.init(this);
        synchronizer = NetworkSynchronizer.getInstance();
        synchronizer.addSynchronizableActivity(this);
        
        setContentView(R.layout.welcome);
        synchronizer.new SynchronizeTask(this).execute((Void [])null);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        setContentView(R.layout.welcome);
        synchronizer.new SynchronizeTask(this).execute((Void [])null);
    }
    
	@Override
	public void onSuccessfulNetworkSynchronization() {
		TextView text = (TextView)findViewById(R.id.textView1);
    	text.setText(R.string.refresh_succesful);
		Intent next_activity = new Intent(this, MainActivity.class);
		synchronizer.detachSynchronizableActivity(this);
    	this.startActivity(next_activity);
    	this.finish();
	}

	@Override
	public void onUnsuccessfulNetworkSynchronization() {
		TextView text = (TextView)findViewById(R.id.textView1);
    	text.setText(R.string.connectivity_error);
	}

	@Override
	public void onLocationSynchronization() {}

	@Override
	public FragmentActivity getSynchronizableActivity() {
		return this;
	}
}