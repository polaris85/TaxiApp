package com.libreteam.driver.extra;


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
    
    
    public RowItem( String image,String driver,String vehicle, String heart, String time, String price, String rating
    		, String top1, String starnum, String response, String top2, String responsenum,String driverid
    		) {
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
}