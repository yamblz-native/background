package ru.yandex.yamblz.models;

import java.util.List;

/**
 * Created by shmakova on 29.07.16.
 */

public class Genre {
    private String name;
    private List<String> urls;

    public Genre(String name, List<String> urls) {
        this.name = name;
        this.urls = urls;
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

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                ", urls=" + urls.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;

        Genre genre = (Genre) o;

        if (getName() != null ? !getName().equals(genre.getName()) : genre.getName() != null)
            return false;
        return getUrls() != null ? getUrls().equals(genre.getUrls()) : genre.getUrls() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getUrls() != null ? getUrls().hashCode() : 0);
        return result;
    }
}
