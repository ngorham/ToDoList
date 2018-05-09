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
 * @version 1.1 05/08/2018
 *
 * 1.1: Commented out shared preferences, removed DarkTheme
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //boolean switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        setTheme(R.style.LightTheme);
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ThemePreferenceFragment())
                .commit();
    }
}
