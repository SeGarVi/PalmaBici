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
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;

import com.poguico.palmabici.R;;

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
	
	public static String formatLastUpdated(long lastUpdated, Context context) {
		String ret = context.getString(R.string.last_updated) + " ";
		
		long interval = (Calendar.getInstance().getTimeInMillis() -
							lastUpdated) / 1000;
		
		long mins, hours, days;
				
		if ((days = interval/86400) > 0) {
			ret += context.getString(R.string.more_than) + " " + days + " d" +
					((!context.getString(R.string.ago).equals(""))?
							" " + context.getString(R.string.ago) : "");
		} else if ((hours = interval/3600) > 0) {
			ret += context.getString(R.string.more_than) + " " + hours + " h" +
					((!context.getString(R.string.ago).equals(""))?
							" " + context.getString(R.string.ago) : "");
		} else if ((mins = interval/60) > 0) {
			ret += mins + " m" +
					((!context.getString(R.string.ago).equals(""))?
							" " + context.getString(R.string.ago) : "");
		} else {
			ret += (interval%60) + " s" +
					((!context.getString(R.string.ago).equals(""))?
							" " + context.getString(R.string.ago) : "");
		}
		
		return ret; 
	}
}
