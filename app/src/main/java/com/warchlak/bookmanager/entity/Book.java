package com.warchlak.bookmanager.entity;

public class Book
{
	private String title;
	private String description;
	private String tags;
	private double price;
	private String imageLink;
	
	public Book(String title, String description, String tags, double price, String imageLink)
	{
		this.title = title;
		this.description = description;
		this.tags = tags;
		this.price = price;
		this.imageLink = imageLink;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getTags()
	{
		return tags;
	}
	
	public void setTags(String tags)
	{
		this.tags = tags;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	public void setPrice(double price)
	{
		this.price = price;
	}
	
	public String getImageLink()
	{
		return imageLink;
	}
	
	public void setImageLink(String imageLink)
	{
		this.imageLink = imageLink;
	}
	
	@Override
	public String toString()
	{
		return "Book{" +
				"title='" + title + '\'' +
				", description='" + description + '\'' +
				", tags='" + tags + '\'' +
				", price=" + price +
				", imageLink='" + imageLink + '\'' +
				'}';
	}
}
