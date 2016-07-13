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

    public static final String AD_STORE_PREFERENCES = "ad-store";
    public static final String AD_STORE_KEY = "ad.store";
    public static final String AD_STORE_VERSION_KEY = "ad.store.version";
    public static final String AD_STORE_VERSION = BuildConfig.VERSION_NAME;


    public static final String AMPLITUDE_ANALYTICS_PROD_KEY = "24cebf7fc9582b8f65cda06a88b034d6";
    public static final String AMPLITUDE_ANALYTICS_TEST_KEY = "513b41fac14a39b2a6cf5feb146d2bce";

    public static final String MIXPANEL_ANALYTICS_PROD_KEY = "c5d8144015cdb2d6decd430120ae8803";
    public static final String MIXPANEL_ANALYTICS_TEST_KEY = "64de34da2e70e18ddf9813a4ad675586";

    public static final String VIDEO_EVENT_KEY = "video_viewed";
    public static final String MAGAZINE_EVENT_KEY = "magazine_browsed";

    public static final String MAGAZINE_ID = "magazine_id";
    public static final String VIDEO_VIEWED_COUNT = "video_viewed_count";
    public static final String TOTAL_VIDEO_COUNT = "video_total_count";
    public static final String CURRENT_VIDEO_POSITION = "current_video_position";
    public static final String MAGAZINE_VIEWED_COUNT = "magazine_viewed_count";
    public static final int MAX_MAGAZINE_VIEW_COUNT_TO_SHOW_FRESH_ICON_TEXT = 4;
    public static final int MAX_MAGAZINE_VIEW_COUNT_TO_SHOW_STALE_ICON = 8;
    public static final int MAX_VIDEO_VIEW_PERC_TO_SHOW_STALE_ICON = 70;

    public static final String GOOGLE_PLAY_ACCESS = "isGooglePlayAccess";

    public static final String LOCKSCREEN_KV_STORE = "lockscreenkvstore";
    public static final String FIRST_TIME_USE = "firstTime";
    public static final String EXPLORE_TRIGGERED_FROM = "exploreTriggeredFrom";

    public static final String INMOBI_LOCK_SCREEN_NUMBER_OF_TIME_SHOW_ICON = "inmobi.lockscreen.number.showIcon";
    public static final String INMOBI_LOCK_SCREEN_NUMBER_OF_COUNT_PER_CYCLE = "inmobi.lockscreen.number.cycleCound";

    //DEFAULT_CARD_TIMEOUT_SCREEN * 2 seconds
    public static final int DEFAULT_CARD_TIMEOUT_SCREEN = 30;
    public static final String INMOBI_LOCK_SCREEN_INTIAL_X_CORDINATE = "inmobi.lockscreen.initialX";
    public static final String INMOBI_LOCK_SCREEN_INTIAL_Y_CORDINATE = "inmobi.lockscreen.initialY";
    public static final String INMOBI_LOCK_SCREEN_BUTTON_SIZE = "inmobi.lockscreen.buttonSize";

    public static final String Y_POSITION = "y_position";
    public static final String X_POSITION = "x_position";
    public static final String SHOULD_EXPLORE = "lockscreen.should.explore";

    public static final String CLEARSCREEN_INTENT = "com.inmobi.oem.lockscreen.CLEAR_SCREEN";

    public static final int ORIG_Y_LARGE_ICON = 140;
    public static final String FACEBOOK_APP_PKG = "com.facebook.katana";
    public static final String FACEBOOK_LITE_PKG = "com.facebook.lite";

    public static final int ORIG_W = 100;

    public static final int ORIG_Y_SMALL_ICON = 180;

    public static final String LOGO_URI = "inmobi.vendor.logouri";

    public static final String LOGO_RATIO = "inmobi.vendor.logoratio";
    public static final String DRAW_OVER_OTHER_APP_PERMISSION = "android.permission.SYSTEM_ALERT_WINDOW";

    public static final int MIN_ADS_REQUESTED = 1;
    public static final int SIX_HOURS_IN_MILLIS = 6 * 60 * 60 * 1000;

    public static final String FALLBACK_ICON_TITLE = "Top picks for you this week";

    public static final String DEFAULT_FAN_PLACEMENT_ID = "161764364221538_163096124088362";
    public static final Random RAND = new SecureRandom();

    public static final double METERS_IN_ONE_MILE = 1609.34;
    public static final int MAX_RESTAUNRANT_TITLE = 20;

    public static final String SHARED_PREFS = "lockscreen.prefs";
    public static final String USERNAME = "username";
    public static final String EMAIL_ID = "email.id";
    public static final String NUDGE_ALIGN_LEFT = "nudge.align.left";

    public static final HashMap<String, Boolean> WHITELIST_DEVICE_MAP = new HashMap<>();

    static {
        WHITELIST_DEVICE_MAP.put("380727857e9ec2021119d40b37e6ad1f5dd64978", true);
        WHITELIST_DEVICE_MAP.put("b0ca0618c27d668ea89c73a768480b4cea0fe64d", true);
        WHITELIST_DEVICE_MAP.put("95af1991c2bdc6390c95eca61c6ffe060c8d3baa", true);
        WHITELIST_DEVICE_MAP.put("079b54c34c2273aa5a5984f18e1145a26776bf2c", true);
        WHITELIST_DEVICE_MAP.put("98bfea0c811d7c0590692a3018db4f1557c42c6e", true);
        WHITELIST_DEVICE_MAP.put("5edf9076c5bb78d820b4901453807fa70f06bc2c", true);
        WHITELIST_DEVICE_MAP.put("f28331722803daca90e5b1fa50ad806480acd86e", true);
        WHITELIST_DEVICE_MAP.put("928593f5c4b2f649ca2db86b1198e12f1d7e4c8d", true);
        WHITELIST_DEVICE_MAP.put("f05b676825777dd254d1c45628e45a45825d3e4c", true);

    }

    public static final Gson THRIFT_GSON = new GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .registerTypeAdapter(new TypeToken<TAd>() { }.getType(), new ThriftTypeAdapter<TAd>() {
                @Override
                protected TAd newT() {
                    return new TAd();
                }
            })
            .create();
}
