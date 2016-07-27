package ru.yandex.yamblz.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Artist implements Comparable<Artist>, Parcelable, Serializable {

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @SerializedName("id")
    private long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("genres")
    private String[] mGenres;
    @SerializedName("tracks")
    private int mCountOfTracks;
    @SerializedName("albums")
    private int mCountOfAlbums;
    @SerializedName("link")
    private String mSiteUrl;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("cover")
    private Map<String, String> mCover;

    protected Artist(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mGenres = in.createStringArray();
        mCountOfTracks = in.readInt();
        mCountOfAlbums = in.readInt();
        mSiteUrl = in.readString();
        mDescription = in.readString();
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getGenres() {
        return TextUtils.join(", ", mGenres);
    }

    // TODO: Можно ли сразу Set?
    public Set<String> getGenresSet() {
        Set<String> set = new HashSet<>();

        Collections.addAll(set, mGenres);

        return set;
    }

    public int getCountOfTracks() {
        return mCountOfTracks;
    }

    public int getCountOfAlbums() {
        return mCountOfAlbums;
    }

    public String getSiteUrl() {
        return mSiteUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getUrlOfBigCover() {
        return mCover.get("big");
    }

    public String getUrlOfSmallCover() {
        return mCover.get("small");
    }

    @Override
    public int compareTo(@NonNull Artist another) {
        return getName().compareTo(another.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist)) return false;

        Artist artist = (Artist) o;

        return mId == artist.mId;

    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeStringArray(mGenres);
        dest.writeInt(mCountOfTracks);
        dest.writeInt(mCountOfAlbums);
        dest.writeString(mSiteUrl);
        dest.writeString(mDescription);
    }
}