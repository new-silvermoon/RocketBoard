package com.silvermoon.rocketboard.smartfeatures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.silvermoon.rocketboard.data.SmartKeyContract;
import com.silvermoon.smartkeyboard.R;

public class UserActionList extends AppCompatActivity implements
UserActionAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private UserActionAdapter userActionAdapter;
    private Cursor cursor;
    private String queryUri = SmartKeyContract.CONTENT_URI.toString();
    private RecyclerView recyclerView;
    private static final String MODIFY_COMPLETE_BROADCAST ="com.silvermoon.smartkeyboard.INSERTION_COMPLETE_BROADCAST";
    private UpdateBroadcastReceiver updateBroadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_action_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                startActivity(new Intent(view.getContext(),AddUserAction.class));

            }
        });

        updateBroadcastReceiver = new UpdateBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateBroadcastReceiver,new IntentFilter(MODIFY_COMPLETE_BROADCAST));

        getSupportLoaderManager().initLoader(0,null,this);

        userActionAdapter = new UserActionAdapter(cursor);
        recyclerView = (RecyclerView)findViewById(R.id.rvUserActionList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(userActionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = new String[]{SmartKeyContract.TABLE_USER_ACTION};
        int isAssigned = 1;
        return new CursorLoader(this, Uri.parse(queryUri),projection,null,new String[] {String.valueOf(isAssigned)},SmartKeyContract.SORT_ORDER);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
        userActionAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        userActionAdapter.swapCursor(null);

    }

    @Override
    public void OnItemClick(int id, String value) {

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateBroadcastReceiver);
        super.onDestroy();
    }

    public class UpdateBroadcastReceiver extends BroadcastReceiver {
        private String intentAction;
        @Override
        public void onReceive(Context context, Intent intent) {
            intentAction = intent.getAction();
            if(intentAction==MODIFY_COMPLETE_BROADCAST){

                getSupportLoaderManager().restartLoader(0,null,UserActionList.this);

            }
        }
    }
}
