package com.acubeapps.lockscreen.shortcuts.cards.model;

import com.inmobi.oem.thrift.ad.model.TContent;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.felipecsl.asymmetricgridview.AsymmetricItem;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

public class MagazineContentItemLayout implements AsymmetricItem {
    private int columnSpan;
    private int rowSpan;
    private int position;
    private TContent magazineContent;

    public MagazineContentItemLayout() {
    }

    public MagazineContentItemLayout(int columnSpan, int rowSpan, int position,
                                     TContent magazineContent) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
        this.magazineContent = magazineContent;
    }

    public MagazineContentItemLayout(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int getColumnSpan() {
        return columnSpan;
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    public TContent getMagazineContent() {
        return magazineContent;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("%s: %sx%s", position, rowSpan, columnSpan);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();
        byte[] serializedContentAd = in.createByteArray();
        magazineContent = new TContent();
        try {
            new TDeserializer().deserialize(magazineContent, serializedContentAd);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);
        byte[] serializedContentAd = new byte[0];
        try {
            serializedContentAd = new TSerializer().serialize(magazineContent);
            dest.writeByteArray(serializedContentAd);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    /* Parcelable interface implementation */
    public static final Parcelable.Creator<MagazineContentItemLayout> CREATOR =
            new Parcelable.Creator<MagazineContentItemLayout>() {

        @Override
        public MagazineContentItemLayout createFromParcel(@NonNull Parcel in) {
            return new MagazineContentItemLayout(in);
        }

        @Override
        @NonNull
        public MagazineContentItemLayout[] newArray(int size) {
            return new MagazineContentItemLayout[size];
        }
    };
}
