package ru.yandex.yamblz.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleien on 31.07.16.
 *
 */

public class Genre {
    private String name;
    private List<Artist> artists;

    public Genre(String name, List<Artist> artists) {
        this.name = name;
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    // TODO: Отдавать на загрузку больше 4-х ссылок
    public List<String> getCollageUrls() {
        List<String> urls = new ArrayList<>();
        for (Artist artist : artists) {
            urls.add(artist.cover.small);
            if (urls.size() >= 4) return urls;
        }

        return urls;
    }
}
