package ru.yandex.yamblz.retrofit;

import java.util.List;

import ru.yandex.yamblz.data.Artist;
import java.util.List;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import rx.Observable;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;
/**
 * Created by Volha on 01.08.2016.
 */

public class ApiServices {

    public final static String API_URL = "http://cache-default04e.cdn.yandex.net/download.cdn.yandex.net/";

    private final ApiWebService webService;

    static {
        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
            @Override
            public void handleError(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public ApiServices() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( API_URL )
                .addConverterFactory( JacksonConverterFactory.create() )
                .addCallAdapterFactory( RxJavaCallAdapterFactory.create() )
                .build();

        webService = retrofit.create( ApiWebService.class );
    }

    public interface ApiWebService {

        @GET("mobilization-2016/artists.json")
        Observable<List<Artist>> getArtists();
    }

    public rx.Observable<List<Artist>> getArtists() {

        return webService.getArtists();
    }


}