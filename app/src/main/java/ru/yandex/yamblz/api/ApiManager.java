package ru.yandex.yamblz.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.yamblz.model.Artist;
import rx.Observable;

/**
 * Encapsulates api work.
 * Created by Litun on 21.04.2016.
 */
public class ApiManager {
    private static final String URL = "http://cache-default06d.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/";
    private ApiService service;

    public ApiManager() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    public Observable<List<Artist>> requestArtists() {
        return Observable.just(true)
                .map(this::loadArtists);
    }

    @WorkerThread
    private List<Artist> loadArtists(boolean ignored) {
        try {
            Response<List<Artist>> response = service.listArtist().execute();
            if (response.isSuccessful())
                return response.body();
        } catch (IOException e) {
        }
        return null;
    }

    @WorkerThread
    public Bitmap downloadImageSync(String url) {
        Call<ResponseBody> call = service.downloadImage(url);
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                return BitmapFactory.decodeStream(response.body().byteStream());
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
