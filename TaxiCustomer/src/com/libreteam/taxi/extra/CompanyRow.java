package com.libreteam.taxi.extra;

public class CompanyRow {

	private String id;
	private Boolean check;
	private String name;
	private String position;
	private String rate;
	
	public CompanyRow(String id, Boolean check,String name,String position,String rate) {
		this.id = id;
		this.check = check;
		this.name = name;
		this.position = position;
		this.rate = rate;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}

	public Boolean getCheck()
	{
		return check;
	}
	
	public void setCheck(Boolean check)
	{
		this.check = check;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getPosition()
	{
		return position;
	}
	
	public void setPostition(String position)
	{
		this.position = position;
	}
	
	public String getRate()
	{
		return rate;
	}
	
	public void setRate(String rate)
	{
		this.rate = rate;
	}
}
