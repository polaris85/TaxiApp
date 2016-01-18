package com.libreteam.taxi.extra;

public class GPS {
	
	private double lat;
	private double lng;
	public GPS(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLng() {
		return lng;
	}
}
