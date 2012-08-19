package com.poguico.palmabici;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class StationListActivity extends ActionBarActivity {
	ArrayList <Station> stations;
	ProgressDialog dialog;
	Synchronizer synchronizer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        synchronizer = Synchronizer.getInstance();
        StationList station_list = new StationList(this, NetworkInformation.getNetwork());        
        ListView list = (ListView) findViewById(R.id.stationList);        
        list.setAdapter(station_list);
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
		
		if (conf.getBoolean("autoupdate", true) && (now - synchronizer.getLastUpdate()) > 60000) {
			synchronizer.new SynchronizeTask(this).execute((Void [])null);
		}
	}

	@Override
	public void successfulSynchronization() {
		if (dialog != null)
    		dialog.hide();
    	
    	Toast.makeText(this, R.string.refresh_succesful, Toast.LENGTH_SHORT).show();
    	StationList station_list = new StationList(this, NetworkInformation.getNetwork());        
        ListView list = (ListView) findViewById(R.id.stationList);        
        list.setAdapter(station_list);
	}

	@Override
	public void unsuccessfulSynchronization() {
		Toast.makeText(this, R.string.connectivity_error, Toast.LENGTH_SHORT).show();
	}
}
