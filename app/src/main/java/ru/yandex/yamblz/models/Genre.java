package ru.yandex.yamblz.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Genre {
    private String genre;
    private List<Singer> singers;

    public Genre(@NonNull String genre, @NonNull List<Singer> singers) {
        this.genre = genre;
        this.singers = singers;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Genre)) {
            return false;
        }
        return getGenre().equals(((Genre) o).getGenre());
    }

    @Override
    public int hashCode() {
        return getGenre().hashCode();
    }

    @Override
    public String toString() {
        return genre;
    }

    public String getGenre() {
        return genre;
    }

    public List<Singer> getSingers() {
        return singers;
    }

    /**
     * Retrieves covers' url for the given genre
     * @param genre the genre
     * @return the urls
     */
    public static List<String> getCoversForCollage(Genre genre) {
        List<String> urls = new ArrayList<>();
        for(Singer singer : genre.singers) {
            urls.add(singer.getCover().getSmall());
        }
        return urls;
    }
}
