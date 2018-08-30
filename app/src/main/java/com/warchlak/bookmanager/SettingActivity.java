package com.warchlak.bookmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity
{
	public static final String PAGE_SIZE_DEFAULT_VALUE = "10";
	public static final String LOOKUP_METHOD_KEY = "lookup_method";
	public static String PAGE_SIZE_PREFERENCE_KEY = "pageSize";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		getSupportFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();
	}
	
}
