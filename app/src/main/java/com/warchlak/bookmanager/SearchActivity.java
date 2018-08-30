package com.warchlak.bookmanager;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SearchActivity extends AppCompatActivity
{
	private static final String TAG = "SearchActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate: begin");
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_search);
		
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.d(TAG, "onCreate: query: " + query);
			
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			sharedPreferences.edit().putString(MainActivity.TAGS_TO_SEARCH, query)
			                 .putBoolean(MainActivity.SEARCH_REQUESTED, true).apply();
		}
		Log.d(TAG, "onCreate: ends");
		finish();
	}
}
