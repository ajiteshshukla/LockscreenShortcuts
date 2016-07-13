package com.acubeapps.lockscreen.shortcuts;

import com.acubeapps.lockscreen.shortcuts.BuildConfig;
import com.inmobi.oem.thrift.ad.model.TAd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by aasha.medhi on 5/29/16.
 */
public final class Constants {
    private Constants() {
    }

    public static final long PICASSO_CACHE_AGE = 60 * 60 * 24 * 7;

    public static final String MAGAZINE_ID = "magazine_id";
    public static final String VIDEO_VIEWED_COUNT = "video_viewed_count";
    public static final String TOTAL_VIDEO_COUNT = "video_total_count";
    public static final String MAGAZINE_VIEWED_COUNT = "magazine_viewed_count";
    public static final int MAX_MAGAZINE_VIEW_COUNT_TO_SHOW_FRESH_ICON_TEXT = 4;
    public static final int MAX_MAGAZINE_VIEW_COUNT_TO_SHOW_STALE_ICON = 8;
    public static final int MAX_VIDEO_VIEW_PERC_TO_SHOW_STALE_ICON = 70;

    public static final String SHARED_PREFS = "lockscreen.prefs";
    public static final String USERNAME = "username";
    public static final String EMAIL_ID = "email.id";
    public static final String NUDGE_ALIGN_LEFT = "nudge.align.left";

}
