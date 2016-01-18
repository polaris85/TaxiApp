package com.libreteam.taxi.extra;

public class HistoryRow {

	private String image;
	private String date;
	private String time;
	private String name;
	
	
	public HistoryRow(String image,String date,String time,String name) {
		this.image = image;
		this.date = date;
		this.time = time;
		this.name = name;
	}

	
	public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
	
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
