package com.acubeapps.lockscreen.shortcuts.analytics;

import com.acubeapps.lockscreen.shortcuts.Constants;
import com.acubeapps.lockscreen.shortcuts.utils.ConnectionUtils;

import android.app.Application;
import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

import java.util.Collection;

/**
 * Created by netra.shetty on 6/22/16.
 */
public class MixPanelAnalyticsClient implements AnalyticsClient {

    private final MixpanelAPI mixpanelApi;

    public MixPanelAnalyticsClient(Application application, String projectToken, String userId) {
        this.mixpanelApi = MixpanelAPI.getInstance(application, projectToken);
        this.mixpanelApi.identify(userId);
    }

    public void setUserMeta(String userName, String emailId) {
        JSONObject superProperties = new JSONObject();
        try {
            superProperties.put("userName", userName);
            superProperties.put("emailId", emailId);
        } catch (JSONException exception) {
            Timber.e("Video Event Logging Failed");
        }
        mixpanelApi.registerSuperProperties(superProperties);
    }

    @Override
    public void logVideoEvent(String videoId, long videoDuration, long videoViewDuration,
                              String magazineId, String tileId, long videoLoadTime, Context context) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("magazineId", magazineId);
            eventProperties.put("videoId", videoId);
            eventProperties.put("tileId", tileId);
            eventProperties.put("videoLoadTime", videoLoadTime);
            eventProperties.put("videoDuration", videoDuration);
            eventProperties.put("videoViewDuration", videoViewDuration);
            double percentageDurationWatched = ((double) videoViewDuration * 100) / videoDuration;
            eventProperties.put("videoPercentageViewed", percentageDurationWatched);
            //carrier info
            String networkType = ConnectionUtils.getCarrier(context);
            Timber.d("NetworkType %s", networkType);
            eventProperties.put("networkType", networkType);
        } catch (JSONException exception) {
            Timber.e("Video Event Logging Failed");
        }
        mixpanelApi.track(Constants.VIDEO_EVENT_KEY, eventProperties);
    }

    @Override
    public void logMagazineEvent(String magazineId, long magazineViewDuration, long videosPresentCount,
                                 Collection<String> videosPresent, long videosViewedCount,
                                 Collection<String> videosViewed, long tilesViewedCount,
                                 Collection<String> tileIds, Context context) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("magazineViewDuration", magazineViewDuration);
            eventProperties.put("videosPresentCount", videosPresentCount);
            eventProperties.put("videosPresent", new JSONArray(videosPresent));
            eventProperties.put("videosViewedCount", videosViewedCount);
            eventProperties.put("videosViewed", new JSONArray(videosViewed));
            eventProperties.put("tilesViewedCount", tilesViewedCount);
            eventProperties.put("tileIds", new JSONArray(tileIds));
            eventProperties.put("magazineId", magazineId);
            //carrier info
            String networkType = ConnectionUtils.getCarrier(context);
            Timber.d("NetworkType %s", networkType);
            eventProperties.put("networkType", networkType);
        } catch (JSONException exception) {
            Timber.e("Video Event Logging Failed");
        }
        mixpanelApi.track(Constants.MAGAZINE_EVENT_KEY, eventProperties);
    }

    @Override
    public void flush() {
        mixpanelApi.flush();
    }
}
