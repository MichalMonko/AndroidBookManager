package com.warchlak.bookmanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.warchlak.bookmanager.entity.Book;
import com.warchlak.bookmanager.util.NetworkUtils;

import java.net.HttpURLConnection;
import java.util.Locale;

public class AddNewActivity extends AppCompatActivity implements Response.Listener<NetworkResponse>, BookPostManager.SendingCompleteListener
{
	private static final String TAG = "AddNewActivity";
	
	private String photoName;
	
	static final int REQUEST_CODE_IMAGE_GET = 1;
	
	ImageButton selectedPhotoButton;
	Button sendButton;
	
	TextView titleView;
	TextView descriptionView;
	TextView tagsView;
	TextView priceView;
	
	Uri photoUri;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		titleView = findViewById(R.id.bookTitle);
		descriptionView = findViewById(R.id.bookDescription);
		tagsView = findViewById(R.id.bookTags);
		
		selectedPhotoButton = findViewById(R.id.selectedPhotoButton);
		selectedPhotoButton.setOnClickListener(v ->
		{
			Log.d(TAG, "onClick: button clicked, starting intent");
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			if (intent.resolveActivity(getPackageManager()) != null)
			{
				startActivityForResult(intent, REQUEST_CODE_IMAGE_GET);
			}
			Log.d(TAG, "onClick: intent started");
		});
		
		sendButton = findViewById(R.id.sendButton);
		sendButton.setOnClickListener(view -> uploadBook());
		
		priceView = findViewById(R.id.bookPrice);
		priceView.setOnFocusChangeListener((v, hasFocus) ->
		{
			if (!hasFocus)
			{
				String preFormattedText = priceView.getText().toString();
				String formattedPrice = String.format(Locale.getDefault(), "%.2f", Double.parseDouble(preFormattedText));
				priceView.setText(formattedPrice);
			}
		});
		
	}
	
	private void uploadBook()
	{
		Book book = createBook();
		BookPostManager bookUploader = new BookPostManager(this);
		bookUploader.execute(book);
	}
	
	private Book createBook()
	{
		Book book = new Book();
		book.setTitle(titleView.getText().toString());
		book.setDescription(descriptionView.getText().toString());
		book.setTags(tagsView.getText().toString());
		Log.d(TAG, "createBook: parsed price is " + Double.parseDouble(priceView.getText().toString()));
		book.setPrice(Double.parseDouble(priceView.getText().toString()));
		book.setImageLink(photoName);
		
		return book;
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
				
				NetworkUtils.sendMultipartFileRequest(getApplicationContext(), photoUri, this);
				
			}
			else
			{
				Toast.makeText(this, R.string.photoLoadingError, Toast.LENGTH_LONG).show();
			}
			Log.d(TAG, "onActivityResult: ends");
		}
	}
	
	@Override
	public void onResponse(NetworkResponse response)
	{
		Log.d(TAG, "onResponse: response is: " + response.statusCode);
		if (response.statusCode != HttpURLConnection.HTTP_OK)
		{
			Toast.makeText(this, "Cannot send photo to the server", Toast.LENGTH_LONG).show();
		}
		else
		{
			photoName = new String(response.data);
		}
	}
	
	@Override
	public void onSendingComplete(BookPostManager.SendingStatus sendingStatus)
	{
		if (sendingStatus != BookPostManager.SendingStatus.OK)
		{
			Toast.makeText(this, "Book cannot be uploaded", Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(this, "Book uploaded", Toast.LENGTH_LONG).show();
			finish();
		}
	}
}
