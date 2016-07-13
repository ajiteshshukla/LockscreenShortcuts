package com.acubeapps.lockscreen.shortcuts.core.card;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TTaxiAd;

import android.os.Parcel;

/**
 * Created by ritwik on 29/05/16.
 */
public class TaxiAdCard extends AdCard<TTaxiAd> {

    public TaxiAdCard(Parcel source) {
        super(source);
    }

    public TaxiAdCard(TAd tad) {
        super(tad.getTaxi());
    }

    @Override
    protected TTaxiAd newAdInstance() {
        return new TTaxiAd();
    }

    public static final Creator<TaxiAdCard> CREATOR = new Creator<TaxiAdCard>() {
        @Override
        public TaxiAdCard createFromParcel(Parcel in) {
            return new TaxiAdCard(in);
        }

        @Override
        public TaxiAdCard[] newArray(int size) {
            return new TaxiAdCard[size];
        }
    };

}
