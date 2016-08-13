package ru.yandex.yamblz.genre.data.source;

import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Artist;
import ru.yandex.yamblz.genre.data.entity.Genre;
import rx.Observable;

/**
 * Created by platon on 27.07.2016.
 */
public interface DataSource
{
    Observable<List<Artist>> getArtists();
    Observable<List<Genre>> getGenres();
    void delete();
}
