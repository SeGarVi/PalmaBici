package com.poguico.palmabici;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StationList extends ArrayAdapter<Station> {
	private final Context context;
	private final List<Station> stations;
	
	/*
	 * Ugly solution to keep nice names
	 */
	private static final Map<String, String> myMap;
    static {
    	Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("01", "Parc de ses Veles");
        aMap.put("06", "Manacor - Manuel Azaña");
        aMap.put("07", "Aragó - Nuredduna");
        aMap.put("09", "F. Manuel de los Herreros");
        aMap.put("13", "Parc de les Estacions");
        aMap.put("15", "J. Verdaguer - J. Balmes");
        aMap.put("16", "Parc de sa Riera");
        aMap.put("17", "Aragó - J. Balmes");
        aMap.put("21", "Pl. Alexander Flemimg");
        aMap.put("24", "Blanquerna - C. de Sallent");
        aMap.put("25", "Blanquerna - Bartolomé");
        aMap.put("27", "Pl. París");
        aMap.put("29", "Institut Balear");
        aMap.put("31", "Pl. Madrid");
        aMap.put("37", "Av. Argentina");
        aMap.put("41", "Fàbrica");
        aMap.put("45", "Jaume III");
        aMap.put("46", "Pl. Rei Joan Carles I");
        aMap.put("47", "Pl. Porta de Santa Catalina");
        aMap.put("49", "Pl. de la Reina");
        aMap.put("51", "Via Roma");
        aMap.put("52", "Cecili Metel");
        aMap.put("55", "Pl. Santa Eulàlia");
        aMap.put("56", "Pl. del Mercat");
        aMap.put("59", "Mateu Enric Lladó");
        aMap.put("60", "Travessera Ballester");
        aMap.put("63", "Pl. d'Espanya");
        aMap.put("65", "Pl. Alexandre Jaume");
        myMap = Collections.unmodifiableMap(aMap);
    }

	public StationList(Context context, List<Station> stations) {
		super(context, R.layout.main_list_item_layout, stations);
		
		Collections.sort(stations);
		
		this.context = context;
		this.stations = stations;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView   = inflater.inflate(R.layout.main_list_item_layout, parent, false);
		TextView id    = (TextView) rowView.findViewById(R.id.id);
		TextView name  = (TextView) rowView.findViewById(R.id.name);
		TextView bikes = (TextView) rowView.findViewById(R.id.bikes);
		TextView holes = (TextView) rowView.findViewById(R.id.holes);
		//ImageView favourite = (ImageView) rowView.findViewById(R.id.favourite);
				
		id.setText(stations.get(position).getN_estacio() + " · ");		
		name.setText(myMap.get(stations.get(position).getN_estacio()));
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
