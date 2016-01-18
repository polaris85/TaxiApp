package com.libreteam.taxi.extra;

import com.google.android.gms.maps.model.LatLng;


public class RowItem {
	private String driver;
    private String image;
    private String vehicle;
    private String heart;
    private String time;
    private String price;
    private String rating;
    private String top1;
    private String starnum;
    private String response;
    private String top2;
    private String responsenum;
    private String driverid;
    private String start_fee;
    private String per_km;
    private String min;
    private LatLng latlng;
    private String companyName;
    private String appName;
    private String plateNumber;
    
    public RowItem( String plateNumber, String image,String driver,String vehicle, String heart, String time, String price, String rating
    		, String top1, String starnum, String response, String top2, String responsenum,String driverid,String start_fee,String per_km,String min,LatLng latlng
    		) {
    	this.plateNumber = plateNumber;
    	this.driver = driver;
        this.image = image;
        this.vehicle = vehicle;
        this.heart = heart;
        this.time = time;
        this.price = price;
        this.rating = rating;
        this.top1 = top1;
        this.starnum = starnum;
        this.response = response;
        this.top2 = top2;
        this.responsenum = responsenum;
        this.driverid = driverid;
        this.start_fee = start_fee;
        this.per_km = per_km;
        this.min = min;
        this.latlng = latlng;
    }
    
    public String getPlateNumber() {
    	return plateNumber;
    }
    public void setPlateNumber(String plateNumber) {
    	this.plateNumber = plateNumber;
    }
    public String getDriver() {
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
    
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    
    public String getVehicle() {
        return vehicle;
    }
    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }
    
    public String getHeart() {
        return heart;
    }
    public void setHeart(String heart) {
        this.heart = heart;
    }
    
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    
    public String getTop1() {
        return top1;
    }
    public void setTop1(String top1) {
        this.top1 = top1;
    }
    
    public String getStarnum() {
        return starnum;
    }
    public void setStarnum(String starnum) {
        this.starnum = starnum;
    }
    
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getTop2() {
        return top2;
    }
    public void setTop2(String top2) {
        this.top2 = top2;
    }
    
    public String getResponsenum() {
        return responsenum;
    }
    public void setResponsenum(String responsenum) {
        this.responsenum = responsenum;
    }
    
    public String getDriverid() {
        return driverid;
    }
    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }
    
    public String getStart_fee() {
        return start_fee;
    }
    public void setStart_fee(String start_fee) {
        this.start_fee = start_fee;
    }
    
    public String getPer_km() {
        return per_km;
    }
    public void setPer_km(String per_km) {
        this.per_km = per_km;
    }
    
    public String getMin() {
        return min;
    }
    public void setMin(String min) {
        this.min = min;
    }
    
    public LatLng getLatLng() {
        return latlng;
    }
    public void setLatLng(LatLng latlng) {
        this.latlng = latlng;
    }

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}