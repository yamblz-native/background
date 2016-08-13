package ru.yandex.yamblz.genre.data.source;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.yamblz.genre.api.ArtistsApi;
import ru.yandex.yamblz.genre.data.entity.Artist;
import ru.yandex.yamblz.genre.data.entity.Genre;
import rx.Observable;

public class RemoteDataSource implements DataSource
{
    private static final String ENDPOINT = "http://download.cdn.yandex.net/";
    private ArtistsApi api;

    public RemoteDataSource()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        api = retrofit.create(ArtistsApi.class);
    }

    @Override
    public Observable<List<Artist>> getArtists()
    {
        return api.listArtists();
    }

    @Override
    public Observable<List<Genre>> getGenres()
    {
        return null;
    }

    @Override
    public void delete() {}
}
