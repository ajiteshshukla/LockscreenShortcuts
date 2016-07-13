package com.acubeapps.lockscreen.shortcuts.core.card;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TCommerceAd;

import android.os.Parcel;

/**
 * Created by ritwik on 29/05/16.
 */
public class DodAdCard extends AdCard<TCommerceAd> {

    public DodAdCard(Parcel source) {
        super(source);
    }

    public DodAdCard(TAd tad) {
        super(tad.getCommerce());
    }

    @Override
    protected TCommerceAd newAdInstance() {
        return new TCommerceAd();
    }

    public static final Creator<DodAdCard> CREATOR = new Creator<DodAdCard>() {
        @Override
        public DodAdCard createFromParcel(Parcel in) {
            return new DodAdCard(in);
        }

        @Override
        public DodAdCard[] newArray(int size) {
            return new DodAdCard[size];
        }
    };
}
