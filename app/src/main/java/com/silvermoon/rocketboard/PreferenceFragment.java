package com.silvermoon.rocketboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import com.silvermoon.smartkeyboard.R;

/**
 * Created by faith on 9/28/2017.
 */

public class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    ListPreference listTheme;
    ListPreference listStart;
    @Override
    public void onCreate(Bundle s){
        super.onCreate(s);
        addPreferencesFromResource(R.xml.ime_preferences);
        listTheme = (ListPreference) findPreference("theme");
        listStart = (ListPreference) findPreference("start");
        listTheme.setSummary(listTheme.getEntry());
        listStart.setSummary(listStart.getEntry());
        PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        listTheme.setSummary(listTheme.getEntry());
        listStart.setSummary(listStart.getEntry());
    }
}
