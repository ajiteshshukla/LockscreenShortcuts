package com.acubeapps.lockscreen.shortcuts.core.card;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TAppAd;

import android.os.Parcel;

/**
 * Created by ritwik on 29/05/16.
 */
public class AppAdCard extends AdCard<TAppAd> {

    public AppAdCard(Parcel source) {
        super(source);
    }

    public AppAdCard(TAd tad) {
        super(tad.getApp());
    }

    @Override
    protected TAppAd newAdInstance() {
        return new TAppAd();
    }

    public static final Creator<AppAdCard> CREATOR = new Creator<AppAdCard>() {
        @Override
        public AppAdCard createFromParcel(Parcel in) {
            return new AppAdCard(in);
        }

        @Override
        public AppAdCard[] newArray(int size) {
            return new AppAdCard[size];
        }
    };
}
