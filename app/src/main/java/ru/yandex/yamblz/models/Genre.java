package ru.yandex.yamblz.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by grin3s on 06.08.16.
 */

public class Genre {
    String name;
    List<Artist> artistList;

    public Genre(String name, List<Artist> artistList) {
        this.name = name;
        this.artistList = artistList;
    }

    private void addArtist(Artist artist) {
        artistList.add(artist);
    }


    public static List<Genre> groupArtistsByGenres(List<Artist> inArtists) {
        HashMap<String, Genre> resMap = new HashMap<>();
        for (Artist artist : inArtists) {
            for (String genreName : artist.getGenres()) {
                if (resMap.containsKey(genreName)) {
                    resMap.get(genreName).addArtist(artist);
                }
                else {
                    resMap.put(genreName, new Genre(genreName, new ArrayList<>(Arrays.asList(artist))));
                }
            }
        }

        return new ArrayList<>(resMap.values());
    }

    public String getName() {
        return name;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }
}
