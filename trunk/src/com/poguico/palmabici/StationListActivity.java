package com.poguico.palmabici;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
	
	/**
	 * Put in other place
	 */
	private Long last_update = Calendar.getInstance().getTimeInMillis();

	private class SynchronizeTask extends AsyncTask <Void, Void, Void> {
        
		Activity activity;
		
		public SynchronizeTask (Activity activity) {
			this.activity = activity;
		}
		
    	protected Void doInBackground(Void... params) {
    		NetworkInfo.setNetwork(Synchronizer.getNetworkInfo());
            return null;
        }

        protected void onPostExecute(Void params) {
        	last_update = Calendar.getInstance().getTimeInMillis();
        	
        	if (dialog != null)
        		dialog.hide();
        	
        	Toast.makeText(activity, R.string.refresh_succesful, Toast.LENGTH_SHORT).show();
        	StationList station_list = new StationList(activity, NetworkInfo.getNetwork());        
            ListView list = (ListView) findViewById(R.id.stationList);        
            list.setAdapter(station_list);
        }
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        StationList station_list = new StationList(this, NetworkInfo.getNetwork());        
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
                Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_refresh:
            	//dialog = ProgressDialog.show(this, "", R.string.refresh_ongoing, true);
                new SynchronizeTask(this).execute((Void [])null);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SharedPreferences conf=PreferenceManager
				.getDefaultSharedPreferences(this);

		long now = Calendar.getInstance().getTimeInMillis();
		
		if (conf.getBoolean("autoupdate", true) && (now-last_update) > 60000) {
			new SynchronizeTask(this).execute((Void [])null);
		}
	}
}
