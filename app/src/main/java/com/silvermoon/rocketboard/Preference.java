package com.silvermoon.rocketboard;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.silvermoon.smartkeyboard.R;

public class Preference extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        getFragmentManager().beginTransaction().replace(R.id.main, new PreferenceFragment()).commit();
    }
}
