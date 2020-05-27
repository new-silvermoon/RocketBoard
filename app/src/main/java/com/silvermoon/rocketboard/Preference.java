package com.silvermoon.rocketboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



public class Preference extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        getFragmentManager().beginTransaction().replace(R.id.main, new PreferenceFragment()).commit();
    }
}
