package ru.yandex.yamblz.data;

import java.util.List;

import rx.observables.BlockingObservable;

/**
 * Created by dalexiv on 8/8/16.
 */

public class Genre {
    private String name;
    private List<String> urls;

    public Genre(String name, List<String> urls) {
        this.name = name;
        this.urls = urls;
    }

    // How to refactor that?
    public Genre(String key, BlockingObservable<List<String>> single) {
        this.name = key;
        single.subscribe(list-> this.urls = list);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
