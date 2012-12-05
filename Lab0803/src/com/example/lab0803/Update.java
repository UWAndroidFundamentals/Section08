package com.example.lab0803;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Update extends Activity {

	// set log value
	public static final String tag = "log";

	// UI elements
	Button addButton;
	EditText EditName;
	EditText EditIsbn;
	EditText EditAuthor;

	// values passed from previous activity
	String passedID;
	String passedName;
	String passedIsbn;
	String passedAuthor;
	
	// hold current context
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen002);

		// set current context
		context = getApplicationContext();

		// pull input field definitions from XML
		EditName = (EditText) findViewById(R.id.EditName);
		EditIsbn = (EditText) findViewById(R.id.EditIsbn);
		EditAuthor = (EditText) findViewById(R.id.EditAuthor);
		
		// set passed values from intent
		passedID = this.getIntent().getExtras().getString("ID");  
		passedName = this.getIntent().getExtras().getString("NAME");
		passedIsbn = this.getIntent().getExtras().getString("ISBN");
		passedAuthor = this.getIntent().getExtras().getString("AUTHOR");
		
		// set default values for update fields to passed values of current book entry
		EditName.setText(passedName);
		EditIsbn.setText(passedIsbn);
		EditAuthor.setText(passedAuthor);
		
		// link the button to the XML definition and add listener
		addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// get the values the user entered
				String bookValue = EditName.getText().toString();
				String isbnValue = EditIsbn.getText().toString();
				String authorValue = EditAuthor.getText().toString();
				
				// create a holder for name / value pairs
				Log.d(tag, "Updating a book");
				ContentValues cv = new ContentValues();
				cv.put(BookProviderMetaData.BookTableMetaData.BOOK_NAME,
						bookValue);
				cv.put(BookProviderMetaData.BookTableMetaData.BOOK_ISBN,
						isbnValue);
				cv.put(BookProviderMetaData.BookTableMetaData.BOOK_AUTHOR,
						authorValue);

				// access the content resolver from the activity context
				ContentResolver cr = context.getContentResolver();
				
				// build the URI
				Uri uri = BookProviderMetaData.BookTableMetaData.CONTENT_URI;
				
				// add the passed id which is the item selected
				Uri updateUri = Uri.withAppendedPath(uri, passedID);
				Log.d(tag, "book updated uri:" + uri);
				
				// invoke update method on content provider 
				cr.update(updateUri, cv, null, null);
				Log.d(tag, "updated uri:" + updateUri);
				
				// close this activity
				finish();
			}
		});
		
	}

}

