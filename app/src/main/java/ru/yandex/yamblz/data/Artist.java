package ru.yandex.yamblz.data;

import java.util.List;

public class Artist {
    public final int id;
    public final String name;
    public final List<String> genres;
    private final int tracks;
    private final int albums;
    private final String link;
    private final String description;
    final Cover cover;

    public Artist(int id, String name, List<String> genres, int tracks, int albums, String link, String description, Cover cover) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = cover;
    }

    static class Cover {
        final String small;
        final String big;

        public Cover(String small, String big) {
            this.small = small;
            this.big = big;
        }
    }

}
