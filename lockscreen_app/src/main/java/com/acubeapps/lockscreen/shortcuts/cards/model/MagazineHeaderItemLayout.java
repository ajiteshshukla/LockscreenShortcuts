package com.acubeapps.lockscreen.shortcuts.cards.model;

import com.inmobi.oem.thrift.ad.model.TContent;

import android.os.Parcel;
import android.support.annotation.NonNull;
import com.felipecsl.asymmetricgridview.AsymmetricItem;

import java.util.List;

public class MagazineHeaderItemLayout implements AsymmetricItem {
    private int columnSpan;
    private int rowSpan;
    private int position;
    private List<TContent> magazineHeader;

    public MagazineHeaderItemLayout() {
    }

    public MagazineHeaderItemLayout(int columnSpan, int rowSpan, int position,
                                    List<TContent> magazineHeader) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
        this.magazineHeader = magazineHeader;
    }

    public MagazineHeaderItemLayout(Parcel in) {
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

    public List<TContent> getHeaderContent() {
        return magazineHeader;
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

        //TODO fix this
        byte[] serializedContentAd = in.createByteArray();
//        magazineHeader = new TContent();
//        try {
//            new TDeserializer().deserialize(magazineContent, serializedContentAd);
//        } catch (TException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);
        //TODO fix this
        byte[] serializedContentAd = new byte[0];
//        try {
//            serializedContentAd = new TSerializer().serialize(magazineHeader);
//            dest.writeByteArray(serializedContentAd);
//        } catch (TException e) {
//            e.printStackTrace();
//        }
    }

    /* Parcelable interface implementation */
    public static final Creator<MagazineHeaderItemLayout> CREATOR = new Creator<MagazineHeaderItemLayout>() {

        @Override
        public MagazineHeaderItemLayout createFromParcel(@NonNull Parcel in) {
            return new MagazineHeaderItemLayout(in);
        }

        @Override
        @NonNull
        public MagazineHeaderItemLayout[] newArray(int size) {
            return new MagazineHeaderItemLayout[size];
        }
    };
}
