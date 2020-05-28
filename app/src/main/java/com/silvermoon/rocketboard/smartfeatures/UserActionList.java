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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.silvermoon.rocketboard.data.SmartKeyContract;
import com.silvermoon.rocketboard.R;

public class UserActionList extends AppCompatActivity implements
UserActionAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private UserActionAdapter userActionAdapter;
    private Cursor cursor;
    private String queryUri = SmartKeyContract.CONTENT_URI.toString();
    private RecyclerView recyclerView;
    private static final String MODIFY_COMPLETE_BROADCAST ="com.silvermoon.rocketboard.INSERTION_COMPLETE_BROADCAST";
    private UpdateBroadcastReceiver updateBroadcastReceiver;
    private FloatingActionButton fabMain, fabAction;
    private Boolean isFabExpanded = false;
    private CardView cvAction;
    private TextView tvNoActions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_action_list);

        fabMain = findViewById(R.id.fabMain);
        fabAction = findViewById(R.id.fabAction);
        cvAction = findViewById(R.id.cvAction);
        tvNoActions = findViewById(R.id.tvNoActions);
        setTitle("User Actions list");


        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFabExpanded){
                    fabAction.setVisibility(View.GONE);
                    cvAction.setVisibility(View.GONE);
                    isFabExpanded = false;

                }
                else{
                    fabAction.setVisibility(View.VISIBLE);
                    cvAction.setVisibility(View.VISIBLE);
                    isFabExpanded = true;
                }

            }
        });

        fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFabExpanded = false;
                startActivity(new Intent(view.getContext(),AddUserAction.class));

            }
        });

        updateBroadcastReceiver = new UpdateBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateBroadcastReceiver,new IntentFilter(MODIFY_COMPLETE_BROADCAST));

        getSupportLoaderManager().initLoader(0,null,this);

        userActionAdapter = new UserActionAdapter(cursor,this);
        recyclerView = (RecyclerView)findViewById(R.id.rvUserActionList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(userActionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        fabAction.setVisibility(View.GONE);
        cvAction.setVisibility(View.GONE);
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
        if(data.getCount() < 1){
            tvNoActions.setVisibility(View.VISIBLE);


        }
        else{
            tvNoActions.setVisibility(View.GONE);

        }

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
