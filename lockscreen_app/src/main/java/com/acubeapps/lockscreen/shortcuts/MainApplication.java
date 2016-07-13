package com.acubeapps.lockscreen.shortcuts;

import com.acubeapps.lockscreen.shortcuts.core.NudgeService;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.webkit.WebView;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * Created by ajitesh.shukla on 7/12/16.
 */
public class MainApplication extends MultiDexApplication {

    @Inject
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        Injectors.initialize(this);
        Injectors.appComponent().injectMainApplication(this);

        startServices();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Timber.e(ex, "Fatal exception");
            }
        });
    }

    private void startServices() {
        startNudgeService();
    }

    private void startNudgeService() {
        Intent intent = new Intent(context, NudgeService.class);
        startService(intent);
    }
}
