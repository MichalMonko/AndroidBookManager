package com.warchlak.bookmanager.util;

import android.net.Uri;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

public class BookRestApiUriHolder
{
	private static final String TAG = "BookRestApiUriHolder";
	
	public static final String BASE_BOOK_URL = "http://192.168.1.234:8081/api/book/";
	public static final String BASE_IMAGE_URL = "http://192.168.1.234:8081/api/images/";
	public static final String FILE_PARAM_NAME = "file";
	public static final String DEFAULT_URI_STRING = "http://192.168.1.234:8081/api/book/?pageNumber=0&pageSize=10&lookupMethod=all";
	
	public static String lastUsedUri = DEFAULT_URI_STRING;
	
	public static Uri buildPhotoUri(String imageName)
	{
		Log.d(TAG, "buildPhotoUri: start");
		
		Uri uri = Uri.parse(BASE_IMAGE_URL)
		             .buildUpon()
		             .appendPath(imageName)
		             .build();
		
		Log.d(TAG, "buildPhotoUri: returning URI: " + uri.toString());
		
		return uri;
	}
	
	public static class TagSearchMethod
	{
		public static final String ANY = "any";
		public static final String ALL = "all";
	}
	
	public static URI buildPageUri(int pageNumber, int pageSize, String tags, String searchMethod)
	{
		Log.d(TAG, "buildPageUri: start");
		
		URI netUri = null;
		Uri uri = null;
		
		Uri.Builder builder = Uri.parse(BASE_BOOK_URL)
		                         .buildUpon()
		                         .appendQueryParameter("pageNumber", String.valueOf(pageNumber))
		                         .appendQueryParameter("pageSize", String.valueOf(pageSize))
		                         .appendQueryParameter("lookupMethod", searchMethod);
		
		Log.d(TAG, "buildPageUri: tags are: " + tags);
		if (tags != null)
		{
			Log.d(TAG, "buildPageUri: appending tags");
			builder.appendQueryParameter("tags", tags).build();
		}
		
		try
		{
			uri = builder.build();
			netUri = new URI(uri.toString());
		} catch (URISyntaxException e)
		{
			Log.e(TAG, "buildPageUri: uri " + uri + " cannot be converted to URI");
			return null;
		}
		
		Log.d(TAG, "buildPageUri: returning Uri: " + netUri.toString());
		
		lastUsedUri = uri.toString();
		return netUri;
	}
}
