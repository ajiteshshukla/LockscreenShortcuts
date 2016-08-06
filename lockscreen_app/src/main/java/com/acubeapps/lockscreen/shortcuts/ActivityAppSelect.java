package com.acubeapps.lockscreen.shortcuts;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.acubeapps.lockscreen.shortcuts.core.events.ScreenOffEvent;
import com.acubeapps.lockscreen.shortcuts.onboarding.LockscreenIntro;
import com.acubeapps.lockscreen.shortcuts.settings.SettingsActivity;
import com.acubeapps.lockscreen.shortcuts.typeface.WaltToGraph;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by ajitesh.shukla on 7/13/16.
 */
public class ActivityAppSelect extends AppCompatActivity {

    @Inject
    EventBus eventBus;

    @BindView(R.id.applist)
    ListView listView;

    private LazyAdapter lazyAdapter;

    @BindView(R.id.header_text)
    TextView textView;

    @BindView(R.id.inputSearch)
    EditText inputSearch;

    @BindView(R.id.settings)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.ONBOARDING_EXPLORED, false)) {
            Intent onBoardingIntent = new Intent(this, LockscreenIntro.class);
            onBoardingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(onBoardingIntent);
            finish();
        } else {
            setContentView(R.layout.activity_app_select);
            Injectors.appComponent().injectAppSelectActivity(this);
            eventBus.register(this);

            ButterKnife.bind(this);
            WaltToGraph.applyFont(this, textView);

            AppListStore appListStore = new AppListStore(this);
            lazyAdapter = new LazyAdapter(this, getAppInfoList(), appListStore);
            listView.setAdapter(lazyAdapter);

            inputSearch.addTextChangedListener(getTextWatcher());

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityAppSelect.this, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ActivityAppSelect.this.startActivity(intent);
                }
            });
        }
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return appInfoList;
    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                ActivityAppSelect.this.lazyAdapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }
        };
    }

    @Subscribe
    public void onScreenOff(ScreenOffEvent event) {
        finish();
    }
}
