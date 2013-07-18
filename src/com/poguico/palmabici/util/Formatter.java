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

package com.poguico.palmabici.util;

import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;

public class Formatter {
	public static String formatDistance (float meters, Context context) {
		String ret = "";
		
		Locale current_local = context.getResources().getConfiguration().locale;
		NumberFormat format  = NumberFormat.getNumberInstance(current_local);
		
		format.setMaximumFractionDigits(2);
		
		ret += (meters < 1000)? format.format(meters) + "m" :
			                    format.format(meters/1000) + "km";
				
		return ret;
	}
}
