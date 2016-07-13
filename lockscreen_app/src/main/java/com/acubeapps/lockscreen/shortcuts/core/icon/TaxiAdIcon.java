package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.inmobi.oem.thrift.ad.model.TTaxiAd;

import android.net.Uri;

/**
 * Created by ritwik on 29/05/16.
 */
public class TaxiAdIcon implements Icon {

    private final TTaxiAd taxiAd;

    public TaxiAdIcon(TTaxiAd taxiAd) {
        this.taxiAd = taxiAd;
    }

    @Override
    public Uri getIconUri() {
        if (taxiAd.getMerchantInfo() == null) {
            return null;
        }
        if (taxiAd.getMerchantInfo().getIcon() == null) {
            return null;
        }
        return Uri.parse(taxiAd.getMerchantInfo().getIcon());
    }

    @Override
    public String getTitle() {
        if (taxiAd.getMerchantInfo() == null) {
            return null;
        }
        return taxiAd.getMerchantInfo().getName();
    }

    @Override
    public String getTagline() {
        if (taxiAd.getMerchantInfo() == null) {
            return null;
        }
        return taxiAd.getMerchantInfo().getHeadline();
    }

    @Override
    public Object getAd() {
        return taxiAd;
    }
}
