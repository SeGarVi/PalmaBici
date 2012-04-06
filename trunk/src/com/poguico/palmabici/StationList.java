package com.poguico.palmabici;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationList extends ArrayAdapter<Station> {
	private final Context context;
	private final List<Station> stations;	

	public StationList(Context context, List<Station> stations) {
		super(context, R.layout.main_list_item_layout, stations);
		this.context = context;
		this.stations = stations;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.main_list_item_layout, parent, false);
		TextView id = (TextView) rowView.findViewById(R.id.id);
		TextView name = (TextView) rowView.findViewById(R.id.name);
		TextView bikes = (TextView) rowView.findViewById(R.id.bikes);
		TextView holes = (TextView) rowView.findViewById(R.id.holes);
		//ImageView favourite = (ImageView) rowView.findViewById(R.id.favourite);
				
		id.setText(stations.get(position).getId() + " · ");
		name.setText(stations.get(position).getName());
		bikes.setText("Bicicletas: " + stations.get(position).getBusy_slots());
		holes.setText("Sitios libres: " + stations.get(position).getFree_slots());

		/*if (stations.get(position).isFavourite()) {
			favourite.setImageResource(R.drawable.bookmarks);
		} else {
			favourite.setImageResource(R.drawable.bookmarks_desaturate);
		}*/
		
		return rowView;
	}
}
