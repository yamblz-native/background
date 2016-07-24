package ru.yandex.yamblz.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Aleksandra on 25/07/16.
 */
public class Genre {
    private List<String> urls;
    private String name;

    public Genre(List<String> urls, String name) {
        this.urls = urls;
        this.name = name;
    }

    public List<String> getUrls() {
        return urls;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Genre{" +
                "urls=" + Arrays.deepToString(urls.toArray()) +
                ", name='" + name + '\'' +
                '}';
    }
}
