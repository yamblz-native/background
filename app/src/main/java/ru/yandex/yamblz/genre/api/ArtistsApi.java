package ru.yandex.yamblz.genre.api;

import java.util.List;

import retrofit2.http.GET;
import ru.yandex.yamblz.genre.data.entity.Artist;
import rx.Observable;

/**
 * Created by platon on 27.07.2016.
 */
public interface ArtistsApi
{
    @GET("mobilization-2016/artists.json")
    Observable<List<Artist>> listArtists();
}
