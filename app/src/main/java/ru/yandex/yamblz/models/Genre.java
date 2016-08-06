package ru.yandex.yamblz.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yamblz.api.YandexArtistResponse;

/**
 * Created by SerG3z on 02.08.16.
 */

public class Genre implements Comparable<Genre>, Parcelable {
    private String genre;
    private List<String> listArtist;
    private List<String> imageUrls;

    public Genre(String genreStr) {
        genre = genreStr;
        listArtist = new ArrayList<>();
        imageUrls = new ArrayList<>();
    }

    protected Genre(Parcel in) {
        genre = in.readString();
        listArtist = in.createStringArrayList();
        imageUrls = in.createStringArrayList();
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    public String getGenre() {
        return genre;
    }

    public List<String> getListArtist() {
        return listArtist;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "genre='" + genre + '\'' +
                ", listArtist=" + listArtist +
                ", imageUrls=" + imageUrls +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genre genre1 = (Genre) o;

        return genre.equals(genre1.genre) && listArtist.equals(genre1.listArtist) && imageUrls.equals(genre1.imageUrls);

    }

    @Override
    public int hashCode() {
        int result = genre.hashCode();
        result = 31 * result + listArtist.hashCode();
        result = 31 * result + imageUrls.hashCode();
        return result;
    }

    @Override
    public int compareTo(@NonNull Genre another) {
        return genre.compareToIgnoreCase(another.genre);
    }

    public void appendArtist(YandexArtistResponse yandexArtistResponse) {
        imageUrls.add(yandexArtistResponse.getCover().getSmall());
        listArtist.add(yandexArtistResponse.getName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(genre);
        dest.writeStringList(listArtist);
        dest.writeStringList(imageUrls);
    }
}
