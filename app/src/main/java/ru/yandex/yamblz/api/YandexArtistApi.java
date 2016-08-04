package ru.yandex.yamblz.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by user on 01.08.16.
 */

public interface YandexArtistApi {

    String URL = "http://download.cdn.yandex.net/";

    @GET("mobilization-2016/artists.json")
    Call<List<YandexArtistResponse>> getListArtist();
}
