package com.poguico.palmabici.util;

import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;

public class Formatter {
	public static String formatDistance (float meters, Context context) {
		String ret = "";
		
		if (meters < 1000) {
			ret += String.valueOf(meters) + "m";
		} else {
			Locale current_local = context.getResources().getConfiguration().locale;
			NumberFormat format = NumberFormat.getNumberInstance(current_local);
			
			format.setMaximumFractionDigits(2);
			
			ret += format.format(meters/1000) + "km";
		}
		
		return ret;
	}
}
