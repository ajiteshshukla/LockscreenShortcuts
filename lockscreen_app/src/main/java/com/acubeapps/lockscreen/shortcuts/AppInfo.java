package com.acubeapps.lockscreen.shortcuts;

import android.content.Intent;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public class AppInfo {
    String packageName;
    Intent launchIntent;

    public AppInfo(String packageName, Intent launchIntent) {
        this.packageName = packageName;
        this.launchIntent = launchIntent;
    }

    public String getPackageName() {
        return packageName;
    }

    public Intent getLaunchIntent() {
        return launchIntent;
    }
}
