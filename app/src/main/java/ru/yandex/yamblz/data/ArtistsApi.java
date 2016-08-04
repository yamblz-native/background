package ru.yandex.yamblz.data;

import java.util.List;

import retrofit2.http.GET;
import rx.Single;

public interface ArtistsApi {
    @GET("artists.json")
    Single<List<Artist>> getArtists();
}
