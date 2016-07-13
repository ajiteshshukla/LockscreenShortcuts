package com.acubeapps.lockscreen.shortcuts.core.card;

import com.inmobi.oem.thrift.ad.model.TAd;
import com.inmobi.oem.thrift.ad.model.TMovieAd;

import android.os.Parcel;

/**
 * Created by ritwik on 29/05/16.
 */
public class MovieAdCard extends AdCard<TMovieAd> {


    public MovieAdCard(Parcel source) {
        super(source);
    }

    public MovieAdCard(TAd tad) {
        super(tad.getMovie());
    }

    @Override
    protected TMovieAd newAdInstance() {
        return new TMovieAd();
    }

    public static final Creator<MovieAdCard> CREATOR = new Creator<MovieAdCard>() {
        @Override
        public MovieAdCard createFromParcel(Parcel in) {
            return new MovieAdCard(in);
        }

        @Override
        public MovieAdCard[] newArray(int size) {
            return new MovieAdCard[size];
        }
    };

}
