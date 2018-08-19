package com.warchlak.bookmanager;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RawDataDownloader extends AsyncTask<String, Void, String>
{
	private static final String TAG = "RawDataDownloader";
	
	private DownloadStatus downloadStatus;
	private DownloadCompleteListener downloadListener;
	
	enum DownloadStatus
	{
		IDLE, NOT_INITIALIZED, DOWNLOADING, OK, ERROR
	}
	
	public interface DownloadCompleteListener
	{
		void onDownloadComplete(String downloadedData, DownloadStatus downloadStatus);
	}
	
	public RawDataDownloader(DownloadCompleteListener downloadListener)
	{
		this.downloadListener = downloadListener;
		this.downloadStatus = DownloadStatus.IDLE;
	}
	
	@Override
	protected void onPostExecute(String s)
	{
		Log.d(TAG, "onPostExecute: start");
		
		if (downloadListener != null)
		{
			downloadListener.onDownloadComplete(s, downloadStatus);
		}
		
		Log.d(TAG, "onPostExecute: ends");
	}
	
	@Override
	protected String doInBackground(String... strings)
	{
		String uriString = strings[0];
		
		if (uriString == null)
		{
			downloadStatus = DownloadStatus.NOT_INITIALIZED;
			return null;
		}
		
		BufferedReader bufferedReader = null;
		HttpURLConnection connection = null;
		
		try
		{
			URL url = new URL(uriString);
			connection = (HttpURLConnection) url.openConnection();
			
			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			
			downloadStatus = DownloadStatus.DOWNLOADING;
			
			while (null != (line = bufferedReader.readLine()))
			{
				stringBuilder.append(line);
			}
			
			downloadStatus = DownloadStatus.OK;
			
			return stringBuilder.toString();
			
			
		} catch (MalformedURLException e)
		{
			Log.e(TAG, "doInBackground: uri: " + uriString + " is invalid");
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
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				} catch (IOException e)
				{
					Log.e(TAG, "doInBackground: cannot close connection buffered reader: " + e.getMessage());
				}
			}
		}
		
		downloadStatus = DownloadStatus.ERROR;
		return null;
	}
}
