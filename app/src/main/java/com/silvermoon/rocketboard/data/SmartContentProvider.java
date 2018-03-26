package com.silvermoon.rocketboard.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class SmartContentProvider extends ContentProvider {

    private static final String TAG = SmartContentProvider.class.getSimpleName();
    private static final int USER_ACTION = 100;
    private static final int USER_ACTION_WITH_KEY =101;
    private static final int USER_ACTION_WITH_ID =102;
    private SmartKeyDBHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://com.silvermoon.smartkeyboard/user_action
        sUriMatcher.addURI(SmartKeyContract.CONTENT_AUTHORITY,
                SmartKeyContract.TABLE_USER_ACTION,
                USER_ACTION);

        // content://com.silvermoon.smartkeyboard/user_action/key_name
        sUriMatcher.addURI(SmartKeyContract.CONTENT_AUTHORITY,
                SmartKeyContract.TABLE_USER_ACTION + "/#",
                USER_ACTION_WITH_KEY);
        // content://com.silvermoon.smartkeyboard/user_action/id
        sUriMatcher.addURI(SmartKeyContract.CONTENT_AUTHORITY,
                SmartKeyContract.TABLE_USER_ACTION + "/#",
                USER_ACTION_WITH_ID);

    }


    public SmartContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new SmartKeyDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)){
            case USER_ACTION_WITH_ID:
                long id  = ContentUris.parseId(uri);
                selection = String.format("%s = ?", SmartKeyContract.UserActionColumns._ID);
                selectionArgs = new String[]{String.valueOf(id)};
                break;
            case USER_ACTION:
                selection = String.format("%s = ?", SmartKeyContract.UserActionColumns.isAssigned);
                break;

            case UriMatcher.NO_MATCH:
                Log.d(TAG, "NO MATCH FOR THIS URI IN SCHEME: " + uri);
                break;
            default:
                Log.d(TAG, "INVALID URI - URI NOT RECOGNIZED: "  + uri);

        }

        cursor = sqLiteDatabase.query(SmartKeyContract.TABLE_USER_ACTION,null,selection,selectionArgs,null,null,sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int returnValue=0;
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)){
            case USER_ACTION_WITH_ID:
                String whereClause = String.format("%s = ?", SmartKeyContract.UserActionColumns._ID);
                returnValue = sqLiteDatabase.update(SmartKeyContract.TABLE_USER_ACTION,values,whereClause,selectionArgs);
        }

        return returnValue;
    }
}
