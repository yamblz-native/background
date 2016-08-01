package ru.yandex.yamblz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Singer implements Parcelable{
    private long id;
    private String genres = "";
    private String link;
    private String cover_small;
    private String cover_big;
    private String name = "";
    private int tracks;
    private int albums;
    private String bio = "";

    public Singer() {}

    /**
     * Create an instance of Singer class from Parcel.
     * Used with intents.
     */
    public Singer(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        cover_small = parcel.readString();
        cover_big = parcel.readString();
        bio = parcel.readString();
        albums = parcel.readInt();
        tracks = parcel.readInt();
        genres = parcel.readString();
    }

    /**
     * Assumed that every performer has unique id.
     */
    @Override
    public boolean equals(Object ob) {
        return ob instanceof Singer && this.id == ((Singer) ob).id;
    }

    @Override
    public int hashCode() {
        return (int)this.id;
    }

    public static final Creator<Singer> CREATOR = new Creator<Singer>() {
        public Singer createFromParcel(Parcel parcel) {
            return new Singer(parcel);
        }

        public Singer[] newArray(int size) {
            return new Singer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write instance of Singer class to parcel.
     * Used with intent.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(cover_small);
        dest.writeString(cover_big);
        dest.writeString(bio);
        dest.writeInt(albums);
        dest.writeInt(tracks);
        dest.writeString(genres);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCover_small(String cover_small) {
        this.cover_small = cover_small;
    }

    public void setCover_big(String cover_big) {
        this.cover_big = cover_big;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public int getTracks() {
        return tracks;
    }

    public String getCover_small() {
        return cover_small;
    }

    public String getCover_big() {
        return cover_big;
    }

    public long getId() {
        return id;
    }

    public String getBio() {
        return bio;
    }

    public List<String> getGenres() {
        ArrayList<String> list = new ArrayList<>();
        int pos = 0;
        String curGenre = "";
        while (pos < genres.length()) {
            if (genres.charAt(pos) != ',' && !Character.isWhitespace(genres.charAt(pos)))
                curGenre += genres.charAt(pos);
            else {
                if (genres.charAt(pos) == ',' || pos == genres.length()-1) {
                    list.add(curGenre);
                    curGenre = "";
                }
            }
            pos++;
        }
        return list;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }
}
