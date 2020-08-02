package com.cgty.okumalarimuser.model;

public class Lecture
{
	private String lectureName;
	private String malz;
	private String notes;
	private String linkToWatch;
	private String image;
	private String sectionid;
	
	public Lecture()
	{
	}
	
	public Lecture(String lectureName, String malz, String notes, String linkToWatch, String image, String sectionid)
	{
		this.lectureName = lectureName;
		this.malz = malz;
		this.notes = notes;
		this.linkToWatch = linkToWatch;
		this.image = image;
		this.sectionid = sectionid;
	}
	
	public String getLectureName()
	{
		return lectureName;
	}
	
	public void setLectureName(String lectureName)
	{
		this.lectureName = lectureName;
	}
	
	public String getMalz()
	{
		return malz;
	}
	
	public void setMalz(String malz)
	{
		this.malz = malz;
	}
	
	public String getNotes()
	{
		return notes;
	}
	
	public void setNotes(String notes)
	{
		this.notes = notes;
	}
	
	public String getLinkToWatch()
	{
		return linkToWatch;
	}
	
	public void setLinkToWatch(String linkToWatch)
	{
		this.linkToWatch = linkToWatch;
	}
	
	public String getImage()
	{
		return image;
	}
	
	public void setImage(String image)
	{
		this.image = image;
	}
	
	public String getSectionid()
	{
		return sectionid;
	}
	
	public void setSectionid(String sectionid)
	{
		this.sectionid = sectionid;
	}
}
