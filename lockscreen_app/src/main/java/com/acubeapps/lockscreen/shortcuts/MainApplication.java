package com.acubeapps.lockscreen.shortcuts;

import com.inmobi.oem.moments.matcher.MomentApi;
import com.acubeapps.lockscreen.shortcuts.BuildConfig;
import com.acubeapps.lockscreen.shortcuts.core.NudgeService;
import com.acubeapps.lockscreen.shortcuts.core.ad.AdApi;
import com.acubeapps.lockscreen.shortcuts.video.VideoService;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.webkit.WebView;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * Created by ritwik on 29/05/16.
 */
public class MainApplication extends MultiDexApplication {

    @Inject
    Context context;

    @Inject
    AdApi adApi;

    @Inject
    MomentApi momentApi;

    @Inject
    VideoService videoService;

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
        adApi.initialize();

        startNudgeService();
        momentApi.startMomentService();
        videoService.start();
    }

    private void startNudgeService() {
        Intent intent = new Intent(context, NudgeService.class);
        startService(intent);
    }
}
