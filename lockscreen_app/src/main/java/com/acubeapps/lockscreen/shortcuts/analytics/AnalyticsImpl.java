package com.acubeapps.lockscreen.shortcuts.analytics;

/**
 * Created by netra.shetty on 6/21/16.
 */
import com.acubeapps.lockscreen.shortcuts.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import timber.log.Timber;

import java.util.Collection;
import java.util.List;

/**
 * Created by netra.shetty on 6/21/16.
 */
public class AnalyticsImpl implements Analytics {

    MagazineEvent magazineEvent;
    private List<AnalyticsClient> analyticsClientList;
    private SharedPreferences preferences;

    public AnalyticsImpl(List<AnalyticsClient> analyticsClientList, SharedPreferences preferences) {
        this.analyticsClientList = analyticsClientList;
        this.preferences = preferences;
    }


    private void logVideoEvent(String videoId, String tileId, long videoLoadTime, long videoDuration,
                               long videoViewDuration, String magazineId, Context context) {
        for (AnalyticsClient analyticsClient : analyticsClientList) {
            analyticsClient.logVideoEvent(videoId, videoDuration, videoViewDuration, magazineId,
                    tileId, videoLoadTime, context);
        }
    }


    private void logMagazineEvent(String magazineId, long magazineViewDuration,
                                  long videosPresentCount, Collection<String> videosPresent,
                                  long videosViewedCount, Collection<String> videosViewed,
                                  long tilesViewedCount, Collection<String> tileIds,
                                  Context context) {
        for (AnalyticsClient analyticsClient : analyticsClientList) {
            analyticsClient.logMagazineEvent(magazineId, magazineViewDuration, videosPresentCount,
                    videosPresent, videosViewedCount, videosViewed, tilesViewedCount, tileIds,
                    context);
        }
    }

    @Override
    public void startMagazineSession(String magazineId) {
        Timber.d("Log magazine started: magazineId %s", magazineId);
        this.magazineEvent = new MagazineEvent(magazineId);
        magazineEvent.startMagazineSession();
        long magazineViewedCount = preferences.getLong(Constants.MAGAZINE_VIEWED_COUNT, 0);
        preferences.edit().putLong(Constants.MAGAZINE_VIEWED_COUNT, ++magazineViewedCount).apply();
    }

    @Override
    public void stopMagazineSession(String magazineId, Context context) {
        if (magazineEvent != null) {
            Timber.d("Log magazine stopped: magazineId %s", magazineId);
            magazineEvent.stopMagazineSession();
            logMagazineEvent(magazineId, magazineEvent.getMagazineViewDuration(),
                    magazineEvent.getVideosPresentCount(), magazineEvent.getVideosPresent(),
                    magazineEvent.getVideosViewedCount(), magazineEvent.getVideosViewed(),
                    magazineEvent.getTilesViewedCount(), magazineEvent.getTileIds(), context);
            preferences.edit().putLong(Constants.VIDEO_VIEWED_COUNT,
                    magazineEvent.getVideosViewedCount()).apply();
            preferences.edit().putLong(Constants.TOTAL_VIDEO_COUNT,
                    magazineEvent.getVideosPresentCount()).apply();
            magazineEvent = null;
        } else {
            Timber.e("Magazine Event not found");
        }
    }

    @Override
    public void videoPresent(String videoId) {
        if (magazineEvent != null) {
            Timber.d("Log video present: magazineId %s, videoId %s", magazineEvent.getMagazineId(),
                    videoId);
            magazineEvent.logVideoPresent(videoId);
        } else {
            Timber.e("Magazine Event not found");
        }
    }

    @Override
    public void startVideoSession(String videoId, String tileId, long videoDuration,
                                  long videoLoadTime) {
        if (magazineEvent != null) {
            Timber.d("Log video started: video id %s tile id %s", videoId, tileId);
            VideoEvent videoEvent = new VideoEvent(videoId, tileId, videoDuration, videoLoadTime);
            videoEvent.startVideoSession();
            magazineEvent.addVideoViewedEvent(videoId, videoEvent);
        } else {
            Timber.e("Magazine Event not found");
        }
    }

    @Override
    public void stopVideoSession(String videoId, Context context) {
        Timber.d("Log video stopped: videoId %s", videoId);
        if (magazineEvent != null) {
            VideoEvent videoEvent = magazineEvent.getVideoEvent(videoId);
            if (videoEvent != null) {
                videoEvent.stopVideoSession();
                //Avoid logging duplicate video event
                if (videoEvent.getVideoViewDuration() <= videoEvent.getVideoDuration()) {
                    logVideoEvent(videoId, videoEvent.getTileId(), videoEvent.getVideoLoadTime(),
                            videoEvent.getVideoDuration(), videoEvent.getVideoViewDuration(),
                            magazineEvent.getMagazineId(), context);
                }
            } else {
                Timber.e("Video Event not found");
            }
        } else {
            Timber.e("Magazine Event not found");
        }
    }

    @Override
    public void pauseVideoSession(String videoId) {
        if (magazineEvent != null) {
            Timber.d("Log video paused: videoId %s", videoId);
            VideoEvent videoEvent = magazineEvent.getVideoEvent(videoId);
            if (videoEvent != null) {
                videoEvent.stopVideoSession();
            } else {
                Timber.e("Video Event not found");
            }
        } else {
            Timber.e("Magazine Event not found");
        }
    }

    @Override
    public void resumeVideoSession(String videoId) {
        if (magazineEvent != null) {
            Timber.d("Log video resumed: videoId %s", videoId);
            VideoEvent videoEvent = magazineEvent.getVideoEvent(videoId);
            if (videoEvent != null) {
                videoEvent.startVideoSession();
            } else {
                Timber.e("Video Event not found");
            }
        } else {
            Timber.e("Magazine Event not found");
        }
    }

    @Override
    public void tileViewed(String tileId) {
        if (magazineEvent != null) {
            Timber.d("Log tile viewed: tileId %s", tileId);
            magazineEvent.registerTileView(tileId);
        } else {
            Timber.e("Magazine Event not found");
        }
    }

    @Override
    public void logUserMeta(String userName, String emailId) {
        for (AnalyticsClient analyticsClient : analyticsClientList) {
            analyticsClient.setUserMeta(userName, emailId);
        }
    }

    @Override
    public void flush() {
        for (AnalyticsClient analyticsClient : analyticsClientList) {
            analyticsClient.flush();
        }
    }
}
