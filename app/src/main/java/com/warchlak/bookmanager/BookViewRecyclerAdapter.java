package com.warchlak.bookmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.warchlak.bookmanager.entity.Book;
import com.warchlak.bookmanager.util.BookRestApiUriHolder;

import java.util.ArrayList;
import java.util.List;

public class BookViewRecyclerAdapter extends RecyclerView.Adapter<BookViewRecyclerAdapter.BookViewHolder>
{
	private static final String TAG = "BookViewRecyclerAdapter";
	
	private List<Book> booksData;
	
	class BookViewHolder extends RecyclerView.ViewHolder
	{
		ImageView coverView;
		TextView titleView;
		TextView descriptionView;
		TextView tagsView;
		TextView priceView;
		
		BookViewHolder(View itemView)
		{
			super(itemView);
			this.coverView = itemView.findViewById(R.id.coverView);
			this.titleView = itemView.findViewById(R.id.titleView);
			this.descriptionView = itemView.findViewById(R.id.descriptionView);
			this.tagsView = itemView.findViewById(R.id.tagsView);
			this.priceView = itemView.findViewById(R.id.priceView);
		}
	}
	
	 BookViewRecyclerAdapter()
	{
		this.booksData = new ArrayList<>();
	}
	
	@NonNull
	@Override
	public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_browser_layout, parent, false);
		return new BookViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull BookViewHolder holder, int position)
	{
		if (booksData == null || booksData.size() <= 0)
		{
			Log.d(TAG, "onBindViewHolder: data set is empty ");
			holder.titleView.setText(R.string.emptyDataSet);
			holder.coverView.setImageResource(R.drawable.image_placeholder_icon);
		}
		else
		{
			Book book = getBook(position);
			if (book != null)
			{
				String priceString = String.valueOf(book.getPrice()) + "$";
				holder.titleView.setText(book.getTitle());
				holder.tagsView.setText(book.getTags());
				holder.priceView.setText(priceString);
				holder.descriptionView.setText(book.getDescription());
				
				String imageName = book.getImageLink();
				if (null != imageName)
				{
					Picasso.get()
					       .load(BookRestApiUriHolder.buildPhotoUri(imageName))
					       .error(R.drawable.broken_image)
					       .placeholder(R.drawable.image_placeholder_icon)
					       .into(holder.coverView);
				}
			}
			else
			{
				Log.e(TAG, "onBindViewHolder: Book is null!");
			}
		}
	}
	
	public void changeDataSet(List<Book> booksData)
	{
		this.booksData = booksData;
		notifyDataSetChanged();
	}
	
	private Book getBook(int position)
	{
		return (booksData != null && booksData.size() >= position + 1) ? booksData.get(position) : null;
	}
	
	@Override
	public int getItemCount()
	{
		return booksData.size();
	}
}
