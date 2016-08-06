package com.acubeapps.lockscreen.shortcuts.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.WindowManager;

import com.acubeapps.lockscreen.shortcuts.Injectors;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOffEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

/**
 * Created by ajitesh.shukla on 7/23/16.
 */
public class SettingsActivity extends PreferenceActivity {

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        addPreferencesFromResource(R.xml.settings_preferences);

        Injectors.appComponent().injectSettingsActivity(this);
        eventBus.register(this);
    }

    @Subscribe
    public void onScreenOff(ScreenOffEvent event) {
        finish();
    }
}
