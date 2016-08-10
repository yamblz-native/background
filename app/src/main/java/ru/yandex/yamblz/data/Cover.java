package ru.yandex.yamblz.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dalexiv on 4/21/16.
 */
/*
    Data class for cover
 */
public class Cover implements Parcelable {
    private String small;
    private String big;

    public Cover() {
    }

    protected Cover(Parcel in) {
        small = in.readString();
        big = in.readString();
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(small);
        dest.writeString(big);
    }

    public static final Creator<Cover> CREATOR = new Creator<Cover>() {
        @Override
        public Cover createFromParcel(Parcel in) {
            return new Cover(in);
        }

        @Override
        public Cover[] newArray(int size) {
            return new Cover[size];
        }
    };
}
