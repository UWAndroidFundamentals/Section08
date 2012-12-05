package com.example.project0803;

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
	
	/*
	 * TODO: Add EditText for description
	 */
	
	// values passed from previous activity
	String passedID;
	String passedName;
	String passedIsbn;
	String passedAuthor;
	
	/*
	 * TODO: Add string for description
	 */
	
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
		
		/*
		 * TODO: Get XML definition for EditDescription
		 */
		
		// set passed values from intent
		passedID = this.getIntent().getExtras().getString("ID");  
		passedName = this.getIntent().getExtras().getString("NAME");
		passedIsbn = this.getIntent().getExtras().getString("ISBN");
		passedAuthor = this.getIntent().getExtras().getString("AUTHOR");
		
		/*
		 * TODO: set value passed from previous activity for description
		 */
			
		// set default values for update fields to passed values of current book entry
		EditName.setText(passedName);
		EditIsbn.setText(passedIsbn);
		EditAuthor.setText(passedAuthor);
		
		/*
		 * TODO: Set default test for edit field "description" to value passed into activity
		 */
		
		// link the button to the XML definition and add listener
		addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// get the values the user entered
				String bookValue = EditName.getText().toString();
				String isbnValue = EditIsbn.getText().toString();
				String authorValue = EditAuthor.getText().toString();
				/*
				 * TODO: set string value from EditText field
				 */

				/*
				 * TODO: Create ContentValues name / pair set and load with data
				 * Add a content resolver from the context
				 * Create a URI using CONTENT_URI from the meta data
				 * Append the passed id to the URI
				 * invoke update method on content provider
				 */

				
				// close this activity
				finish();
			}
		});
		
	}

}

