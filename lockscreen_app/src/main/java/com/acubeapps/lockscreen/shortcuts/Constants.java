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
    public static final String NUDGE_ALIGN_LEFT = "nudge.align.left";

}
