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
import com.poguico.palmabici.synchronizers.*;
import com.poguico.palmabici.util.NetworkInformation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class WelcomeActivity extends SherlockFragmentActivity implements SynchronizableActivity  {

	private static final int DEFERRED_FINALIZATION_TIME = 2000;
	
	private NetworkSynchronizer synchronizer;
	private Intent              next_activity = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar action_bar = getSupportActionBar();
        if (action_bar != null)
        	action_bar.hide();
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		
		setContentView(R.layout.welcome);
		
		synchronizer = NetworkSynchronizer.getInstance(this);
		synchronizer.synchronize(this);
	}

	@Override
	public void onDestroy() {
		synchronizer.detachSynchronizableActivity(this);		
		super.onDestroy();
	}
	
	@Override
	public void onSuccessfulNetworkSynchronization() {
		TextView text = (TextView)findViewById(R.id.textView1);
    	text.setText(R.string.refresh_succesful);
    	instantiateMainActivity();
	}

	@Override
	public void onUnsuccessfulNetworkSynchronization() {
		TextView text = (TextView)findViewById(R.id.textView1);
    	text.setText(R.string.connectivity_error);
    	(new DeferredFinalizationClass(this, DEFERRED_FINALIZATION_TIME))
    		.execute((Void [])null);;
	}

	@Override
	public void onLocationSynchronization() {}

	@Override
	public FragmentActivity getSynchronizableActivity() {
		return this;
	}
	
	public synchronized void instantiateMainActivity () {
		next_activity = new Intent(this, MainActivity.class);
		synchronizer.detachSynchronizableActivity((SynchronizableActivity)this);
		this.startActivity(next_activity);
		this.finish();
	}
	
	private class DeferredFinalizationClass extends AsyncTask <Void, Void, Void> {
		private WelcomeActivity    activity;
		private NetworkInformation network;
		private long              timeToDie;
		
		public DeferredFinalizationClass (WelcomeActivity activity, long timeToDie) {
			this.activity  = activity;
			this.timeToDie = timeToDie;
			this.network   = NetworkInformation.getInstance();
		}
		
    	protected Void doInBackground(Void... params) {    		
    		try {
				Thread.sleep(timeToDie);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            return null;
        }

        protected void onPostExecute(Void params) {
        	if (network.getLastUpdateTime() > 0) {
        		activity.instantiateMainActivity();
        	} else {
        		System.exit(0);
        	}
        }
    }
}