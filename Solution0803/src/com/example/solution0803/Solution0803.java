package com.example.solution0803;

import java.util.ArrayList;


import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Solution0803 extends Activity {

	// set log value
	public static final String tag = "log";
	
	// create an array list of book objects
	ArrayList<Book> Books = new ArrayList<Book>();

	// UI elements
	Button addButton;
	ListView listview;
	EditText EditName;
	EditText EditIsbn;
	EditText EditAuthor;
	
	/*
	 * TODO: Add EditText field for description
	 */
	EditText EditDescription;

	// hold current context
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen001);
		
		// set current context
		context = getApplicationContext();

		// link edit fields to definitions
		EditName = (EditText) findViewById(R.id.EditName);
		EditIsbn = (EditText) findViewById(R.id.EditIsbn);
		EditAuthor = (EditText) findViewById(R.id.EditAuthor);
		
		/*
		 * TODO: get EditText layout from XML
		 */
		
		EditDescription = (EditText) findViewById(R.id.EditDescription);

		// link the add button to the XML definition and add listener
		addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// pull values entered
				String bookValue = EditName.getText().toString();
				String isbnValue = EditIsbn.getText().toString();
				String authorValue = EditAuthor.getText().toString();
		
				/*
				 * TODO: Extract string value from field
				 */
				
				String descriptionValue = EditDescription.getText().toString();

				// create name / value pairs for each of the three values
				Log.d(tag, "Adding a book");
				ContentValues cv = new ContentValues();
				cv.put(BookProviderMetaData.BookTableMetaData.BOOK_NAME,
						bookValue);
				cv.put(BookProviderMetaData.BookTableMetaData.BOOK_ISBN,
						isbnValue);
				cv.put(BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR,
						authorValue);

				/*
				 * TODO: Add BOOL_DESCRIPTION to cv
				 */
				
				cv.put(BookProviderMetaData.BookTableMetaData.BOOK_DESCRIPTION,
						descriptionValue);
				
				// get the content resolver from the current activity context
				ContentResolver cr = context.getContentResolver();
				
				// build the URI
				Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;
				
				// invoke the insert method of the content resolver 
				Log.d(tag, "book insert uri:" + uri);
				Uri insertedUri = cr.insert(uri, cv);
				Log.d(tag, "inserted uri:" + insertedUri);
				
				// update the UI list view element
				SetList();

			}
		});

		// define list view from XML entry
		listview = (ListView) findViewById(R.id.list);

		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				// get the id of the element listed
				int i = Integer.parseInt(Books.get(position).ID);
				
				// get the content resolver from the current activity context
				ContentResolver cr = context.getContentResolver();
				
				// build URI
				Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;
				
				// append the id to the URI
				Uri delUri = Uri.withAppendedPath(uri, Integer.toString(i));
				Log.v("log", "Del Uri:" + delUri);
				
				// invoke the delete method of the content resolver
				cr.delete(delUri, null, null);
				Log.v("log", "Newcount:" + getCount());

				// update the UI list view element
				SetList();

				// indicate that the long press handled the touch event
				return true;
			}
		});

		// detect a short click on the list view element
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				// create explicit intent
				Intent temp = new Intent(context, Update.class);
				
				// add book elements to the intent
				temp.putExtra("ID", Books.get(position).ID);
				temp.putExtra("NAME", Books.get(position).BOOK_NAME);
				temp.putExtra("ISBN", Books.get(position).BOOK_ISBN);
				temp.putExtra("AUTHOR", Books.get(position).BOOK_AUTHOR);
				temp.putExtra("DESCRIPTION", Books.get(position).BOOK_DESCRIPTION);
				
				// start the new activity and pass the intent with book elements
				startActivity(temp);

			}
		});
		
		
		
	}

	// if the activity is brought to the foreground, make sure the list view is up to date
	@Override
	public void onResume() {
		super.onResume();
		SetList();
	}

	// load the list view with the most up to date information from the provider
	private void SetList() {
		
		// reset the array list of book objects
		Books = new ArrayList<Book>();

		// set URI
		Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;

		// get the content resolve from the activity context
		ContentResolver cr = context.getContentResolver();

		// get the full list of entries in the provider 
		Cursor c = cr.query(uri, null, // projection
				null, // selection string
				null, // selection args array of strings
				null); // sort order

		// pull indexes from cursor for all elements
		int iname = c
				.getColumnIndex(BookProviderMetaData.BookTableMetaData.BOOK_NAME);
		int iisbn = c
				.getColumnIndex(BookProviderMetaData.BookTableMetaData.BOOK_ISBN);
		int iauthor = c
				.getColumnIndex(BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR);

		/*
		 * TODO: get column index for BOOK_DESCRIPTION
		 */

		int idescription = c
				.getColumnIndex(BookProviderMetaData.BookTableMetaData.BOOK_DESCRIPTION);
		
		// Report your indexes
		Log.v("log", "display indexes = " + iname + iisbn + iauthor + idescription);

		// walk through the rows based on indexes
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			// load cursor values into book object
			Book book = new Book();
			book.ID = c.getString(1);
			book.BOOK_NAME = c.getString(iname);
			book.BOOK_ISBN = c.getString(iisbn);
			book.BOOK_AUTHOR = c.getString(iauthor);

			/*
			 * TODO: set book object BOOK_DESCRIPTION
			 */
			
			book.BOOK_DESCRIPTION = c.getString(idescription);
			
			// add book object to book array list
			Books.add(book);
		}

		// Report how many rows have been read
		int numberOfRecords = c.getCount();
		Log.v("log", "Num of Records:" + numberOfRecords);

		// Close the cursor
		c.close();

		// create fully custom adapter 
		EfficientAdapter myAdapter = new EfficientAdapter(context);
		myAdapter.notifyDataSetChanged();
		listview.setAdapter(myAdapter);

	}

	// get count of entries in the content provider
	private int getCount() {
		Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;
		ContentResolver cr = context.getContentResolver();

		Cursor c = cr.query(uri, null, // projection
				null, // selection string
				null, // selection args array of strings
				null); // sort order

		int numberOfRecords = c.getCount();
		c.close();
		return numberOfRecords;
	}

	// custom adapter 
	class EfficientAdapter extends BaseAdapter {
		
		// use inflater for each individual view
		private LayoutInflater mInflater;

		// constructor accepts context to get inflater 
		public EfficientAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		// returns the count of the list view
		public int getCount() {
			return Books.size();
		}

		// return the current position
		public Object getItem(int position) {
			return position;
		}

		// return the current item
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;

			// inflate a new view if needed
			if (convertView == null) {
				
				// use custom "row" entry for view
				convertView = mInflater.inflate(R.layout.row, null);
				convertView.setId(position);
				
				// link elements to "row" view 
				holder = new ViewHolder();
				holder.id = (TextView) convertView.findViewById(R.id.id);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.isbn = (TextView) convertView.findViewById(R.id.isbn);
				holder.author = (TextView) convertView
						.findViewById(R.id.author);
				/*
				 * TODO: Add description from XML defintion
				 */
				
				holder.description = (TextView) convertView
						.findViewById(R.id.description);
				
				convertView.setTag(holder);
			} else {
				// get a previously inflated view
				holder = (ViewHolder) convertView.getTag();
			}
			
			// set the value for each element in the view from the driving array
			// note that this gives you control over how each element can be displayed
			// this is where much of the power of a custom list view is found
			// in this case we are simply setting the value for each element
			holder.id.setText(Books.get(position).ID);
			holder.name.setText(Books.get(position).BOOK_NAME);
			holder.isbn.setText(Books.get(position).BOOK_ISBN);
			holder.author.setText(Books.get(position).BOOK_AUTHOR);
			
			/*
			 * set value of description
			 */
			
			holder.description.setText(Books.get(position).BOOK_DESCRIPTION);
			
			return convertView;
		}

		// internal view class
		class ViewHolder {
			TextView id;
			TextView name;
			TextView isbn;
			TextView author;
			
			/*
			 * add TextView for description
			 */
			
			TextView description;
		}
	}


}
