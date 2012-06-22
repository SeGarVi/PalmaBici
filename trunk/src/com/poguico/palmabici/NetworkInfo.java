package com.poguico.palmabici;

import java.util.ArrayList;

public class NetworkInfo {
	private static ArrayList <Station> network;
	
	public static void setNetwork(String stations) {
		network = JSONParser.parse(stations);
	}
	
	public static ArrayList <Station> getNetwork() {
		return network;
	}
}
