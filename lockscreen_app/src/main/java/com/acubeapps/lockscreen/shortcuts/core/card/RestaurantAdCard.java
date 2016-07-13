package com.acubeapps.lockscreen.shortcuts.core.card;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TRestaurantAd;

import android.os.Parcel;

/**
 * Created by ritwik on 29/05/16.
 */
public class RestaurantAdCard extends AdCard<TRestaurantAd> {

    public RestaurantAdCard(Parcel source) {
        super(source);
    }

    public RestaurantAdCard(TAd tad) {
        super(tad.getRestaurant());
    }

    @Override
    protected TRestaurantAd newAdInstance() {
        return new TRestaurantAd();
    }

    public static final Creator<RestaurantAdCard> CREATOR = new Creator<RestaurantAdCard>() {
        @Override
        public RestaurantAdCard createFromParcel(Parcel in) {
            return new RestaurantAdCard(in);
        }

        @Override
        public RestaurantAdCard[] newArray(int size) {
            return new RestaurantAdCard[size];
        }
    };

}
