package com.warchlak.bookmanager.entity;

import java.util.List;

public class Page
{
	final int pageNumber;
	final int maximumNumberOfPages;
	List<Book> booksList;
	
	public Page(int pageNumber, int maximumNumberOfPages, List<Book> booksList)
	{
		this.pageNumber = pageNumber;
		this.maximumNumberOfPages = maximumNumberOfPages;
		this.booksList = booksList;
	}
	
	public int getPageNumber()
	{
		return pageNumber;
	}
	
	public int getMaximumNumberOfPages()
	{
		return maximumNumberOfPages;
	}
	
	public List<Book> getBooksList()
	{
		return booksList;
	}
	
	public void setBooksList(List<Book> booksList)
	{
		this.booksList = booksList;
	}
}
