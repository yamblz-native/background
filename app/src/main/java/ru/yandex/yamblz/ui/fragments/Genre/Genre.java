package ru.yandex.yamblz.ui.fragments.Genre;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kostya on 26.07.16.
 */

public class Genre {
    private String name;
    private List<Artist> artists = new ArrayList<>();

    public Genre(String name) {
        this.name = name;
    }
    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Artist artist: artists) {
            stringBuilder.append(". ");
            stringBuilder.append(artist.getName());
        }
        return stringBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public void addArtist(Artist artist) {
        artists.add(artist);
    }

    public List<String> getImgUrls() {
        List<String> listUrls = new LinkedList<>();
        for (Artist artist: artists) {
            listUrls.add(artist.getImgUrl());
        }
        return listUrls;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
