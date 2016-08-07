package ru.yandex.yamblz.api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import ru.yandex.yamblz.model.Artist;

/**
 * Created by Litun on 07.08.2016.
 */

public interface ApiService {
    @GET("artists.json")
    Call<List<Artist>> listArtist();

    @GET
    Call<ResponseBody> downloadImage(@Url String fileUrl);
}
