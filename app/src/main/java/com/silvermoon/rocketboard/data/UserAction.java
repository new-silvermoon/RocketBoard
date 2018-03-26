package com.silvermoon.rocketboard.data;

/**
 * Created by faith on 10/6/2017.
 */

import android.database.Cursor;
import com.silvermoon.rocketboard.data.SmartKeyContract.*;

public class UserAction {
    public static final long NO_ID = -1;

    public long id;
    public String keyName;
    public int keyId;
    public String packageName;
    public int isAssigned;

    public UserAction(Cursor cursor){
        this.id = SmartKeyContract.getColumnLong(cursor, UserActionColumns._ID);
        this.keyName = SmartKeyContract.getColumnString(cursor,UserActionColumns.keyName);
        this.keyId = SmartKeyContract.getColumnInt(cursor,UserActionColumns.keyId);
        this.packageName = SmartKeyContract.getColumnString(cursor,UserActionColumns.packageName);
        this.isAssigned = SmartKeyContract.getColumnInt(cursor,UserActionColumns.isAssigned);
    }
}
