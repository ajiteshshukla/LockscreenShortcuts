package com.acubeapps.lockscreen.shortcuts.analytics;

import android.content.Context;

import timber.log.Timber;

/**
 * Created by netra.shetty on 6/21/16.
 */
public class NopAnalytics implements Analytics {

    public NopAnalytics() {
        Timber.i("Instance created");
    }

    @Override
    public void startMagazineSession(String magazineId) {
       Timber.i("Magazine Session started for magazine id %s", magazineId);
    }

    @Override
    public void stopMagazineSession(String magazineId, Context context) {
        Timber.i("Magazine Session stopped for magazine id %s", magazineId);
    }

    @Override
    public void tileViewed(String tileId) {
        Timber.i("Viewed tile Id %s", tileId);
    }

    @Override
    public void startVideoSession(String videoId, String tileId, long videoDuration,
                                  long videoLoadTime) {
        Timber.i("Video Session started for video id %s, tile id %s, video duration %s, "
                + "video load time %s", videoId, tileId, videoDuration, videoLoadTime);
    }

    @Override
    public void stopVideoSession(String videoId, Context context) {
        Timber.i("Video Session stopped for video id %s", videoId);
    }

    @Override
    public void pauseVideoSession(String videoId) {
        Timber.i("Video Session paused for video id %s", videoId);
    }

    @Override
    public void resumeVideoSession(String videoId) {
        Timber.i("Video Session resumed for video id %s", videoId);
    }

    @Override
    public void videoPresent(String videoId) {
        Timber.i("Video id %s present in magazine", videoId);
    }

    @Override
    public void logUserMeta(String userName, String emailId) {
        Timber.i("Logging user meta username %s emailId %s", userName, emailId);
    }

    public void flush() {
        Timber.i("Flushing analytics");
    }
}
