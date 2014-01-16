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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    Affero GNU General Public License for more details
 *    (https://www.gnu.org/licenses/agpl-3.0.html).
 *    
 */

package com.poguico.palmabici.widgets;

import com.poguico.palmabici.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CreditsDialog extends Dialog {

	Button closeButton, webButton, agplButton, codeButton;
	Context context;
	Dialog self;
	
	public CreditsDialog(Context context) {
		super(context);
		this.context = context;
		self = this;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.credits_layout);	
		
		webButton    = (Button)findViewById(R.id.button_visit_web);
		codeButton   = (Button)findViewById(R.id.button_source_code);
		agplButton   = (Button)findViewById(R.id.button_agpl_license);
		closeButton  = (Button)findViewById(R.id.button_close);
		
		webButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        openUrl("https://github.com/SeGarVi/PalmaBici/wiki");
		        self.hide();
		    }
		});
		
		codeButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        openUrl("https://github.com/SeGarVi/PalmaBici");
		        self.hide();
		    }
		});
		
		agplButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        openUrl("https://www.gnu.org/licenses/agpl-3.0.en.html");
		        self.hide();
		    }
		});
		
		closeButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	self.hide();
		    }
		});
		
		Linkify.addLinks((TextView)findViewById(R.id.label_author), Linkify.EMAIL_ADDRESSES);
		Linkify.addLinks((TextView)findViewById(R.id.label_citybik), Linkify.WEB_URLS);
		Linkify.addLinks((TextView)findViewById(R.id.label_osm), Linkify.WEB_URLS);
		Linkify.addLinks((TextView)findViewById(R.id.label_osm_bonus), Linkify.WEB_URLS);
	}

	private void openUrl(String url) {
		Intent openUrlActivity = new Intent(Intent.ACTION_VIEW);
		openUrlActivity.setData(Uri.parse(url));
		context.startActivity(openUrlActivity);
	}
}
