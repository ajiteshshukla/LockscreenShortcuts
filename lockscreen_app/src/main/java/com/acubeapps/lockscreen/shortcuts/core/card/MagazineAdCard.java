package com.acubeapps.lockscreen.shortcuts.core.card;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TMagazine;

import android.os.Parcel;

/**
 * Created by ritwik on 29/05/16.
 */
public class MagazineAdCard extends AdCard<TMagazine> {


    public MagazineAdCard(Parcel source) {
        super(source);
    }

    public MagazineAdCard(TAd tad) {
        super(tad.getMagazine());
    }

    @Override
    protected TMagazine newAdInstance() {
        return new TMagazine();
    }

    public static final Creator<MagazineAdCard> CREATOR = new Creator<MagazineAdCard>() {
        @Override
        public MagazineAdCard createFromParcel(Parcel in) {
            return new MagazineAdCard(in);
        }

        @Override
        public MagazineAdCard[] newArray(int size) {
            return new MagazineAdCard[size];
        }
    };

}
