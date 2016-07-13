package com.acubeapps.lockscreen.shortcuts.core;

import com.acubeapps.lockscreen.shortcuts.BuildConfig;
import com.acubeapps.lockscreen.shortcuts.Injectors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
        if (BuildConfig.DEBUG) {
            /*
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
            wakeLock.acquire();

            Intent notifIntent = new Intent(this, AppAdActivity.class);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.icon_app_info)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .build();
            startForeground(123, notification);
            */
        }

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
