package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.inmobi.oem.thrift.ad.model.TBrandAd;

import android.net.Uri;

/**
 * Created by ajitesh.shukla on 6/9/16.
 */
public class BrandAdIcon implements Icon {

    private final TBrandAd brandAd;

    public BrandAdIcon(TBrandAd brandAd) {
        this.brandAd = brandAd;
    }

    @Override
    public Uri getIconUri() {
        return Uri.parse(brandAd.getIconUrl());
    }

    @Override
    public String getTitle() {
        return brandAd.getTitle();
    }

    @Override
    public String getTagline() {
        return brandAd.getHeadline();
    }

    @Override
    public Object getAd() {
        return brandAd;
    }
}
