package ru.yandex.yamblz.data;

import java.util.ArrayList;
import java.util.List;

public class Genre {
    private final String name;
    private final List<Artist> artists;

    public Genre(String name, List<Artist> artists) {
        this.name = name;
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    public List<String> getCollageUrls() {
        List<String> urls = new ArrayList<>();
        for (Artist artist : artists) {
            urls.add(artist.cover.small);
            if (urls.size() >= 4) return urls;
        }

        return urls;
    }
}
