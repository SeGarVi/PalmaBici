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

import java.util.ArrayList;
import java.util.Collections;


import android.app.AlertDialog;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.poguico.palmabici.parsers.*;
import com.poguico.palmabici.syncronizers.OrientationSynchronizer;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;

public class StationListFragment extends ListFragment {
	private StationAdapter adapter;
	private Context context;
	private ArrayList<Station> stations;
	
    private class StationAdapter extends ArrayAdapter<Station> {    	
    	private final Context context;
    	private final ArrayList<Station> stations;
    	
    	public StationAdapter(Context context, ArrayList<Station> stations) {
    		super(context, R.layout.main_list_item_layout, stations);
    		
    		Collections.sort(stations);
    		
    		this.context = context;
    		this.stations = stations;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		Float dist_f;
    		Matrix rotate_matrix;
    		LayoutInflater inflater = (LayoutInflater) context
    				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View rowView   = inflater.inflate(R.layout.main_list_item_layout, parent, false);
    		
    		if (stations.get(position).isFavourite())
    			rowView.setBackgroundColor(context.getResources().getColor(R.color.list_favourite_item));
    				
    		TextView id       = (TextView)  rowView.findViewById(R.id.id);
    		TextView name     = (TextView)  rowView.findViewById(R.id.name);
    		TextView bikes    = (TextView)  rowView.findViewById(R.id.bikes);
    		TextView holes    = (TextView)  rowView.findViewById(R.id.holes);
    		TextView distance = (TextView)  rowView.findViewById(R.id.distance);
    		//ImageView compass = (ImageView) rowView.findViewById(R.id.compass);
    				
    		id.setText(stations.get(position).getN_estacio() + " · ");		
    		name.setText(stations.get(position).getName());
    		bikes.setText(context.getString(R.string.bikes) + ": " + stations.get(position).getBusy_slots());
    		holes.setText(context.getString(R.string.free_slots) + ": " + stations.get(position).getFree_slots());
    		    		
    		dist_f = stations.get(position).getDistance();
    		if (dist_f >= 0) {
    			
    			float rotation = (stations.get(position).getBearing() -
    							  OrientationSynchronizer.getOrientation()
    							  - (90 * getResources().getConfiguration().orientation)) %
    							  360;
    			    			
    			rotate_matrix = new Matrix();
    			/*rotate_matrix.setRotate(rotation,
    					compass.getDrawable().getIntrinsicWidth() / (float)2,mTabs.get(0).
    					compass.getDrawable().getIntrinsicHeight() / (float)2);
    			compass.setImageMatrix(rotate_matrix);*/
    			
	    		if (dist_f >= 0 && dist_f < 1000)
	    			distance.setText(String.valueOf(dist_f.intValue()) + "m");
	    		else if (dist_f >= 0)
	    			distance.setText(Parser.parseDistance(dist_f/1000, context) + "km");
    		} else {
    			/*compass.setVisibility(ListView.INVISIBLE);*/
    		}
    			
    		
    		return rowView;
    	}
    }
        
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        context = this.getActivity();
        stations = NetworkInformation.getNetwork();

        adapter = new StationAdapter(this.getActivity(),
        							 NetworkInformation.getNetwork());
        setListAdapter(adapter);
        
        /*((ListView)getView()).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				final Station station = adapter.getItem(position);
        		String message;	
        		
        		if (station.isFavourite()) {
        			message = new String(context.getString(R.string.remove_from_favorites));
        		} else {
        			message = new String(context.getString(R.string.add_to_favorites));
        		}

        		final CharSequence[] item = {message.subSequence(0, message.length())};
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        		builder.setTitle(stations.get(position).getName());
        		builder.setItems(item, new DialogInterface.OnClickListener() {
        		    public void onClick(DialogInterface dialog, int item) {
        		        station.changeFavouriteState();
        		        Collections.sort(stations);
        		        adapter.notifyDataSetChanged();
        		    }
        		});
        		builder.show();
        		return true;
			}
			
		});*/
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	
    	final Station station = adapter.getItem(position);
		String message;	
		
		if (station.isFavourite()) {
			message = new String(context.getString(R.string.remove_from_favorites));
		} else {
			message = new String(context.getString(R.string.add_to_favorites));
		}

		final CharSequence[] item = {message.subSequence(0, message.length())};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(stations.get(position).getName());
		builder.setItems(item, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        station.changeFavouriteState();
		        Collections.sort(stations);
		        adapter.notifyDataSetChanged();
		    }
		});
		builder.show();
    }
    	
	public void refresh() {
		for (Station station : stations)
			station.updatePosition();
		Collections.sort(stations);
		adapter.notifyDataSetChanged();	
	}	
}
