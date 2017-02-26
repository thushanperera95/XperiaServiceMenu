package com.kaozgamer.easyaboutandfeedback;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Fragment that contains the about preferences screen.
 *
 * @author Thushan Perera
 * @version 4
 */
public class AboutFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_help);
    }
}
