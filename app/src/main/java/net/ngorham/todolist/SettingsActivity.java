package net.ngorham.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {
    //SharedPreferences variables
    private SharedPreferences sharedPrefs;
    private boolean switchTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        switchTheme = sharedPrefs.getBoolean("switch_theme", false);
        if(switchTheme){ //Light Theme
            setTheme(R.style.AppTheme);
        } else { //Dark Theme
            setTheme(R.style.DarkTheme);
        }
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }
}
