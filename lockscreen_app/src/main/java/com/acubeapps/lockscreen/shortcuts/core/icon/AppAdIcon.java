package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.inmobi.oem.thrift.ad.model.TAppAd;

import android.net.Uri;

/**
 * Created by ritwik on 29/05/16.
 */
public class AppAdIcon implements Icon {

    private final TAppAd ad;

    public AppAdIcon(TAppAd ad) {
        this.ad = ad;
    }

    @Override
    public Uri getIconUri() {
        return Uri.parse(ad.getIconUrl());
    }

    @Override
    public String getTitle() {
        return ad.getTitle();
    }

    @Override
    public String getTagline() {
        return ad.getSubtitle();
    }

    @Override
    public Object getAd() {
        return ad;
    }
}
