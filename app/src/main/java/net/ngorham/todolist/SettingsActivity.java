package net.ngorham.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * To Do List
 * SettingsActivity.java
 * Detail/Edit
 * Purpose: Provides access to SharedPreferences
 *
 * @author Neil Gorham
 * @version 1.0 03/29/2018
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        if(switchTheme){ setTheme(R.style.LightTheme); }
        else { setTheme(R.style.DarkTheme); }
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ThemePreferenceFragment())
                .commit();
    }
}
