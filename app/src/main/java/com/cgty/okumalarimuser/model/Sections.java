package com.cgty.okumalarimuser.model;

public class Sections
{
	private String name;
	private String image;
	private String categoryid;
	
	public Sections()
	{
	}
	
	public Sections(String name, String image, String categoryid)
	{
		this.name = name;
		this.image = image;
		this.categoryid = categoryid;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getImage()
	{
		return image;
	}
	
	public void setImage(String image)
	{
		this.image = image;
	}
	
	public String getCategoryid()
	{
		return categoryid;
	}
	
	public void setCategoryid(String categoryid)
	{
		this.categoryid = categoryid;
	}
}