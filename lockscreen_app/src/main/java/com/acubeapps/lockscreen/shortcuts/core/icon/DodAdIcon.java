package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.inmobi.oem.thrift.ad.model.TCommerceAd;

import android.net.Uri;

/**
 * Created by ritwik on 29/05/16.
 */
public class DodAdIcon implements Icon {

    private final TCommerceAd dodAd;

    public DodAdIcon(TCommerceAd tcommercead) {
        this.dodAd = tcommercead;
    }

    @Override
    public Uri getIconUri() {
        return Uri.parse(dodAd.getIcon());
    }

    @Override
    public String getTitle() {
        return dodAd.getTitle();
    }

    @Override
    public String getTagline() {
        String tagline = dodAd.getHeadline();
        if (tagline == null) {
            tagline = dodAd.getMerchantName();
        }
        return tagline;
    }

    @Override
    public Object getAd() {
        return dodAd;
    }
}
