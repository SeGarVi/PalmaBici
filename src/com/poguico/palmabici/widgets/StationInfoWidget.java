package com.poguico.palmabici.widgets;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.poguico.palmabici.R;
import com.poguico.palmabici.util.NetworkInformation;
import com.poguico.palmabici.util.Station;

public class StationInfoWidget implements InfoWindowAdapter {

	private Activity activity;
	private NetworkInformation networkInformation;
	
	public StationInfoWidget (Activity activity) {
		this.activity    = activity;
		networkInformation = NetworkInformation.getInstance();
	}
	
	@Override
	public View getInfoContents(Marker arg0) {
		int    freeBikes, freeSlots, brokenBikes, brokenSlots;
		Station station = networkInformation.get(arg0.getSnippet());
		
		View stationInfoView =
			activity.getLayoutInflater().inflate(R.layout.station_info, null);
		
		TextView title =
				(TextView)stationInfoView.findViewById(R.id.markerTitle);
		TextView tvFreeBikes =
				(TextView)stationInfoView.findViewById(R.id.freeBikes);
		TextView tvFreeSlots =
				(TextView)stationInfoView.findViewById(R.id.freeSlots);
		LinearLayout lyBrokenApparel =
				(LinearLayout)stationInfoView.findViewById(R.id.brokenApparel);
		
		freeBikes   = station.getBusy_slots();
		freeSlots   = station.getFree_slots();
		brokenBikes = station.getBroken_bikes();
		brokenSlots = station.getBroken_slots();
		
		
		title.setText(arg0.getTitle());
		tvFreeBikes.setText(String.valueOf(freeBikes));
		tvFreeSlots.setText(String.valueOf(freeSlots));
		
		if (brokenBikes > 0 || brokenSlots > 0) {
			TextView tvBrokenBikes =
					(TextView)stationInfoView.findViewById(R.id.brokenBikes);
			TextView tvBrokenSlots =
					(TextView)stationInfoView.findViewById(R.id.brokenSlots);
			tvBrokenBikes.setText(String.valueOf(brokenBikes));
			tvBrokenSlots.setText(String.valueOf(brokenSlots));
			//lyBrokenApparel.setVisibility(View.VISIBLE);
			lyBrokenApparel.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
												  LayoutParams.WRAP_CONTENT));
		} else {
			//lyBrokenApparel.setVisibility(View.INVISIBLE);
			lyBrokenApparel.setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,0));
		}
		
		return stationInfoView;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
