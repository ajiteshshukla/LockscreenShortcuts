package com.acubeapps.lockscreen.shortcuts.onboarding;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.acubeapps.lockscreen.shortcuts.ActivityAppSelect;
import com.acubeapps.lockscreen.shortcuts.Constants;
import com.acubeapps.lockscreen.shortcuts.R;
import com.acubeapps.lockscreen.shortcuts.utils.Device;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

/**
 * Created by ajitesh.shukla on 7/25/16.
 */
public class LockscreenIntro extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title1 = "Welcome!!";
        String description1 = "Your Favorite Apps can now be on Lockscreen";

        String title2 = "Swipe To Expand";
        String description2 = "Swipe this icon on lockscreen to display the selected shortcuts";

        String title3 = "Tap";
        String description3 = "Tap on the icon to launch the app";

        String title4 = "LongPress";
        String description4 = "LongPress the icon to move it across screen";

        String title5 = "All Set";
        String description5 = "Lets get started by choosing some shortcuts";

        addSlide(AppIntro2Fragment.newInstance(title1, description1, R.drawable.screenwelcome, Color.parseColor("#38DCAC")));
        addSlide(AppIntro2Fragment.newInstance(title2, description2, R.drawable.screenswipe, Color.parseColor("#6ECCF2")));
        addSlide(AppIntro2Fragment.newInstance(title3, description3, R.drawable.screentap, Color.parseColor("#ECA0E3")));
        addSlide(AppIntro2Fragment.newInstance(title4, description4, R.drawable.screenlongpress, Color.parseColor("#FB8268")));
        addSlide(AppIntro2Fragment.newInstance(title5, description5, R.drawable.allset, Color.parseColor("#BA75F1")));
        setCustomTransformer(new DepthPageTransformer());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit()
                .putBoolean(Constants.ONBOARDING_EXPLORED, true).apply();
        if (Build.MANUFACTURER.equalsIgnoreCase(Constants.KEY_XIAOMI)) {
            try {
                launchMiuiManager();
                Toast.makeText(this, "Please enable pop up window permission.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Please enable pop up window permission.", Toast.LENGTH_LONG).show();
                launchAppSelectActivity();
            }
        } else {
            launchAppSelectActivity();
        }
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit()
                .putBoolean(Constants.ONBOARDING_EXPLORED, true).apply();
        if (Build.MANUFACTURER.equalsIgnoreCase(Constants.KEY_XIAOMI)) {
            try {
                launchMiuiManager();
                Toast.makeText(this, "Please enable pop up window permission.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                launchAppSelectActivity();
                Toast.makeText(this, "Please enable pop up window permission.", Toast.LENGTH_LONG).show();
            }
        } else {
            launchAppSelectActivity();
        }
        finish();
    }

    private void launchMiuiManager() {
        Intent intent = Device.toPermissionMiUiManager(this, this.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private void launchAppSelectActivity() {
        Intent loginIntent = new Intent(this, ActivityAppSelect.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(loginIntent);
    }

}
