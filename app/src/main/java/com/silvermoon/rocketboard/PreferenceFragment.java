package com.silvermoon.rocketboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.silvermoon.rocketboard.smartfeatures.UserActionList;


/**
 * Created by faith on 9/28/2017.
 */

public class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    ListPreference listTheme;
    ListPreference listStart;
    android.preference.Preference prefSmartKey;
    @Override
    public void onCreate(Bundle s){
        super.onCreate(s);
        addPreferencesFromResource(R.xml.ime_preferences);
        listTheme = (ListPreference) findPreference("theme");
        listStart = (ListPreference) findPreference("start");
        prefSmartKey = findPreference("prefSmartKey") ;

        prefSmartKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), UserActionList.class));
                return true;
            }
        });


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
