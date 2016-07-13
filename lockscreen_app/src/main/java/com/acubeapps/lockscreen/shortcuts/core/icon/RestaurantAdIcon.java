package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.inmobi.oem.thrift.ad.model.TRestaurantAd;

import android.net.Uri;

/**
 * Created by ritwik on 29/05/16.
 */
public class RestaurantAdIcon implements Icon {

    private final TRestaurantAd restaurantAd;

    public RestaurantAdIcon(TRestaurantAd restaurantAd) {
        this.restaurantAd = restaurantAd;
    }

    @Override
    public Uri getIconUri() {
        if (restaurantAd.getMerchantInfo() == null) {
            return null;
        }
        if (restaurantAd.getMerchantInfo().getIcon() == null) {
            return null;
        }
        return Uri.parse(restaurantAd.getMerchantInfo().getIcon());
    }

    @Override
    public String getTitle() {
        if (restaurantAd.getMerchantInfo() == null) {
            return null;
        }
        return restaurantAd.getMerchantInfo().getName();
    }

    @Override
    public String getTagline() {
        if (restaurantAd.getMerchantInfo() == null) {
            return null;
        }
        return restaurantAd.getMerchantInfo().getHeadline();
    }

    @Override
    public Object getAd() {
        return restaurantAd;
    }
}
