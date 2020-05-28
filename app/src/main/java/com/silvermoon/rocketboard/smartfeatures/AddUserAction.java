package com.silvermoon.rocketboard.smartfeatures;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.silvermoon.rocketboard.R;
import com.silvermoon.rocketboard.data.SmartKeyContract;
import com.silvermoon.rocketboard.data.UserAction;
import com.silvermoon.rocketboard.data.UserActionIntentService;


import java.util.ArrayList;
import java.util.List;

public class AddUserAction extends AppCompatActivity implements View.OnClickListener {

    private Spinner spKey;
    private ArrayAdapter<String> arrayAdapter;
    private TextView tvAppSelection;
    private Cursor mCursor;
    private List<String> lKeysList;
    private List<Long> lKeysIdList = new ArrayList<Long>();
    private Button btnAdd;
    private String queryURI, selectedAppPkg, selectedAppName;
    private UserAction userAction;
    private static final String TAG = AddUserAction.class.getSimpleName();
    PackageManager packageManager;
    RecyclerView.Adapter rAdapter;
    RecyclerView rvlistOfApps;
    RecyclerView.LayoutManager rLayoutManager;
    List<PackageInfo> packageList;
    List<PackageInfo> packageListNoSysApps = new ArrayList<PackageInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_action);

        setTitle("Add an action");

        spKey = findViewById(R.id.spKey);
        tvAppSelection = findViewById(R.id.tvAppSelect);
        btnAdd = findViewById(R.id.btnAdd);
        tvAppSelection.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        queryURI = SmartKeyContract.CONTENT_URI.toString();
        String [] projection = new String[]{SmartKeyContract.TABLE_USER_ACTION};
        int isAssigned = 0;
        mCursor = getContentResolver().query(Uri.parse(queryURI),projection,null,new String[] {String.valueOf(isAssigned)},SmartKeyContract.SORT_ORDER);
        lKeysList = new ArrayList<String>();

        if(mCursor!=null){
            mCursor.moveToFirst();

            userAction = new UserAction(mCursor);
            lKeysList.add(userAction.keyName);
            lKeysIdList.add(userAction.id);

            while(mCursor.moveToNext()){
                userAction = new UserAction(mCursor);
                lKeysList.add(userAction.keyName);
                lKeysIdList.add(userAction.id);
            }

        }
        else{
            Log.i(TAG, "onCreate: Cursor is null");
        }

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lKeysList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKey.setAdapter(arrayAdapter);




    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.tvAppSelect:

                final Dialog dialog = new Dialog(AddUserAction.this);
                dialog.setContentView(R.layout.activity_app_list);
                dialog.setTitle("Select an app ");


                rvlistOfApps = dialog.findViewById(R.id.rvListOfApps);
                rLayoutManager = new LinearLayoutManager(view.getContext());
                rvlistOfApps.setLayoutManager(rLayoutManager);

                packageManager = getPackageManager();
                packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
                packageListNoSysApps = new ArrayList<PackageInfo>();

                for(PackageInfo pi : packageList) {
                    boolean googleApps = pi.packageName.contains("google");
                    boolean b = isSystemPackage(pi);
                    if(!b || googleApps) {
                        packageListNoSysApps.add(pi);
                    }
                }

                rAdapter = new AppPickerAdapter(packageListNoSysApps,packageManager,new AppPickerAdapter.OnItemClickListener(){
                    @Override
                    public void onItemClick(PackageInfo item) {
                        //Add code here
                        selectedAppPkg = item.packageName;
                        selectedAppName = packageManager.getApplicationLabel(item.applicationInfo).toString();
                        tvAppSelection.setText(selectedAppName);
                        dialog.dismiss();


                    }
                });
                rvlistOfApps.setAdapter(rAdapter);

                dialog.show();

                break;
            case R.id.btnAdd:

                if(tvAppSelection.getText().equals(getResources().getString(R.string.user_action_default_text))){
                    Toast.makeText(this, "Please select an app.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int position = (int) spKey.getSelectedItemId();
                long id  = lKeysIdList.get(position);
                Uri updateURI = ContentUris.withAppendedId(SmartKeyContract.CONTENT_URI,id);

                ContentValues values = new ContentValues();
                values.put(SmartKeyContract.UserActionColumns.isAssigned,"1");
                values.put(SmartKeyContract.UserActionColumns.packageName, selectedAppPkg);
                values.put(SmartKeyContract.UserActionColumns.appName,selectedAppName);
                String [] selectionArgs = new String[]{String.valueOf(id)};
                UserActionIntentService.updateUserAction(this,updateURI,values,selectionArgs);
                finish();
                break;

        }

    }
    private boolean isSystemPackage(PackageInfo pkgInfo) {

        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

}
