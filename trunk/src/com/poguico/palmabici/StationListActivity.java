package com.poguico.palmabici;

import java.util.ArrayList;
import android.os.Bundle;
import android.widget.ListView;

public class StationListActivity extends ActionBarActivity {
	ArrayList <Station> stations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        StationList station_list = new StationList(this, NetworkInfo.getNetwork());        
        ListView list = (ListView) findViewById(R.id.stationList);        
        list.setAdapter(station_list);
    }
}
