package com.acubeapps.lockscreen.shortcuts.adstore;

import java.util.List;

/**
 * Created by anshul.srivastava on 08/06/16.
 */
public interface AdStore {

    void addAd(InternalAd ad);

    InternalAd getAdById(long id);

    void clearAds(List<Long> ads);

    List<InternalAd> getNudgedAds(String[] nudgeIds);

    List<InternalAd> getAllNudgedAds();

    void clearAllNudgedAds();

    void clearNudgeById(String nudgeId);

}
