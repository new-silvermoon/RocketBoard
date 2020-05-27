package com.silvermoon.rocketboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;


import com.silvermoon.rocketboard.smartfeatures.UserActionList;

public class MainActivity extends AppCompatActivity {


    @Override public void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_main_new);


    }


    public void activate(View v){
        this.startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
    }

    public void select(View v){
        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        } else {
            Toast.makeText(this, "Not possible" , Toast.LENGTH_LONG).show();
        }
    }

    public void sett(View v){
        Intent intent = new Intent(this, com.silvermoon.rocketboard.Preference.class);
        startActivity(intent);
    }

    public void support(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://patreon.com/user?u=6697739"));
        startActivity(intent);

    }

}
