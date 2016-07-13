package com.acubeapps.lockscreen.shortcuts.core.icon;

import com.inmobi.oem.thrift.ad.model.TMovieAd;

import android.net.Uri;

/**
 * Created by ritwik on 29/05/16.
 */
public class MovieAdIcon implements Icon {

    private final TMovieAd movieAd;

    public MovieAdIcon(TMovieAd movieAd) {
        this.movieAd = movieAd;
    }

    @Override
    public Uri getIconUri() {
        if (movieAd.getMerchantInfo() == null) {
            return null;
        }
        if (movieAd.getMerchantInfo().getIcon() == null) {
            return null;
        }
        return Uri.parse(movieAd.getMerchantInfo().getIcon());
    }

    @Override
    public String getTitle() {
        if (movieAd.getMerchantInfo() == null) {
            return null;
        }
        return movieAd.getMerchantInfo().getName();
    }

    @Override
    public String getTagline() {
        if (movieAd.getMerchantInfo() == null) {
            return null;
        }
        return movieAd.getMerchantInfo().getHeadline();
    }

    @Override
    public Object getAd() {
        return movieAd;
    }
}
