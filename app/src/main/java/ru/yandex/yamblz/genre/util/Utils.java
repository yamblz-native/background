package ru.yandex.yamblz.genre.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Artist;
import ru.yandex.yamblz.genre.data.entity.Genre;

/**
 * Created by platon on 27.07.2016.
 */
public class Utils
{
    public static List<Genre> transformArtistToGenres(List<Artist> artists)
    {
        HashMap<String, Genre> genresMap = new HashMap<>();
        List<Genre> genresList = new ArrayList<>();

        for (Artist a : artists)
        {
            for (String s : a.getGenres())
            {
                if (genresMap.containsKey(s))
                {
                    genresMap.get(s).getUrls().add(a.getCover().getCoverUrl());
                }
                else
                {
                    List<String> urls = new ArrayList<>();
                    urls.add(a.getCover().getCoverUrl());
                    Genre genre = new Genre();
                    genre.setName(s);
                    genre.setUrls(urls);
                    genresMap.put(s, genre);
                }
            }
        }
        genresList.addAll(genresMap.values());

        return genresList;
    }
}
