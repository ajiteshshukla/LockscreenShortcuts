package com.acubeapps.lockscreen.shortcuts.adstore;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vikram.rathi on 6/27/16.
 */
public class AdStoreContainer {

    @SerializedName("ads")
    private final Map<Long, InternalAd> ads = new HashMap<>();

    public Map<Long, InternalAd> getAds() {
        return ads;
    }

}
