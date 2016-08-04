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

    public List<String> getGenres() {
        return genres;
    }

    public Cover getCover() {
        return cover;
    }

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
    }
}
