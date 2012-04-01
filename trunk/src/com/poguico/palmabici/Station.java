package com.poguico.palmabici;

public class Station {
	private int id, free_slots, busy_slots;
	private String name;
	private long station_long, station_lat;
	private boolean favourite;
	
	public Station(int id, String name, long station_long, long station_lat,
				   int free_slots, int busy_slots, boolean favourite) {
		this.id = id;
		this.free_slots = free_slots;
		this.busy_slots = busy_slots;
		this.name = name;
		this.station_long = station_long;
		this.station_lat  = station_lat;
		this.favourite = favourite;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFree_slots() {
		return free_slots;
	}

	public void setFree_slots(int free_slots) {
		this.free_slots = free_slots;
	}

	public int getBusy_slots() {
		return busy_slots;
	}

	public void setBusy_slots(int busy_slots) {
		this.busy_slots = busy_slots;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStation_long() {
		return station_long;
	}

	public void setStation_long(long station_long) {
		this.station_long = station_long;
	}

	public long getStation_lat() {
		return station_lat;
	}

	public void setStation_lat(long station_lat) {
		this.station_lat = station_lat;
	}

	public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}
}
