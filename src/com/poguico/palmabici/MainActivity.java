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
import com.poguico.palmabici.syncronizers.NetworkSynchronizer;
import com.poguico.palmabici.util.Formatter;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.widgets.CreditsDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends    SherlockFragmentActivity
                          implements SynchronizableActivity {
	private static final long update_time = 600000;
	
	private ProgressDialog    dialog;	
	private SharedPreferences conf = null;
	private NetworkSynchronizer synchronizer;
	
	private class ShowLabelTask extends AsyncTask <Void, Void, Void> {
        
		private static final String okColor = "#CC8bff16";
		private static final String problemColor = "#CCFF0000";		
		
		private TextView textView;
		private String   message;
		private long    duration;
		private Activity parent;
		private Animation showLabel;
		private Animation hideLabel;
		
		public ShowLabelTask (TextView textView,
                               String   message,
                               long    duration,
                               Activity parent,
                               boolean problem) {
			this.textView = textView;
			this.message  = message;
			this.duration = duration;
			this.parent   = parent;
			
			showLabel = AnimationUtils.loadAnimation(parent, R.anim.push_up_in);
			hideLabel = AnimationUtils.loadAnimation(parent, R.anim.push_down_out);
			
			textView.setText(message);

			textView.setBackgroundResource((problem)?R.color.problem_palmabici:
                                                     R.color.pressed_palmabici);
			textView.setTextColor((problem)?0xFFFFFFFF:0xFF000000);
			textView.setVisibility(View.VISIBLE);
			textView.startAnimation(showLabel);
		}
		
    	protected Void doInBackground(Void... params) {
    		try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
        }

        protected void onPostExecute(Void params) {
        	textView.startAnimation(hideLabel);
        	textView.setVisibility(View.INVISIBLE);
        }
    }
	
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
		DatabaseManager.getInstance(this).saveFavouriteStations(NetworkInformation.getNetwork());
		synchronizer.detachSynchronizableActivity(this);		
		super.onDestroy();
	}
	
	@Override
	public void onSuccessfulNetworkSynchronization() {
    	TextView updateTime = (TextView) findViewById(R.id.lastUpdatedLabel);
    	
    	if (dialog != null)
    		dialog.hide();
    	(new ShowLabelTask(updateTime,
    			getString(R.string.refresh_succesful), 3000, this, false))
    			.execute((Void [])null);
	}

	@Override
	public void onUnsuccessfulNetworkSynchronization() {
		if (dialog != null)
    		dialog.hide();
		
		TextView updateTime = (TextView) findViewById(R.id.lastUpdatedLabel);
		(new ShowLabelTask(updateTime,
				getString(R.string.connectivity_error), 3000, this, true))
				.execute((Void [])null);
	}

	@Override
	public void onLocationSynchronization() {}

	@Override
	public FragmentActivity getSynchronizableActivity() {
		return this;
	}
	
	private void checkUpdate() {		
		long now = Calendar.getInstance().getTimeInMillis();
		long lastUpdated = now - synchronizer.getLastUpdate();
		
		if (NetworkInformation.getNetwork() == null ||
				(conf.getBoolean("autoupdate", true) &&
				(lastUpdated) > update_time)) {
			dialog = ProgressDialog.show(this, "",
					getString(R.string.refresh_ongoing), true);
			synchronizer.synchronize(this);
		} else if ((lastUpdated/1000) % 60 > 0) {
			TextView updateTime = (TextView) findViewById(R.id.lastUpdatedLabel);
			(new ShowLabelTask(updateTime,
					Formatter.formatLastUpdated(synchronizer.getLastUpdate(), this),
					3000, this, false))
					.execute((Void [])null);
		}
		
		/*if (!conf.getString("list_order", "distance").equals(list_ordering)) {
			list_ordering = conf.getString("list_order", "distance");
			station_list.refresh();
		}*/
	}
}
