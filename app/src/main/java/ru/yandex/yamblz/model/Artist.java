package ru.yandex.yamblz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Artist implements Parcelable {
    private int id;
    private String name;
    private List<String> genres;

    @SerializedName("tracks")
    private int countTracks;

    @SerializedName("albums")
    private int countAlbums;
    private String link;
    private String description;
    private Cover cover;

    public Artist(int id, String name, List<String> genres, int countTracks, int countAlbums, String link, String description, Cover cover) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.countTracks = countTracks;
        this.countAlbums = countAlbums;
        this.link = link;
        this.description = description;
        this.cover = cover;
    }

    private Artist() {}

    public List<String> getGenres() {
        return genres;
    }

    public String getSmallCover() {
        return cover.getSmall();
    }

    public String getBigCover() {
        return cover.getBig();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCountTracks() {
        return countTracks;
    }

    public int getCountAlbums() {
        return countAlbums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Cover getCover() {
        return cover;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeString(name);
        dest.writeList(genres);
        dest.writeValue(countTracks);
        dest.writeValue(countAlbums);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeParcelable(cover,0);
    }

    protected Artist(Parcel in) {
        id = (Integer) in.readValue(Integer.class.getClassLoader());
        name = in.readString();
        in.readList(genres,List.class.getClassLoader());
        countTracks = (Integer) in.readValue(Integer.class.getClassLoader());
        countAlbums = (Integer) in.readValue(Integer.class.getClassLoader());
        link = in.readString();
        description = in.readString();
        cover = in.readParcelable(Cover.class.getClassLoader());
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
