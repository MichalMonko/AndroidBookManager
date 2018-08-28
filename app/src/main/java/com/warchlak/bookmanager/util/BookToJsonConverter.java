package com.warchlak.bookmanager.util;

import com.warchlak.bookmanager.entity.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class BookToJsonConverter
{
	public static JSONObject convert(Book book) throws JSONException
	{
		JSONObject jsonBook = new JSONObject();
		jsonBook.put("title", book.getTitle());
		jsonBook.put("description", book.getDescription());
		jsonBook.put("tags", new JSONArray(convertTagsToList(book.getTags())));
		jsonBook.put("price", book.getPrice());
		jsonBook.put("imageLink", book.getImageLink());
		
		return jsonBook;
	}
	
	private static List<String> convertTagsToList(String tags)
	{
		String[] tagArray = tags.split(",");
		return Arrays.asList(tagArray);
	}
}
