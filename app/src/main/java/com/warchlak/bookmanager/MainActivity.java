package com.warchlak.bookmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.warchlak.bookmanager.entity.Book;
import com.warchlak.bookmanager.util.BookRestApiUriHolder;

import java.util.List;

public class MainActivity extends AppCompatActivity implements JsonDataParser.ParsingCompleteListener
{
	private static final String TAG = "MainActivity";
	private BookViewRecyclerAdapter recyclerAdapter = new BookViewRecyclerAdapter();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		RecyclerView recyclerView = findViewById(R.id.booksRecyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(recyclerAdapter);
		
		JsonDataParser jsonDataParser = new JsonDataParser(BookRestApiUriHolder.buildPageUri(0, 20, null,
				BookRestApiUriHolder.TagSearchMethod.ANY), this);
		jsonDataParser.start();
	}
	
	@Override
	public void onParsingComplete(List<Book> parsedData, JsonDataParser.ParsingStatus status)
	{
		if (status == JsonDataParser.ParsingStatus.OK)
		{
//			for (Book book : parsedData)
//			{
//				TextView textView = findViewById(R.id.restResult);
//				Log.d(TAG, "onParsingComplete: *******************\n\n BOOK: " + book.toString());
//				textView.setText(parsedData.toString());
//			}
			recyclerAdapter.changeDataSet(parsedData);
		}
		else
		{
			View rootView = findViewById(R.id.mainRootLayout);
			Snackbar.make(rootView, R.string.dataLoadingError, Snackbar.LENGTH_INDEFINITE).show();
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void runNewBookActivity()
	{
		Intent intent = new Intent(this, AddNewActivity.class);
		startActivity(intent);
	}
}

