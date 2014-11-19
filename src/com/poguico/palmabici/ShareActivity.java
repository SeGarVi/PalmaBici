/*
 * Copyright 2014 Sergio Garcia Villalonga (yayalose@gmail.com)
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

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShareActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar;
		Button shareRealTimeButton, shareAlarmButton, shareOpenSourceButton;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_layout);
		
		actionBar = getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.sidebar_list_spread));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		shareRealTimeButton = (Button) findViewById(R.id.shareRealTimeButton);
		shareRealTimeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				share(v.getResources().getString(R.string.share_real_time));
			}
		});
		
		shareAlarmButton = (Button) findViewById(R.id.shareAlarmButton);
		shareAlarmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				share(v.getResources().getString(R.string.share_alarm));
			}
		});
		
		shareOpenSourceButton = (Button) findViewById(R.id.shareOpenSourceButton);
		shareOpenSourceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				share(v.getResources().getString(R.string.share_open_source));
			}
		});
	}
	
	private void share(String text) {
		Intent issueIntent = new Intent(Intent.ACTION_SEND);
        issueIntent.setType("text/plain");
        issueIntent.putExtra(Intent.EXTRA_TEXT, text + " " + getResources().getString(R.string.share_get_it));
        startActivity(issueIntent);
        finish();
	}
}
