package com.acubeapps.lockscreen.shortcuts;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajitesh.shukla on 7/13/16.
 */
public class ActivityAppSelect extends Activity {

    private ListView listView;

    private LazyAdapter lazyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);
        listView = (ListView) findViewById(R.id.applist);
        AppListStore appListStore = new AppListStore(this);
        lazyAdapter = new LazyAdapter(this, getAppInfoList(), appListStore);
        listView.setAdapter(lazyAdapter);
    }

    private AppInfo getAppInfo(ResolveInfo resolveInfo) {
        ActivityInfo activity = resolveInfo.activityInfo;
        ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
        final Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        launchIntent.setComponent(name);
        return new AppInfo(resolveInfo.activityInfo.packageName, launchIntent);
    }

    private List<AppInfo> getAppInfoList() {
        List<AppInfo> appInfoList = new ArrayList<>();
        PackageManager packageManager = this.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //read package names from store
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo resolveInfo : apps) {
            try {
                AppInfo appInfo = getAppInfo(resolveInfo);
                if (appInfo.getLaunchIntent() != null) {
                    appInfoList.add(appInfo);
                } else {
                    Log.d("Ajitesh : ", "launch intent null for " + resolveInfo.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return appInfoList;
    }
}
