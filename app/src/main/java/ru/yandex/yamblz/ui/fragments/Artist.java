package ru.yandex.yamblz.ui.fragments;

/**
 * Created by dan on 28.07.16.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Artist implements Serializable {
    private long id ;
    private String name = "";
    private long tracks;
    private long albums;
    private String link = "";
    private String description = "";
    private List<String> genres;
    private String bigImg = "";
    private String smallImg = "";
    private String genresString = "";


    public Artist() {
        genres = new ArrayList<>();
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public List<String> getListGenres() {
        return genres;
    }

    public String getGenres() {
        if (genresString.length() > 0)  {
            return genresString;
        }

        StringBuilder concatination = new StringBuilder();
        for(String s: genres) {
            if (concatination.length() != 0) {
                concatination.append(", ");
            }
            concatination.append(s);

        }
        if (genresString.length() == 0) {
            genresString = concatination.toString();
        }

        return concatination.toString();
    }

    public String getInfo() {
        return String.valueOf(albums) + " альбомов, " + String.valueOf(tracks) + " песен"  ;
    }


    public void setGenresString(String genres) {
        genresString = genres;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTracks(long tracks) {
        this.tracks = tracks;
    }

    public void setAlbums(long albums) {
        this.albums = albums;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return this.id;
    }

    public long getTracks() {
        return this.tracks;
    }

    public long getAlbums() {
        return this.albums;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBigImg() {
        return bigImg;
    }

    public void setBigImg(String bigImg) {
        this.bigImg = bigImg;
    }

    public String getSmallImg() {
        return smallImg;
    }

    public void setSmallImg(String smallImg) {
        this.smallImg = smallImg;
    }
}

