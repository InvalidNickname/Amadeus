package com.example.yink.amadeus;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.RequiresApi;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
