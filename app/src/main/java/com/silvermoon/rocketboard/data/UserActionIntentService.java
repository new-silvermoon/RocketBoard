package com.silvermoon.rocketboard.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UserActionIntentService extends IntentService {

    private static final String TAG = UserActionIntentService.class.getSimpleName();
    public static final String ACTION_UPDATE = TAG + ".UPDATE";
    public static final String EXTRA_VALUES = TAG + ".ContentValues";
    public static final String EXTRA_ARGS=TAG + ".Args";
    private static final String MODIFY_COMPLETE_BROADCAST ="com.silvermoon.rocketboard.INSERTION_COMPLETE_BROADCAST";



    public UserActionIntentService() {
        super(TAG);
    }




    public static  void updateUserAction(Context context, Uri uri, ContentValues values,String[] selectionArgs){
        Intent intent = new Intent(context,UserActionIntentService.class);
        intent.setAction(ACTION_UPDATE);
        intent.setData(uri);
        intent.putExtra(EXTRA_VALUES,values);
        intent.putExtra(EXTRA_ARGS,selectionArgs);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE.equals(action)) {
                ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
                performUpdate(intent.getData(),values,intent.getStringArrayExtra(EXTRA_ARGS));

            }
        }
    }



    private void performUpdate(Uri uri, ContentValues values, String []selectionArgs) {
        int count = getContentResolver().update(uri, values, null, selectionArgs);
        Intent customBroadcastIntent = new Intent(MODIFY_COMPLETE_BROADCAST);
        LocalBroadcastManager.getInstance(this).sendBroadcast(customBroadcastIntent);
        Log.d(TAG, "Updated " + count + " task items");
    }
}
