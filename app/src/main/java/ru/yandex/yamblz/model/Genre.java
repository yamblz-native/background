package ru.yandex.yamblz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Genre implements Parcelable {
    private String name;
    private List<Artist> artists;
    private List<String> artistsSmallPhotos = new ArrayList<>();

    public Genre(String name, List<Artist> artists) {
        this.name = name;
        this.artists = artists;
        for (Artist a : artists) {
            artistsSmallPhotos.add(a.getSmallCover());
        }
    }

    private Genre() {
    }

    public String getName() {
        return name;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void addArtist(Artist artist) {
        artists.add(artist);
        artistsSmallPhotos.add(artist.getSmallCover());
    }

    public StringBuilder getArtistString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (artists.size() != 0) {
            for (int i = 0; i < artists.size() - 1; i++) {
                stringBuilder.append(artists.get(i).getName());
                stringBuilder.append(", ");
            }
            stringBuilder.append(artists.get(artists.size() - 1).getName());
            stringBuilder.append(".");
        } else {
            stringBuilder.append("Нет исполнителей данного жанра");
        }
        return stringBuilder;
    }

    public List<String> getArtistsPhotos() {
        return artistsSmallPhotos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(artists);
        dest.writeList(artistsSmallPhotos);
    }

    protected Genre(Parcel in) {
        name = in.readString();
        in.readList(artists, Artist.class.getClassLoader());
    }

    public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel source) {
            return new Genre(source);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };
}
