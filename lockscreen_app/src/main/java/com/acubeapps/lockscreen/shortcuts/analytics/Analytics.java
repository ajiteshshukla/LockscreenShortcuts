package com.acubeapps.lockscreen.shortcuts.analytics;

import android.content.Context;

/**
 * Created by netra.shetty on 6/21/16.
 */

public interface Analytics {

    void startVideoSession(String videoId, String tileId, long videoDuration, long videoLoadTime);

    void stopVideoSession(String videoId, Context context);

    void pauseVideoSession(String videoId);

    void resumeVideoSession(String videoId);

    void startMagazineSession(String magazineId);

    void stopMagazineSession(String magazineId, Context context);

    void videoPresent(String videoId);

    void tileViewed(String tileId);

    void flush();

    void logUserMeta(String userName, String emailId);
}
