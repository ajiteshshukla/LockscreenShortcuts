package com.acubeapps.lockscreen.shortcuts.analytics;

import android.content.Context;

import java.util.Collection;

/**
 * Created by netra.shetty on 6/22/16.
 */
public interface AnalyticsClient {

    void logVideoEvent(String videoId, long videoDuration, long videoViewDuration,
                       String magazineId, String tileId, long videoLoadTime, Context context);

    void logMagazineEvent(String magazineId, long magazineViewDuration, long videosPresentCount,
                          Collection<String> videosPresent, long videosViewedCount, Collection<String> videoIds,
                          long tilesViewedCount, Collection<String> tileIds, Context context);

    void flush();

    void setUserMeta(String userName, String emailId);

}

