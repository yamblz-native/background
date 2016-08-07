package ru.yandex.yamblz.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Litun on 21.04.2016.
 */
public class Artist {

    int id;
    String name;
    Collection<String> genres = new ArrayList<>();
    int tracks;
    int albums;
    String link;
    String description;
    Cover cover;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSmallCover() {
        return cover == null ? null : cover.small;
    }

    public Cover getCover() {
        return cover;
    }

    public String getBigCover() {
        return cover == null ? null : cover.big;
    }

    public Collection<String> getGenres() {
        return genres;
    }

    public void setGenres(Collection<String> genres) {
        this.genres = genres;
    }

    public int getAlbums() {
        return albums;
    }

    public int getTracks() {
        return tracks;
    }

    public String getDescription() {
        return description;
    }
}
