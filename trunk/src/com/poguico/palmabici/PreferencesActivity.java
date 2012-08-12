package com.poguico.palmabici;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;

public class PreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.preferences_titlebar);
        
		addPreferencesFromResource(R.xml.peferences_layout);
	}

}
