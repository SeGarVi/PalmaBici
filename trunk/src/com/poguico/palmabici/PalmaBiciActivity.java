package com.poguico.palmabici;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class PalmaBiciActivity extends Activity {

    private class SynchronizeTask extends AsyncTask <Void, Void, Void> {
        
    	protected Void doInBackground(Void... params) {
    		NetworkInfo.setNetwork(Synchronizer.getNetworkInfo());
            publishProgress((Void [])null);
            return null;
        }        
        
        protected void onProgressUpdate(Void... params) {
        	TextView text = (TextView)findViewById(R.id.textView1);
        	text.setText("Loaded!");
        }

        protected void onPostExecute(Void params) {
        	enter_app();
        }
    }
	
	String stations;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.welcome);
        new SynchronizeTask().execute((Void [])null);
    }
    
    private void enter_app () {
    	Intent next_activity = new Intent(this, StationListActivity.class);
    	this.startActivity(next_activity);
    }
}