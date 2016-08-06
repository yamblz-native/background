package ru.yandex.yamblz.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

/**
 * Created by user on 01.08.16.
 */
@Generated("org.jsonschema2pojo")
public class YandexArtistResponse {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("genres")
    @Expose
    private List<String> genres = new ArrayList<String>();
    @SerializedName("tracks")
    @Expose
    private int tracks;
    @SerializedName("albums")
    @Expose
    private int albums;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cover")
    @Expose
    private Cover cover;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getTracks() {
        return tracks;
    }

    public int getAlbums() {
        return albums;
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
    public String toString() {
        return "YandexArtistResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genres=" + genres +
                ", tracks=" + tracks +
                ", albums=" + albums +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", cover=" + cover +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YandexArtistResponse that = (YandexArtistResponse) o;

        if (id != that.id) return false;
        if (tracks != that.tracks) return false;
        if (albums != that.albums) return false;
        if (!name.equals(that.name)) return false;
        if (!genres.equals(that.genres)) return false;
        if (!link.equals(that.link)) return false;
        if (!description.equals(that.description)) return false;
        if (!cover.equals(that.cover)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + genres.hashCode();
        result = 31 * result + tracks;
        result = 31 * result + albums;
        result = 31 * result + link.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + cover.hashCode();
        return result;
    }

    @Generated("org.jsonschema2pojo")
    public class Cover {

        @SerializedName("small")
        @Expose
        private String small;
        @SerializedName("big")
        @Expose
        private String big;

        public String getSmall() {
            return small;
        }

        public String getBig() {
            return big;
        }

        @Override
        public String toString() {
            return "Cover{" +
                    "small='" + small + '\'' +
                    ", big='" + big + '\'' +
                    '}';
        }

        @Override
        public int hashCode() {
            int result = small.hashCode();
            result = 31 * result + big.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Cover cover = (Cover) o;

            if (!small.equals(cover.small)) return false;
            if (!big.equals(cover.big)) return false;

            return true;
        }
    }
}
