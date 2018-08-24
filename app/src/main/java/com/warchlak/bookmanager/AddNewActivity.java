package com.warchlak.bookmanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class AddNewActivity extends AppCompatActivity
{
	private static final String TAG = "AddNewActivity";
	
	static final int REQUEST_CODE_IMAGE_GET = 1;
	
	ImageButton selectedPhotoButton;
	Uri photoUri;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
				        .setAction("Action", null).show();
			}
		});
		
		selectedPhotoButton = findViewById(R.id.selectedPhotoButton);
		
		selectedPhotoButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(TAG, "onClick: button clicked, starting intent");
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				if (intent.resolveActivity(getPackageManager()) != null)
				{
					startActivityForResult(intent, REQUEST_CODE_IMAGE_GET);
				}
				Log.d(TAG, "onClick: intent started");
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(TAG, "onActivityResult: started");
		if (requestCode == REQUEST_CODE_IMAGE_GET && resultCode == Activity.RESULT_OK)
		{
			Log.d(TAG, "onActivityResult: getting data");
			photoUri = data.getData();
			if (photoUri != null)
			{
				Log.d(TAG, "onActivityResult: data Uri is " + photoUri.toString());
				
				selectedPhotoButton.setImageURI(photoUri);
			}
			else
			{
				Toast.makeText(this, R.string.photoLoadingError, Toast.LENGTH_LONG).show();
			}
			Log.d(TAG, "onActivityResult: ends");
		}
	}
	
}
