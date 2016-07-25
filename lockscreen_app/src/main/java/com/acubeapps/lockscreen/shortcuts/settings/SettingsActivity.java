package com.acubeapps.lockscreen.shortcuts.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.WindowManager;
import com.acubeapps.lockscreen.shortcuts.R;

/**
 * Created by ajitesh.shukla on 7/23/16.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        addPreferencesFromResource(R.xml.settings_preferences);
    }
}
