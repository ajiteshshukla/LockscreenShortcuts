package com.acubeapps.lockscreen.shortcuts.core.card;

import android.os.Parcel;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

/**
 * Created by anshul.srivastava on 16/06/16.
 */
public abstract class AdCard<T extends TBase> implements Card {

    private final byte[] serializedAd;
    private T tAd;

    public AdCard(Parcel source) {
        serializedAd = source.createByteArray();
    }

    public AdCard(T tbase) {
        byte[] serializedAd = null;
        try {
            serializedAd = new TSerializer().serialize(tbase);
        } catch (TException e) {
            e.printStackTrace();
        }
        this.serializedAd = serializedAd;
    }

    public T getAd() {
        if (this.tAd != null) {
            return tAd;
        }
        this.tAd = newAdInstance();
        try {
            new TDeserializer().deserialize(tAd, serializedAd);
        } catch (TException e) {
            e.printStackTrace();
        }
        return tAd;
    }

    protected abstract T newAdInstance();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(serializedAd);
    }
}
