package com.acubeapps.lockscreen.shortcuts.adstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static com.acubeapps.lockscreen.shortcuts.Constants.AD_STORE_KEY;
import static com.acubeapps.lockscreen.shortcuts.Constants.AD_STORE_PREFERENCES;
import static com.acubeapps.lockscreen.shortcuts.Constants.AD_STORE_VERSION;
import static com.acubeapps.lockscreen.shortcuts.Constants.AD_STORE_VERSION_KEY;
import static com.acubeapps.lockscreen.shortcuts.Constants.THRIFT_GSON;
/**
 * Created by anshul.srivastava on 08/06/16.
 */
public class MemAdStore implements AdStore {

    Map<Long, InternalAd> ads = new ConcurrentHashMap<>();
    SharedPreferences preferences;

    public MemAdStore(Context context) {
        preferences = context.getSharedPreferences(AD_STORE_PREFERENCES, Context.MODE_PRIVATE);
        ads = getFromPreferences();
    }

    @Override
    public void addAd(InternalAd ad) {
        ads.put(ad.getAdId(), ad);
        saveToPreferences();
    }

    @Override
    public InternalAd getAdById(long id) {
        return ads.get(id);
    }

    @Override
    public void clearAds(List<Long> adIds) {
        for (Long adId : adIds) {
            ads.remove(adId);
        }
        saveToPreferences();
    }

    @Override
    public List<InternalAd> getNudgedAds(String[] nudgeIds) {
        List<InternalAd> resultAds = new ArrayList<>();
        List<String> nudgeIdsList = Arrays.asList(nudgeIds);
        for (InternalAd ad : ads.values()) {
            if (nudgeIdsList.contains(ad.getMomentId())) {
                resultAds.add(ad);
            }
        }
        return resultAds;
    }

    @Override
    public List<InternalAd> getAllNudgedAds() {
        List<InternalAd> resultAds = new ArrayList<>();
        for (InternalAd ad : ads.values()) {
            if (!TextUtils.isEmpty(ad.getMomentId())) {
                resultAds.add(ad);
            }
        }
        return resultAds;
    }

    @Override
    public void clearAllNudgedAds() {
        List<Long> clearAdIds = new ArrayList<>();

        for (InternalAd ad : ads.values()) {
            if (!TextUtils.isEmpty(ad.getMomentId())) {
                clearAdIds.add(ad.getAdId());
            }
        }

        for (Long adId : clearAdIds) {
            ads.remove(adId);
        }

        saveToPreferences();
    }

    @Override
    public void clearNudgeById(String nudgeId) {
        List<Long> clearAdIds = new ArrayList<>();

        for (InternalAd ad : ads.values()) {
            if (nudgeId.equals(ad.getMomentId())) {
                clearAdIds.add(ad.getAdId());
            }
        }

        for (Long adId : clearAdIds) {
            ads.remove(adId);
        }

        saveToPreferences();
    }

    private Map<Long, InternalAd> getFromPreferences() {
        try {
            String adStoreVersion = preferences.getString(AD_STORE_VERSION_KEY, AD_STORE_VERSION);
            Timber.i("adStoreVersion: %s", adStoreVersion);

            if (adStoreVersion.equalsIgnoreCase(AD_STORE_VERSION)) {
                Timber.i("getting ads from preferences: " + ads);
                String adStoreString = preferences.getString(AD_STORE_KEY, "");
                Timber.i("Got adStoreContainer value: " + adStoreString);

                AdStoreContainer container = THRIFT_GSON.fromJson(adStoreString, AdStoreContainer.class);

                Timber.i("container.getAds: " + container.getAds());
                return new ConcurrentHashMap<>(container.getAds());
            } else {
                Timber.i("App version updated. Removing stored ads. %s -> %s", adStoreVersion, AD_STORE_VERSION);
                preferences.edit().remove(AD_STORE_KEY).apply();
                return new ConcurrentHashMap<>();
            }
        } catch (Exception e) {
            Timber.e(e, "Error getting ads from preferences");
        }
        return new ConcurrentHashMap<>();
    }

    private void saveToPreferences() {
        try {
            Timber.i("Saving ads to preferences: " + ads);
            AdStoreContainer container = new AdStoreContainer();

            for (Map.Entry entry : ads.entrySet()) {
                container.getAds().put((Long) entry.getKey(), (InternalAd) entry.getValue());
            }

            final String containerString = THRIFT_GSON.toJson(container);
            //Timber.i("Serialized ads container: " + containerString);

            preferences.edit()
                    .putString(AD_STORE_KEY, containerString)
                    .putString(AD_STORE_VERSION_KEY, AD_STORE_VERSION)
                    .apply();
        } catch (Exception e) {
            Timber.e(e, "Error saving ads to preferences");
        }
    }
}
