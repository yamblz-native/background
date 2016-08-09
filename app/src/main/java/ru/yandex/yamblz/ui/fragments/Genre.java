package ru.yandex.yamblz.ui.fragments;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dan on 28.07.16.
 */
public class Genre {
    private String genre = "";
    private List<Artist> artists = new ArrayList<>();

    public void addArtist(Artist artist) {
        artists.add(artist);
    }

    public String getNames() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Artist a: artists) {
            stringBuilder.append(a.getName() + ", ");
        }
        return stringBuilder.toString();
    }

    public List<String> getImgUrls() {
        List<String> list = new LinkedList<>();
        for (Artist a: artists) {
            list.add(a.getSmallImg());
        }
        return list;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
