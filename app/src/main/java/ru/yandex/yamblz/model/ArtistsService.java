package ru.yandex.yamblz.model;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface ArtistsService {
        @GET("mobilization-2016/artists.json")
        Observable<List<Artist>> listArtists();
}