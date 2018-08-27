package com.warchlak.bookmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.warchlak.bookmanager.entity.Book;
import com.warchlak.bookmanager.util.BookRestApiUriHolder;

import java.util.List;

public class MainActivity extends AppCompatActivity implements JsonDataParser.ParsingCompleteListener
{
	private static final String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		ImageView imageView = findViewById(R.id.placeholderImage);
		Picasso.get()
		       .load(Uri.parse(BookRestApiUriHolder.BASE_IMAGE_URL + "ee954570-99b6-4413-b03d-084c57512fbe.png"))
		       .placeholder(R.drawable.image_placeholder_icon)
		       .error(R.drawable.broken_image)
		       .into(imageView, new Callback()
		       {
			       @Override
			       public void onSuccess()
			       {
				
			       }
			
			       @Override
			       public void onError(Exception e)
			       {
				       Log.e(TAG, "onError: " + e.getMessage());
				       e.printStackTrace();
			       }
		       });
	}
	
	@Override
	public void onParsingComplete(List<Book> parsedData, JsonDataParser.ParsingStatus status)
	{
		for (Book book : parsedData)
		{
			Toast.makeText(this, book.toString(), Toast.LENGTH_LONG).show();
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

