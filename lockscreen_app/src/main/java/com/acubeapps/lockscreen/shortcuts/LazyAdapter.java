package com.acubeapps.lockscreen.shortcuts;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajitesh.shukla on 7/13/16.
 */
public class LazyAdapter extends BaseAdapter implements Filterable {

    Context context;
    List<AppInfo> appInfoList;
    List<AppInfo> appInfoListFiltered;
    AppListStore appListStore;

    public LazyAdapter(Context context, List<AppInfo> appInfoList, AppListStore appListStore) {
        this.context = context;
        this.appListStore = appListStore;
        this.appInfoList = sortAppInfoList(appInfoList);
        this.appInfoListFiltered = sortAppInfoList(appInfoList);
    }

    public List<AppInfo> sortAppInfoList(List<AppInfo> appInfoList) {
        List<AppInfo> unSelectedAppInfoList = new ArrayList<>();
        List<AppInfo> selectedAppInfoList = new ArrayList<>();
        List<AppInfo> finalAppInfoList = new ArrayList<>();
        for (AppInfo appInfo : appInfoList) {
            if (appListStore.isPackagePresent(appInfo.getPackageName())) {
                selectedAppInfoList.add(appInfo);
            } else {
                unSelectedAppInfoList.add(appInfo);
            }
        }
        finalAppInfoList.addAll(selectedAppInfoList);
        finalAppInfoList.addAll(unSelectedAppInfoList);
        return finalAppInfoList;
    }

    @Override
    public int getCount() {
        return appInfoListFiltered.size();
    }

    @Override
    public Object getItem(int i) {
        return appInfoListFiltered.get(i);
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

            final TextView appName = (TextView) convertView.findViewById(R.id.app_name);
            final ImageView appImage = (ImageView) convertView.findViewById(R.id.app_image);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.app_select_check);
            checkBox.setOnCheckedChangeListener(null);
            if (appListStore.isPackagePresent(appInfo.getPackageName())) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton.isChecked()) {
                        if (appListStore.getPackageCount() >= 6) {
                            compoundButton.setChecked(false);
                            Toast.makeText(context, "Max 6 Shortcuts Allowed", Toast.LENGTH_SHORT).show();
                        } else {
                            appListStore.addPackage(appInfo);
                        }
                    } else {
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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.count = appInfoList.size();
                    results.values = appInfoList;
                } else {
                    List<AppInfo> resultsData = new ArrayList<>();
                    String searchStr = constraint.toString().toUpperCase();
                    for (AppInfo appInfo : appInfoList) {
                        if (appInfo.getPackageName().toUpperCase().contains(searchStr)) {
                            resultsData.add(appInfo);
                        }
                    }
                    results.count = resultsData.size();
                    results.values = resultsData;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                appInfoListFiltered = (ArrayList<AppInfo>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
