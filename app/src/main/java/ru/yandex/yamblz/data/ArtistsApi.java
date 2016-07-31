package ru.yandex.yamblz.data;

import java.util.List;

import retrofit2.http.GET;
import rx.Single;

/**
 * Created by aleien on 31.07.16.
 */

public interface ArtistsApi {
    @GET("artists.json")
    Single<List<Artist>> getArtists();
}
