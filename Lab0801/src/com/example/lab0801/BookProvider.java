package com.example.lab0801;

import java.util.HashMap;

import com.example.lab0801.BookProviderMetaData.BookTableMetaData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BookProvider extends ContentProvider
{
	// set logging tag
    private static final String TAG = "log";

    // project is a list of columns - similar to "as" construct in SQL
    private static HashMap<String, String> sBooksProjectionMap;
    static 
    {
    	// in this case, all SQL fields and provider columns match
    	sBooksProjectionMap = new HashMap<String, String>();

    	// id field
    	sBooksProjectionMap.put(BookTableMetaData._ID, 
    			                BookTableMetaData._ID);
    	
    	// name field
    	sBooksProjectionMap.put(BookTableMetaData.BOOK_NAME, 
    			                BookTableMetaData.BOOK_NAME);
    	
    	// Isbn field
    	sBooksProjectionMap.put(BookTableMetaData.BOOK_ISBN, 
    			                BookTableMetaData.BOOK_ISBN);
    	
    	// Author field
    	sBooksProjectionMap.put(BookTableMetaData.BOOK_AUTHOR, 
    			                BookTableMetaData.BOOK_AUTHOR);
    	
    	// created date field
    	sBooksProjectionMap.put(BookTableMetaData.CREATED_DATE, 
    			                BookTableMetaData.CREATED_DATE);
    	
    	// modified date field
    	sBooksProjectionMap.put(BookTableMetaData.MODIFIED_DATE, 
    			                BookTableMetaData.MODIFIED_DATE);
    }

    // provide a mechanism to identify all the incoming uri patterns.
    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_BOOK_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_BOOK_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books", 
        		          INCOMING_BOOK_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(BookProviderMetaData.AUTHORITY, "books/#", 
        		          INCOMING_SINGLE_BOOK_URI_INDICATOR);
    }

    // this class helps open, create, and upgrade the database file
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, 
            	BookProviderMetaData.DATABASE_NAME, 
            	null, 
            	BookProviderMetaData.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
        	Log.d(TAG,"inner oncreate called");
            db.execSQL("CREATE TABLE " + BookTableMetaData.TABLE_NAME + " ("
                    + BookTableMetaData._ID + " INTEGER PRIMARY KEY,"
                    + BookTableMetaData.BOOK_NAME + " TEXT,"
                    + BookTableMetaData.BOOK_ISBN + " TEXT,"
                    + BookTableMetaData.BOOK_AUTHOR + " TEXT,"
                    + BookTableMetaData.CREATED_DATE + " INTEGER,"
                    + BookTableMetaData.MODIFIED_DATE + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
        	Log.d(TAG,"inner onupgrade called");
            Log.w(TAG, "Upgrading database from version " 
            		+ oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + 
            		 BookTableMetaData.TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    
    // called when the provider is first accessed
    @Override
    public boolean onCreate() 
    {
    	Log.d(TAG,"main onCreate called");
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    // accepts parameters similar to the SQL query, but also allows decoding the 
    // URI through the URI match method
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, 
    		String[] selectionArgs,  String sortOrder) 
    {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        
        // this is a multiple entry request
        case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
            qb.setTables(BookTableMetaData.TABLE_NAME);
            qb.setProjectionMap(sBooksProjectionMap);
            break;

        // this is a single entry request     
        case INCOMING_SINGLE_BOOK_URI_INDICATOR:
            qb.setTables(BookTableMetaData.TABLE_NAME);
            qb.setProjectionMap(sBooksProjectionMap);
            
            // pull from the path segment the individual item reference
            qb.appendWhere(BookTableMetaData._ID + "=" 
            		    + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // if no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = BookTableMetaData.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, 
        		   selectionArgs, null, null, orderBy);
   
        // tell the cursor what uri to watch so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    // set the MIME type - use constants from the meta data object
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
            return BookTableMetaData.CONTENT_TYPE;

        case INCOMING_SINGLE_BOOK_URI_INDICATOR:
            return BookTableMetaData.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    // inserts entry into the provider 
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        
    	// validate the requested uri
        if (sUriMatcher.match(uri) 
        		!= INCOMING_BOOK_COLLECTION_URI_INDICATOR) 
        {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // create name/value holder for the passed values, if any
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // get current time
        Long now = Long.valueOf(System.currentTimeMillis());

        // make sure that the fields are all set
        if (values.containsKey(BookTableMetaData.CREATED_DATE) == false) 
        {
            values.put(BookTableMetaData.CREATED_DATE, now);
        }

        if (values.containsKey(BookTableMetaData.MODIFIED_DATE) == false) 
        {
            values.put(BookTableMetaData.MODIFIED_DATE, now);
        }

        if (values.containsKey(BookTableMetaData.BOOK_NAME) == false) 
        {
            throw new SQLException(
               "Failed to insert row because Book Name is needed " + uri);
        }

        if (values.containsKey(BookTableMetaData.BOOK_ISBN) == false) {
            values.put(BookTableMetaData.BOOK_ISBN, "Unknown ISBN");
        }
        if (values.containsKey(BookTableMetaData.BOOK_AUTHOR) == false) {
            values.put(BookTableMetaData.BOOK_ISBN, "Unknown Author");
        }

        // open the database for writing
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        // insert the name / value pairs into the provider
        long rowId = db.insert(BookTableMetaData.TABLE_NAME, 
        		BookTableMetaData.BOOK_NAME, values);
        
        // if insert went okay (returned the row ID)
        if (rowId > 0) {
        	
        	// build the URI
            Uri insertedBookUri = 
            	ContentUris.withAppendedId(
            			BookTableMetaData.CONTENT_URI, rowId);
            
            // rebuild the list internal to the content provider
            getContext()
               .getContentResolver()
                    .notifyChange(insertedBookUri, null);
            
            // return the URI
            return insertedBookUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    // deletes entry in provider
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        
    	// access db for write access
    	SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        
        // batch delete
        case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
            count = db.delete(BookTableMetaData.TABLE_NAME, 
            		where, whereArgs);
            break;

        // single item delete    
        case INCOMING_SINGLE_BOOK_URI_INDICATOR:
            String rowId = uri.getPathSegments().get(1);
            count = db.delete(BookTableMetaData.TABLE_NAME, 
            		BookTableMetaData._ID + "=" + rowId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), 
                    whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // notify provider to rebuild list
        getContext().getContentResolver().notifyChange(uri, null);
        
        // return rows affected 
        return count;
    }

    // update the provider
    @Override
    public int update(Uri uri, ContentValues values, 
    		String where, String[] whereArgs) 
    {
    	// open the database for write since we are doing updates
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        
        // update multiple rows
        switch (sUriMatcher.match(uri)) {
        case INCOMING_BOOK_COLLECTION_URI_INDICATOR:
            count = db.update(BookTableMetaData.TABLE_NAME, 
            		values, where, whereArgs);
            break;

        // update single row, but allow for "where" clause too    
        case INCOMING_SINGLE_BOOK_URI_INDICATOR:
            String rowId = uri.getPathSegments().get(1);
            count = db.update(BookTableMetaData.TABLE_NAME, 
            		values, BookTableMetaData._ID + "=" + rowId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), 
                    whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // notify provider to rebuild list
        getContext().getContentResolver().notifyChange(uri, null);
        
        // return number of records updated
        return count;
    }
}
