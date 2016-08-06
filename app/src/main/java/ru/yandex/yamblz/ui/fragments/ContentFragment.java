package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.yamblz.BuildConfig;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.api.YandexArtistApi;
import ru.yandex.yamblz.api.YandexArtistResponse;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.models.Genre;
import ru.yandex.yamblz.ui.adapters.GenreRecyclerViewAdapter;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContentFragment extends BaseFragment {

    private static final String TAG_SAVE_ARRAY = "save array";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private YandexArtistApi artistApi;
    private List<Genre> genreList;
    private GenreRecyclerViewAdapter adapter;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new GenreRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
//        recyclerView.setLayoutManager(gridLayoutManager);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        genreList = new ArrayList<>();

        if (savedInstanceState == null) {
            final OkHttpClient client = new OkHttpClient();
            final OkHttpClient.Builder retrofitClientBuilder = client.newBuilder();

            if (BuildConfig.DEBUG) {
                final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                retrofitClientBuilder.addInterceptor(interceptor);
                retrofitClientBuilder.addNetworkInterceptor(interceptor);
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(YandexArtistApi.URL)
                    .client(retrofitClientBuilder.build())
                    .build();

            artistApi = retrofit.create(YandexArtistApi.class);
            startDownload();
        } else {
            genreList = savedInstanceState.getParcelableArrayList(TAG_SAVE_ARRAY);
            if (genreList != null) adapter.addAllData(genreList);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                CriticalSectionsHandler criticalSectionsHandler = CriticalSectionsManager.getHandler();
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    criticalSectionsHandler.stopSections();
                } else {
                    criticalSectionsHandler.startSection(1);
                }
            }
        });


    }


    private void startDownload() {
//        first variants
        /*Single.fromCallable(this::getResult)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseList -> {
                    Map<String, Genre> genreMap = new HashMap<>();
                    for (YandexArtistResponse yandexArtistResponse : responseList) {
                        for (String keyGenre : yandexArtistResponse.getGenres()) {
                            if (genreMap.get(keyGenre) == null) {
                                Genre genre = new Genre();
                                genre.add(keyGenre, yandexArtistResponse.getName(), yandexArtistResponse.getCover().getSmall());
                                genreMap.put(keyGenre, genre);
                            } else {
                                genreMap.get(keyGenre).add(keyGenre, yandexArtistResponse.getName(), yandexArtistResponse.getCover().getSmall());
                            }
                        }
                    }

                    for (String key : genreMap.keySet()) {
                        genreList.add(genreMap.get(key));
                    }
                    Collections.sort(genreList);
                    adapter.addAllData(genreList);
                });
*/
        Single.fromCallable(this::getResult)
                .subscribeOn(Schedulers.io())
                .flatMapObservable(Observable::from)
                .reduce(new HashMap<String, Genre>(), (genres, yandexArtistResponse) -> {
                    for (String genreStr : yandexArtistResponse.getGenres()) {
                        Genre genre = genres.get(genreStr);
                        if (genre == null) {
                            genres.put(genreStr, genre = new Genre(genreStr));
                        }
                        genre.appendArtist(yandexArtistResponse);
                    }
                    return genres;
                })
                .flatMap(genresMap -> Observable.from(genresMap.values()))
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(genres -> {
                    adapter.addAllData(genres);
                    genreList.addAll(genres);
                });
    }

    private List<YandexArtistResponse> getResult() throws IOException {
        Response<List<YandexArtistResponse>> response;
        response = artistApi.getListArtist()
                .execute();
        return response.body();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TAG_SAVE_ARRAY, (ArrayList<? extends Parcelable>) genreList);
    }
}
