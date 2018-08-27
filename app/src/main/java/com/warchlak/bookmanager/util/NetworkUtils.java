package com.warchlak.bookmanager.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.bikomobile.multipart.Multipart;

import java.util.HashMap;
import java.util.Map;

import static com.warchlak.bookmanager.util.BookRestApiUriHolder.FILE_PARAM_NAME;

public class NetworkUtils
{
	private static final String TAG = "NetworkUtils";
	
	public static void sendMultipartFileRequest(final Context context, Uri photoUri,
	                                            Response.Listener<NetworkResponse> listener)
	{
		Multipart multipart = new Multipart(context);
		
		String contentType = MediaTypeResolver.getMediaType(photoUri.getLastPathSegment());
		Log.d(TAG, "sendMultipartFileRequest: last part segment of photoUri is " + photoUri.getLastPathSegment());
		
		multipart.addFile(contentType, FILE_PARAM_NAME, "file.png", photoUri);
		multipart.launchRequest("http://192.168.1.234:8081/api/images/", listener,
				error ->
				{
					Log.e(TAG, "onErrorResponse: error posting image");
					Toast.makeText(context, "Error during image uploading", Toast.LENGTH_LONG).show();
				});
	}
	
	public static class MediaTypeResolver
	{
		private final static Map<String, String> mediaTypesMap;
		
		private static final String IMAGE_PNG = "image/png";
		private static final String IMAGE_JPG = "image/jpeg";
		private static final String IMAGE_GIF = "image/gif";
		
		static
		{
			mediaTypesMap = new HashMap<>();
			mediaTypesMap.put("jpg", IMAGE_JPG);
			mediaTypesMap.put("png", IMAGE_PNG);
			mediaTypesMap.put("gif", IMAGE_GIF);
			mediaTypesMap.put("JPG", IMAGE_JPG);
			mediaTypesMap.put("PNG", IMAGE_PNG);
			mediaTypesMap.put("GIF", IMAGE_GIF);
		}
		
		private MediaTypeResolver()
		{
		}
		
		static String getMediaType(String filename)
		{
			String extension = ExtensionExtractor.getExtension(filename);
			return mediaTypesMap.get(extension);
		}
		
		public static boolean isImage(String fileExtension)
		{
			return mediaTypesMap.containsKey(fileExtension);
		}
	}
	
	public static class ExtensionExtractor
	{
		public static String getExtension(String filename)
		{
			if (filename != null)
			{
				String[] fileNameSplit = filename.split("\\.");
				if (fileNameSplit.length != 2)
				{
					return null;
				}
				else
				{
					return fileNameSplit[1];
				}
			}
			return null;
		}
	}
}
