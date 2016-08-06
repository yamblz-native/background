package ru.yandex.yamblz.network;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.yandex.yamblz.model.Artist;

public interface YandexService {

    @GET("artists.json")
    Call<ArrayList<Artist>> getArtists();

}
