package com.warchlak.bookmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.warchlak.bookmanager.entity.Book;
import com.warchlak.bookmanager.util.BookRestApiUriHolder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements JsonDataParser.ParsingCompleteListener
{
	private static final String TAG = "MainActivity";
	public static final String TAGS_TO_SEARCH = "query_tags";
	public static final String SEARCH_REQUESTED = "search_requested";
	int pageSize;
	int pageNumber = 0;
	String lookupMethod;
	boolean usesAnyLookup;
	private String lastUsedUri;
	private BookViewRecyclerAdapter recyclerAdapter = new BookViewRecyclerAdapter();
	private RecyclerView recyclerView;
	private TextView emptyResultTextView;
	
	JsonDataParser jsonDataParser;
	
	private static final String LAST_URI_KEY = "lastUrl";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		applyUserPreferences();
		
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		recyclerView = findViewById(R.id.booksRecyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(recyclerAdapter);
		
		emptyResultTextView = findViewById(R.id.emptyResultTextView);
		
		try
		{
			jsonDataParser = new JsonDataParser(new URI(lastUsedUri), this);
			jsonDataParser.start();
		} catch (URISyntaxException e)
		{
			Toast.makeText(this, R.string.invalid_last_uri,
					Toast.LENGTH_LONG).show();
		}
		
	}
	
	@Override
	public void onParsingComplete(List<Book> parsedData, JsonDataParser.ParsingStatus status)
	{
		if (parsedData == null || parsedData.size() <= 0)
		{
			recyclerView.setVisibility(View.GONE);
			emptyResultTextView.setVisibility(View.VISIBLE);
		}
		if (status == JsonDataParser.ParsingStatus.OK)
		{
			emptyResultTextView.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
			recyclerAdapter.changeDataSet(parsedData);
		}
		else
		{
			String errorMessage;
			if (status == JsonDataParser.ParsingStatus.TIMEOUT)
			{
				errorMessage = getString(R.string.server_timeout);
			}
			else
			{
				errorMessage = getString(R.string.dataLoadingError);
			}
			
			View rootView = findViewById(R.id.mainRootLayout);
			Snackbar.make(rootView, errorMessage, Snackbar.LENGTH_INDEFINITE).show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch (id)
		{
			case R.id.menuAddNewBook:
				runNewBookActivity();
				return true;
			
			case R.id.action_settings:
				Intent intent = new Intent(this, SettingActivity.class);
				startActivity(intent);
				return true;
			
			case R.id.app_bar_search:
				Log.d(TAG, "onOptionsItemSelected: searching selected");
				boolean searchRequested = onSearchRequested();
				Log.d(TAG, "onOptionsItemSelected: onSearchRequested: " + searchRequested);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPostResume()
	{
		Log.d(TAG, "onPostResume: start");
		super.onPostResume();
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		applyUserPreferences();
		
		if (sharedPreferences.getBoolean(SEARCH_REQUESTED, false))
		{
			Log.d(TAG, "onPostResume: starting new downloading after search requested");
			
			sharedPreferences.edit().putBoolean(SEARCH_REQUESTED, false).apply();
			
			String query = sharedPreferences.getString(TAGS_TO_SEARCH, null);
			Log.d(TAG, "onPostResume: query is: " + query);
			if (query != null)
			{
				query = query.trim();
			}
			
			URI uri = BookRestApiUriHolder.buildPageUri(pageNumber, pageSize, query, lookupMethod);
			jsonDataParser.setUri(uri);
			jsonDataParser.start();
		}
		Log.d(TAG, "onPostResume: end");
		
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		sharedPreferences.edit().putString(LAST_URI_KEY, BookRestApiUriHolder.lastUsedUri).apply();
	}
	
	private void runNewBookActivity()
	{
		Intent intent = new Intent(this, AddNewActivity.class);
		startActivity(intent);
	}
	
	public void applyUserPreferences()
	{
		Log.d(TAG, "applyUserPreferences: starts");
		
		PreferenceManager.setDefaultValues(this, R.xml.default_preferences, false);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		try
		{
			pageSize = Integer.parseInt(
					sharedPreferences.getString(SettingActivity.PAGE_SIZE_PREFERENCE_KEY,
							SettingActivity.PAGE_SIZE_DEFAULT_VALUE));
		} catch (NumberFormatException e)
		{
			Toast.makeText(this, R.string.invalid_page_size, Toast.LENGTH_LONG).show();
			pageSize = Integer.parseInt(SettingActivity.PAGE_SIZE_DEFAULT_VALUE);
		}
		
		usesAnyLookup = sharedPreferences.getBoolean(SettingActivity.LOOKUP_METHOD_KEY, false);
		
		if (usesAnyLookup)
		{
			lookupMethod = BookRestApiUriHolder.TagSearchMethod.ANY;
		}
		else
		{
			lookupMethod = BookRestApiUriHolder.TagSearchMethod.ALL;
		}
		
		lastUsedUri = sharedPreferences.getString(LAST_URI_KEY,
				BookRestApiUriHolder.DEFAULT_URI_STRING);
		
		Log.d(TAG, "applyUserPreferences: pageSize: " + pageSize +
				"\nusesAnyLookup: " + usesAnyLookup +
				"\nlookupMethod: " + lookupMethod +
				"\nlastUsedUri: " + lastUsedUri);
		
		Log.d(TAG, "applyUserPreferences: ends");
	}
}

