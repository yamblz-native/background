package ru.yandex.yamblz.models;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by grin3s on 06.08.16.
 */

public class Artist {
    public static class Cover {
        String small;
        String big;

        public Cover(String small, String big) {
            this.small = small;
            this.big = big;
        }

        public String getSmall() {
            return small;
        }

        @Override
        public String toString() {
            return "Cover{" +
                    "small='" + small + '\'' +
                    ", big='" + big + '\'' +
                    '}';
        }
    }

    int id;
    String name;
    List<String> genres;
    int tracks;

    int albums;
    String link;
    String description;
    Cover cover;

    public List<String> getGenres() {
        return genres;
    }

    public String getName() {
        return name;
    }

    public Cover getCover() {
        return cover;
    }

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

    public static List<Artist> loadFromJson(InputStream inputStream) throws IOException {
        Type collectionType = new TypeToken<List<Artist>>() {}.getType();
        try {
            List<Artist> resList = new Gson().fromJson(new BufferedReader(new InputStreamReader(inputStream)), collectionType);
            return resList;
        }
        catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genres=" + genres +
                ", tracks=" + tracks +
                ", albums=" + albums +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", cover=" + cover +
                '}';
    }
}
