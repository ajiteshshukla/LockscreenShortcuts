package com.acubeapps.lockscreen.shortcuts.core.card;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TBrandAd;

import android.net.Uri;
import android.os.Parcel;

/**
 * Created by ajitesh.shukla on 6/9/16.
 */
public class ImageBrandAdCard extends AdCard<TBrandAd> {

    public ImageBrandAdCard(Parcel source) {
        super(source);
    }

    public ImageBrandAdCard(TAd tad) {
        super(tad.getBrand());
    }

    @Override
    protected TBrandAd newAdInstance() {
        return new TBrandAd();
    }

    public Uri getImageUri() {
        TBrandAd brandAd = getAd();
        return Uri.parse(brandAd.getImageUrl());
    }

    public static final Creator<ImageBrandAdCard> CREATOR = new Creator<ImageBrandAdCard>() {
        @Override
        public ImageBrandAdCard createFromParcel(Parcel in) {
            return new ImageBrandAdCard(in);
        }

        @Override
        public ImageBrandAdCard[] newArray(int size) {
            return new ImageBrandAdCard[size];
        }
    };
}
