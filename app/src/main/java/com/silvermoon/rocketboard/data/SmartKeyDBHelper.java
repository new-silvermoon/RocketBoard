package com.silvermoon.rocketboard.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by faith on 10/1/2017.
 */

public class SmartKeyDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "silvermoon.db";
    private static final int DATABASE_VERSION=1;
    private static final String TAG = SmartKeyDBHelper.class.getSimpleName();


    public SmartKeyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_TABLE_USER_ACTION = String.format("create table %s"
    + " (%s integer primary key autoincrement, %s text not null, %s integer not null, %s text, %s integer not null)",
            SmartKeyContract.TABLE_USER_ACTION,
            SmartKeyContract.UserActionColumns._ID,
            SmartKeyContract.UserActionColumns.keyName,
            SmartKeyContract.UserActionColumns.keyId,
            SmartKeyContract.UserActionColumns.packageName,
            SmartKeyContract.UserActionColumns.isAssigned);

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USER_ACTION);
        fillDatabaseWithDefaultData(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + SmartKeyContract.TABLE_USER_ACTION);
        onCreate(sqLiteDatabase);
    }

    public void fillDatabaseWithDefaultData(SQLiteDatabase sqLiteDatabase){

        String[] keys = { "y","u","i","o","p","a","s","d","f",
                          "g","h","j","k","l","z","x","c","v",
                          "b","n","m"};
        int[] isAssigned = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        int[] keyIds = {121,117,105,111,112,97,115,100,102,103,104,106,107,108,122,120,99,118,98,110,109};

        //Container for holding the data before inserting to database
        ContentValues values = new ContentValues();
        for(int i=0;i<keys.length;i++){
            values.put(SmartKeyContract.UserActionColumns.keyName,keys[i]);
            values.put(SmartKeyContract.UserActionColumns.isAssigned,isAssigned[i]);
            values.put(SmartKeyContract.UserActionColumns.keyId,keyIds[i]);
            sqLiteDatabase.insert(SmartKeyContract.TABLE_USER_ACTION,null,values);
        }


    }
}
