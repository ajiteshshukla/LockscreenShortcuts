package com.acubeapps.lockscreen.shortcuts.core.card;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TBrandAd;

import android.net.Uri;
import android.os.Parcel;

/**
 * Created by ajitesh.shukla on 6/9/16.
 */
public class VideoBrandAdCard extends AdCard<TBrandAd> {

    public VideoBrandAdCard(Parcel source) {
        super(source);
    }

    public VideoBrandAdCard(TAd tad) {
        super(tad.getBrand());
    }

    @Override
    protected TBrandAd newAdInstance() {
        return new TBrandAd();
    }

    public Uri getVideoUri() {
        TBrandAd brandAd = getAd();
        return Uri.parse(brandAd.getVideoUrl());
    }

    public Uri getIconUri() {
        TBrandAd brandAd = getAd();
        return Uri.parse(brandAd.getIconUrl());
    }

    public static final Creator<VideoBrandAdCard> CREATOR = new Creator<VideoBrandAdCard>() {
        @Override
        public VideoBrandAdCard createFromParcel(Parcel in) {
            return new VideoBrandAdCard(in);
        }

        @Override
        public VideoBrandAdCard[] newArray(int size) {
            return new VideoBrandAdCard[size];
        }
    };
}
