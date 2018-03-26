package com.silvermoon.rocketboard.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by faith on 9/29/2017.
 */

public class SmartKeyContract {

    public static final String TABLE_USER_ACTION ="user_action";

    public static final class UserActionColumns implements BaseColumns{
        //Key name
        public static final String keyName = "key_name";
        //Key Id
        public static final String keyId ="key_id";
        //Package name
        public static final String packageName = "package_name";
        //Checks whether the key is already in use
        public static final String isAssigned = "isAssigned";
    }

     //Authority string for the content provider
    public static final String CONTENT_AUTHORITY = "com.silvermoon.smartkeyboard";
    public static final String COUNT = "count";
    public static final int USERACTION =100;
    public static final int USERACTION_WITH_ID =101;

    public static final String SORT_ORDER = String.format("%s ASC",UserActionColumns._ID);

    //Base content Uri for accessing the provider
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_USER_ACTION)
            .build();

    public static  final Uri USER_ACTION_URI=
            Uri.parse("content://" + CONTENT_AUTHORITY + "/" +TABLE_USER_ACTION);
    /* Helpers to retrieve column values */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }
    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt( cursor.getColumnIndex(columnName) );
    }

    public static long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong( cursor.getColumnIndex(columnName) );
    }

}
