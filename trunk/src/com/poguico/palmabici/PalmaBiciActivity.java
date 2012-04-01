package com.poguico.palmabici;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

public class PalmaBiciActivity extends Activity {
	Synchronizer synchronizer;
	ArrayList <Station> stations;	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        synchronizer = Synchronizer.getInstance();
        
        stations = synchronizer.synchronize();
        
        //TODO represent station info in list.
    }
}