package net.ngorham.todolist;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * To Do List
 * ThemePreferenceFragment.java
 * A simple {@link PreferenceFragment} subclass
 * Purpose: Displays preferences as defined in preferences.xml
 *
 * @author Neil Gorham
 * @version 1.0 03/29/2018
 */

public class ThemePreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
