package com.acubeapps.lockscreen.shortcuts;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ajitesh.shukla on 7/13/16.
 */
public class LazyAdapter extends BaseAdapter {

    Context context;
    List<AppInfo> appInfoList;
    AppListStore appListStore;

    public LazyAdapter(Context context, List<AppInfo> appInfoList, AppListStore appListStore) {
        this.context = context;
        this.appInfoList = appInfoList;
        this.appListStore = appListStore;
    }

    @Override
    public int getCount() {
        return appInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return appInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final AppInfo appInfo = (AppInfo) getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.app_icon_card, parent, false);
            }

            TextView appName = (TextView) convertView.findViewById(R.id.app_name);
            final ImageView appImage = (ImageView) convertView.findViewById(R.id.app_image);
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.app_select_check);
            if (appListStore.isPackagePresent(appInfo.getPackageName())) {
                Log.d("Ajitesh : ", "Package is present - " + appInfo.getPackageName());
                checkBox.setChecked(true);
            } else {
                Log.d("Ajitesh : ", "Package not present setting false - " + appInfo.getPackageName());
                checkBox.setChecked(false);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton.isChecked()) {
                        Log.d("Ajitesh : ", "applist store package count - " + appListStore.getPackageCount());
                        if (appListStore.getPackageCount() >= 6) {
                            compoundButton.setChecked(false);
                            Toast.makeText(context, "Max 6 Shortcuts Allowed", Toast.LENGTH_SHORT).show();
                        } else {
                            appListStore.addPackage(appInfo);
                        }
                    } else {
                        Log.d("Ajitesh : ", "deleting package as unchecked - " + appInfo.getPackageName());
                        if (appListStore.isPackagePresent(appInfo.getPackageName())) {
                            appListStore.removePackage(appInfo.getPackageName());
                        }
                    }
                }
            });

            PackageManager packageManager = context.getPackageManager();
            String applicationLabel = (String) packageManager.getApplicationLabel(packageManager
                    .getApplicationInfo(appInfo.getPackageName(), PackageManager.GET_META_DATA));
            appName.setText(applicationLabel);
            appImage.setImageDrawable(packageManager.getApplicationIcon(appInfo.getPackageName()));
            // Return the completed view to render on screen
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
