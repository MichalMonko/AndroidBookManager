package com.warchlak.bookmanager;

import android.util.Log;

import com.warchlak.bookmanager.entity.Book;
import com.warchlak.bookmanager.entity.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class JsonDataParser implements RawDataDownloader.DownloadCompleteListener
{
	private static final String TAG = "JsonDataParser";
	
	private Page page = null;
	private ParsingCompleteListener callbackReceiver;
	private ParsingStatus status;
	private URI resourceUri;
	
	public void setUri(URI uri)
	{
		resourceUri = uri;
	}
	
	enum ParsingStatus
	{
		OK, IDLE, PARSING, NOT_INITIALIZED, JSON_ERROR, TIMEOUT, FAILED_OR_EMPTY
	}
	
	JsonDataParser(URI resourceUri, ParsingCompleteListener callbackReceiver)
	{
		this.callbackReceiver = callbackReceiver;
		this.status = ParsingStatus.IDLE;
		this.resourceUri = resourceUri;
	}
	
	public void start()
	{
		RawDataDownloader rawDataDownloader = new RawDataDownloader(this);
		rawDataDownloader.execute(resourceUri);
	}
	
	public interface ParsingCompleteListener
	{
		void onParsingComplete(Page page, ParsingStatus status);
	}
	
	@Override
	public void onDownloadComplete(String downloadedData, RawDataDownloader.DownloadStatus downloadStatus)
	{
		Log.d(TAG, "onDownloadComplete: starts");
		
		if (downloadStatus == RawDataDownloader.DownloadStatus.OK)
		{
			
			Log.d(TAG, "onDownloadComplete: started parsing data: " + "\n\n" + downloadedData + "\n\n");
			
			try
			{
				List<Book> books = new ArrayList<>();
				
				JSONObject jsonResponse = new JSONObject(downloadedData);
				
				int pageNumber = jsonResponse.getInt("number");
				int pagesTotal = jsonResponse.getInt("totalPages");
				
				JSONArray jsonArray = jsonResponse.getJSONArray("content");
				
				for (int i = 0; i < jsonArray.length(); i++)
				{
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					
					String title = jsonObject.getString("title");
					String description = jsonObject.getString("description");
					double price = jsonObject.getDouble("price");
					String imageLink = jsonObject.getString("imageLink");
					
					JSONArray jsonTags = jsonObject.getJSONArray("tags");
					
					boolean isFirstIteration = true;
					
					StringBuilder tagsBuilder = new StringBuilder();
					for (int j = 0; j < jsonTags.length(); j++)
					{
						if (!isFirstIteration)
						{
							tagsBuilder.append(", ");
						}
						
						String tag = jsonTags.getString(j);
						tagsBuilder.append("#");
						tagsBuilder.append(tag);
						
						isFirstIteration = false;
					}
					
					Book book = new Book(title, description, tagsBuilder.toString(), price, imageLink);
					Log.d(TAG, "onDownloadComplete: parsed book: " + book.toString());
					books.add(book);
				}
				
				Log.d(TAG, "onDownloadComplete: parsing ended");
				
				page = new Page(pageNumber, pagesTotal, books);
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
		else if (downloadStatus == RawDataDownloader.DownloadStatus.TIMEOUT)
		{
			status = ParsingStatus.TIMEOUT;
		}
		else
		{
			status = ParsingStatus.NOT_INITIALIZED;
		}
		
		if (callbackReceiver != null)
		{
			callbackReceiver.onParsingComplete(page, status);
		}
	}
}
