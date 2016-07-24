package ru.yandex.yamblz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Model
 *
 * @author Aleksandra Bobrova
 */
public class ArtistInfo implements Parcelable {
    public static final Creator<ArtistInfo> CREATOR = new Creator<ArtistInfo>() {
        @Override
        public ArtistInfo createFromParcel(Parcel in) {
            return new ArtistInfo(in);
        }

        @Override
        public ArtistInfo[] newArray(int size) {
            return new ArtistInfo[size];
        }
    };
    private long id;
    private String name;
    private List<String> genres;
    private long tracks;
    private long albums;
    private String link;
    private String description;
    private Cover cover;

    public ArtistInfo() {
    }

    protected ArtistInfo(Parcel in) {
        id = in.readLong();
        name = in.readString();
        genres = in.createStringArrayList();
        tracks = in.readLong();
        albums = in.readLong();
        link = in.readString();
        description = in.readString();
        cover = new Cover();
        cover.setBig(in.readString());
        cover.setSmall(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeStringList(genres);
        dest.writeLong(tracks);
        dest.writeLong(albums);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(cover.getBig());
        dest.writeString(cover.getSmall());
    }

    @Override
    public String toString() {
        return "ArtistInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genres=" + genres +
                ", albums=" + albums +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", cover=" + cover +
                '}';
    }

    public long getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public List<String> getGenres() {
        return genres;
    }

    public Cover getCover() {
        return cover;
    }

    public long getAlbums() {
        return albums;
    }

    public String getDescription() {
        return description;
    }

    public long getTracks() {
        return tracks;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setTracks(long tracks) {
        this.tracks = tracks;
    }

    public void setAlbums(long albums) {
        this.albums = albums;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }
}
