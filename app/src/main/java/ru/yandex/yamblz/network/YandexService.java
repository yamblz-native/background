package ru.yandex.yamblz.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by shmakova on 13.04.16.
 */
public interface YandexService {
    @GET("mobilization-2016/artists.json")
    Call<List<ArtistResponse>> getArtistsList();
}
