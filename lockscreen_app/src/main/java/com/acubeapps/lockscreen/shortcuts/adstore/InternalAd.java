package com.acubeapps.lockscreen.shortcuts.adstore;

import com.inmobi.oem.thrift.ad.model.TAd;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul.srivastava on 08/06/16.
 */
public class InternalAd {

    @SerializedName("adId")
    private final long adId;
    @SerializedName("momentId")
    private String momentId;
    @SerializedName("ad")
    private TAd tad;

    public InternalAd(String momentId, TAd tad) {
        this.adId = IdGenerator.nextId();
        this.momentId = momentId;
        this.tad = tad;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public long getAdId() {
        return adId;
    }

    public String getMomentId() {
        return momentId;
    }

    public TAd getAd() {
        return tad;
    }
}
