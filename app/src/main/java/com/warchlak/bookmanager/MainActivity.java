package com.warchlak.bookmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements RawDataDownloader.DownloadCompleteListener
{
	private static final String TAG = "MainActivity";
	public static final String DOWNLOAD_RESOURCE_LOCATION = "http://77.254.184.154:8081/api/book";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		RawDataDownloader rawDataDownloader = new RawDataDownloader(this);
		rawDataDownloader.execute(DOWNLOAD_RESOURCE_LOCATION);
		
//		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//		fab.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View view)
//			{
//				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//				        .setAction("Action", null).show();
//			}
//		});
	}
	
	@Override
	public void onDownloadComplete(String downloadedData, RawDataDownloader.DownloadStatus downloadStatus)
	{
		Log.d(TAG, "onDownloadComplete: started");
		
		if (downloadStatus == RawDataDownloader.DownloadStatus.OK)
		{
			Log.d(TAG, "onDownloadComplete: \n\ndownloaded Data: " + downloadedData + "\n\n");
			TextView textView = findViewById(R.id.initialTextView);
			textView.setText(downloadedData);
		}
		else
		{
			Log.e(TAG, "onDownloadComplete: downloading failed with status: " + downloadStatus.name());
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
		
		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}

