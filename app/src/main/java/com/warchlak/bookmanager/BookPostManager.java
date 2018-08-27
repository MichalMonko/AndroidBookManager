package com.warchlak.bookmanager;

import android.os.AsyncTask;
import android.util.Log;

import com.warchlak.bookmanager.entity.Book;
import com.warchlak.bookmanager.util.BookRestApiUriHolder;
import com.warchlak.bookmanager.util.BookToJsonConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookPostManager extends AsyncTask<Book, Void, BookPostManager.SendingStatus>
{
	enum SendingStatus
	{
		IDLE, NOT_INITIALIZED, SENDING, OK, ERROR
	}
	
	private static final String TAG = "BookPostManager";
	private final SendingCompleteListener callbackReceiver;
	
	BookPostManager(SendingCompleteListener callbackReceiver)
	{
		this.callbackReceiver = callbackReceiver;
	}
	
	interface SendingCompleteListener
	{
		void onSendingComplete(SendingStatus sendingStatus);
	}
	
	@Override
	protected void onPostExecute(SendingStatus status)
	{
		if (callbackReceiver != null)
		{
			callbackReceiver.onSendingComplete(status);
		}
	}
	
	@Override
	protected SendingStatus doInBackground(Book... books)
	{
		Log.d(TAG, "doInBackground: starting uploading book");
		
		Book book = books[0];
		if (book == null)
		{
			return SendingStatus.NOT_INITIALIZED;
		}
		
		BufferedWriter bufferedWriter = null;
		HttpURLConnection connection = null;
		
		try
		{
			URL url = new URL(BookRestApiUriHolder.BASE_BOOK_URL);
			connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("POST");
			connection.setRequestProperty("content-type", "application/json;charset=UTF-8");
			connection.setDoInput(true);
			
			Log.d(TAG, "doInBackground: connecting");
			
			bufferedWriter = new BufferedWriter(new PrintWriter(connection.getOutputStream()));
			
			JSONObject jsonBook = BookToJsonConverter.convert(book);
			String jsonString = jsonBook.toString();
			
			Log.d(TAG, "doInBackground: json String is " + jsonString);
			
			bufferedWriter.write(jsonString);
			bufferedWriter.flush();
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED)
			{
				Log.d(TAG, "doInBackground: response code is different from expected CREATED (201)");
				return SendingStatus.ERROR;
			}
			
			Log.d(TAG, "doInBackground: returns SendingStatus.OK");
			return SendingStatus.OK;
			
		} catch (JSONException e)
		{
			Log.e(TAG, "doInBackground: cannot parse book to JSON: " + e.getLocalizedMessage());
		} catch (IOException e)
		
		{
			Log.e(TAG, "doInBackground: Cannot open connection: " + e.getMessage());
		} catch (SecurityException e)
		
		{
			Log.e(TAG, "doInBackground: Permission needed: " + e.getMessage());
		} finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
			if (bufferedWriter != null)
			{
				try
				{
					bufferedWriter.close();
				} catch (IOException e)
				{
					Log.e(TAG, "doInBackground: cannot close connection buffered reader: " + e.getMessage());
				}
			}
		}
		
		Log.d(TAG, "doInBackground: ends");
		return SendingStatus.ERROR;
	}
}
