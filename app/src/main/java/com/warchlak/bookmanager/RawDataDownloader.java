package com.warchlak.bookmanager;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;

public class RawDataDownloader extends AsyncTask<URI, Void, String>
{
	private static final String TAG = "RawDataDownloader";
	
	private DownloadStatus downloadStatus;
	private DownloadCompleteListener downloadListener;
	private static final int CONNECTION_TIMEOUT = 2000;
	
	enum DownloadStatus
	{
		IDLE, NOT_INITIALIZED, DOWNLOADING, OK, ERROR, TIMEOUT
	}
	
	public interface DownloadCompleteListener
	{
		void onDownloadComplete(String downloadedData, DownloadStatus downloadStatus);
	}
	
	RawDataDownloader(DownloadCompleteListener downloadListener)
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
	protected String doInBackground(URI... uris)
	{
		Log.d(TAG, "doInBackground: start with uri: " + uris[0]);
		
		URI uri = uris[0];
		if (uri == null)
		{
			downloadStatus = DownloadStatus.NOT_INITIALIZED;
			return null;
		}
		
		BufferedReader bufferedReader = null;
		HttpURLConnection connection = null;
		
		try
		{
			URL url = new URL(uri.toString());
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			Log.d(TAG, "doInBackground: connecting");
			
			Log.d(TAG, "doInBackground: responseCode is " + connection.getResponseCode());
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				Log.e(TAG, "doInBackground: ERROR: response code is: " + connection.getResponseCode());
				return null;
			}
			Log.d(TAG, "doInBackground: after response code");
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
			
		} catch (SocketTimeoutException e)
		{
			downloadStatus = DownloadStatus.TIMEOUT;
			Log.e(TAG, "doInBackground: connection timeout of: " + CONNECTION_TIMEOUT + " expired");
		} catch (MalformedURLException e)
		{
			downloadStatus = DownloadStatus.ERROR;
			Log.e(TAG, "doInBackground: uri: " + uri.toString() + " is invalid");
		} catch (IOException e)
		{
			downloadStatus = DownloadStatus.ERROR;
			Log.e(TAG, "doInBackground: Cannot open connection: " + e.getMessage());
		} catch (SecurityException e)
		{
			downloadStatus = DownloadStatus.ERROR;
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
		
		Log.d(TAG, "doInBackground: ends");
		return null;
	}
}
