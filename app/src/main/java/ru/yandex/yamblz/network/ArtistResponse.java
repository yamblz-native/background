package ru.yandex.yamblz.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shmakova on 29.07.16.
 */
public class ArtistResponse {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("name")
    @Expose
    public String name;

    public List<String> getGenres() {
        return genres;
    }

    public Cover getCover() {
        return cover;
    }

    @SerializedName("genres")
    @Expose
    public List<String> genres = new ArrayList<String>();
    @SerializedName("tracks")
    @Expose
    public int tracks;
    @SerializedName("albums")
    @Expose
    public int albums;
    @SerializedName("link")
    @Expose
    public String link;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("cover")
    @Expose
    public Cover cover;

    public class Cover {
        @SerializedName("small")
        @Expose
        public String small;
        @SerializedName("big")
        @Expose
        public String big;

        public String getSmall() {
            return small;
        }

        public String getBig() {
            return big;
        }
    }
}
