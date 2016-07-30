package ru.yandex.yamblz.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Genre {
    private String mName;
    private List<Artist> mArtistList = new ArrayList<>();
    private List<String> mSmallCoverUrlList = new ArrayList<>();

    public Genre(@NonNull String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public List<Artist> getArtistList() {
        return mArtistList;
    }


    public List<String> getSmallCoverUrlList() {
        return mSmallCoverUrlList;
    }

    public void addArtist(Artist artist) {
        mArtistList.add(artist);
        mSmallCoverUrlList.add(artist.getUrlOfSmallCover());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;

        Genre genre = (Genre) o;

        return mName.equals(genre.mName) && (mArtistList != null ? mArtistList.equals(genre.mArtistList) : genre.mArtistList == null);

    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + (mArtistList != null ? mArtistList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + mName + '\'' + ';' +
                mArtistList.size() + " in artistList" +
                '}';
    }
}
