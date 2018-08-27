package com.warchlak.bookmanager.util;

import com.warchlak.bookmanager.entity.Book;

import org.json.JSONException;
import org.json.JSONObject;

public class BookToJsonConverter
{
	public static JSONObject convert(Book book) throws JSONException
	{
		JSONObject jsonBook = new JSONObject();
		jsonBook.put("title", book.getTitle());
		jsonBook.put("description", book.getDescription());
		jsonBook.put("tags", book.getTags());
		jsonBook.put("price", book.getPrice());
		jsonBook.put("imageLink", book.getImageLink());
		
		return jsonBook;
	}
}
