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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    Affero GNU General Public License for more details
 *    (https://www.gnu.org/licenses/agpl-3.0.html).
 *    
 */

package com.poguico.palmabici.widgets;

import com.poguico.palmabici.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class NewFeaturesDialog extends Dialog {

	Button closeButton;
	Context context;
	Dialog self;
	
	public NewFeaturesDialog(Context context) {
		super(context);
		this.context = context;
		self = this;
		
		this.setTitle(R.string.new_features_new);
		this.setContentView(R.layout.new_features_layout);
		
		closeButton  = (Button)findViewById(R.id.button_close);
		
		closeButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	self.hide();
		    }
		});
	}
}
