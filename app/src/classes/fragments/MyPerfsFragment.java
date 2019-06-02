package com.example.galgoren.finalproject.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.galgoren.finalproject.R;

/**
 * Created by galgoren on 9/26/17.
 */

public class MyPerfsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


    private Preference searchUnitesKey;
    private SharedPreferences sharedPreferences;


    // onCreate
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // find and connect Search Unites preference
        searchUnitesKey = findPreference("search_unites_key");
        searchUnitesKey.setOnPreferenceChangeListener(this);
        searchUnitesKey.setSummary(sharedPreferences.getString("search_unites_key", "Km"));

    }


    //----------------------------------------------------------------------------------------------


    // onPreferenceChanged (using switch for future use)
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        switch (preference.getKey()) {

            case "search_unites_key":

                preference.setSummary(newValue + "");

                break;

        }

        return true;
    }

    //----------------------------------------------------------------------------------------------

}
