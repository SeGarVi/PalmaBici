package com.poguico.palmabici;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class Synchronizer {
	
	private static final String URL = "http://api.citybik.es/palma.json";
	
	public static String getNetworkInfo() {
		StringBuilder builder = new StringBuilder();
		HttpClient    client  = new DefaultHttpClient();
		HttpGet 	  request = new HttpGet(URL);
		String		  line;
		
		try {
			HttpResponse response = client.execute(request);
			StatusLine   status_line = response.getStatusLine();
			if (status_line.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				InputStreamReader content_reader = new InputStreamReader(content);
				BufferedReader reader = new BufferedReader(content_reader);
				
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(Synchronizer.class.toString(), "Failed to download file");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return builder.toString();
	}
}
