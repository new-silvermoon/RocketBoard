package com.silvermoon.rocketboard.smartfeatures;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.silvermoon.rocketboard.R;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {
    PackageManager packageManager;
    RecyclerView.Adapter rAdapter;
    RecyclerView rvlistOfApps;
    RecyclerView.LayoutManager rLayoutManager;
    List<PackageInfo> packageList;
    List<PackageInfo> packageListNoSysApps = new ArrayList<PackageInfo>();
    private static final String TAG = AppListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        rvlistOfApps = (RecyclerView)findViewById(R.id.rvListOfApps);
        rLayoutManager = new LinearLayoutManager(this);
        rvlistOfApps.setLayoutManager(rLayoutManager);



    /*    final PackageManager pm = getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }*/



        packageManager = getPackageManager();
        packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        //To filter out system apps
        for(PackageInfo pi : packageList) {
            boolean b = isSystemPackage(pi);
            if(!b) {
                packageListNoSysApps.add(pi);
            }
        }

        rAdapter = new AppPickerAdapter(packageList,packageManager,new AppPickerAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(PackageInfo item) {
                //Add code here
                Log.d(TAG, "Launch Activity :" + packageManager.getLaunchIntentForPackage(item.packageName).toString());
                Intent launchintent = packageManager.getLaunchIntentForPackage(item.packageName);
                startActivity(launchintent);


            }
        });
        rvlistOfApps.setAdapter(rAdapter);




    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {

        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }



    public void toggleWifi(){

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }


}

