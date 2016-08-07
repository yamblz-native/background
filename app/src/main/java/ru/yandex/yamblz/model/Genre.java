package ru.yandex.yamblz.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Litun on 20.07.2016.
 */
public class Genre {

    public Genre(String name, Artist artist){
        this.name = name;
        this.artist = artist;
    }

    String name;
    private final Artist artist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist getArtist() {
        return artist;
    }
}
