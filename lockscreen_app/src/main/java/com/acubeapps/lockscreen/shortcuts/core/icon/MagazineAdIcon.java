package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.inmobi.oem.thrift.ad.model.TMagazine;

import android.net.Uri;

/**
 * Created by ritwik on 29/05/16.
 */
public class MagazineAdIcon implements Icon {

    private final TMagazine magazineAd;

    public MagazineAdIcon(TMagazine tmagazineAd) {
        this.magazineAd = tmagazineAd;
    }

    @Override
    public Uri getIconUri() {
        return Uri.parse(magazineAd.getNudgeIcon());
    }

    @Override
    public String getTitle() {
        return magazineAd.getNudgeTitle();
    }

    @Override
    public String getTagline() {
        return magazineAd.getNudgeSubtitle();
    }

    @Override
    public Object getAd() {
        return magazineAd;
    }
}
