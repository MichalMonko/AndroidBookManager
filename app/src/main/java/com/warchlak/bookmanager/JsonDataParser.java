package com.warchlak.bookmanager;

import android.util.Log;

import com.warchlak.bookmanager.entity.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class JsonDataParser implements RawDataDownloader.DownloadCompleteListener
{
	private static final String TAG = "JsonDataParser";
	
	private List<Book> books = null;
	private ParsingCompleteListener callbackReceiver;
	private ParsingStatus status;
	
	enum ParsingStatus
	{
		OK, IDLE, PARSING, NOT_INITIALIZED, JSON_ERROR, FAILED_OR_EMPTY
	}
	
	public JsonDataParser(URI resourceUri, ParsingCompleteListener callbackReceiver)
	{
		this.callbackReceiver = callbackReceiver;
		this.status = ParsingStatus.IDLE;
		
		RawDataDownloader rawDataDownloader = new RawDataDownloader(this);
		rawDataDownloader.execute(resourceUri);
	}
	
	public interface ParsingCompleteListener
	{
		void onParsingComplete(List<Book> parsedData, ParsingStatus status);
	}
	
	@Override
	public void onDownloadComplete(String downloadedData, RawDataDownloader.DownloadStatus downloadStatus)
	{
		Log.d(TAG, "onDownloadComplete: starts");
		
		if (downloadStatus == RawDataDownloader.DownloadStatus.OK)
		{
			
			Log.d(TAG, "onDownloadComplete: started parsing data: " + "\n\n" +  downloadedData + "\n\n");
			
			try
			{
				books = new ArrayList<>();
				
				JSONArray jsonArray = new JSONArray(downloadedData);
				
				for (int i = 0; i < jsonArray.length(); i++)
				{
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					
					String title = jsonObject.getString("title");
					String description = jsonObject.getString("description");
					String tags = jsonObject.getString("tags");
					double price = jsonObject.getDouble("price");
					String imageLink = jsonObject.getString("imageLink");
					
					
					Book book = new Book(title, description, tags, price, imageLink);
					Log.d(TAG, "onDownloadComplete: parsed book: " + book.toString());
					books.add(book);
				}
				
				Log.d(TAG, "onDownloadComplete: parsing ended");
				
				status = ParsingStatus.OK;
				
				
			} catch (JSONException e)
			{
				Log.e(TAG, "onDownloadComplete: error while parsing JSON: " + e.getLocalizedMessage());
				status = ParsingStatus.JSON_ERROR;
			} catch (Exception e)
			{
				Log.e(TAG, "onDownloadComplete: error during data parsing: " + e.getLocalizedMessage());
				status = ParsingStatus.FAILED_OR_EMPTY;
			}
		}
		else
		{
			status = ParsingStatus.NOT_INITIALIZED;
		}
		
		if (callbackReceiver != null)
		{
			callbackReceiver.onParsingComplete(books, status);
		}
	}
}
