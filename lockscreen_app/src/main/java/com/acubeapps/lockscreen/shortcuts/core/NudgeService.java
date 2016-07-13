package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.BuildConfig;
import com.acubeapps.lockscreen.shortcuts.Injectors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by indersingh on 5/29/16.
 */
public class NudgeService extends Service {

    @Inject
    AppController appController;

    @Inject
    ScreenEventsDetector screenEventsDetector;

    @Override
    public void onCreate() {
        super.onCreate();

        Injectors.appComponent().injectNudgeService(this);
        appController.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appController.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
