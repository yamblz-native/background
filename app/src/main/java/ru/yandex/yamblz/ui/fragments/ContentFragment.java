package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.model.Artist;
import ru.yandex.yamblz.model.ArtistsService;
import ru.yandex.yamblz.model.Genre;
import ru.yandex.yamblz.ui.adapters.GenreAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ContentFragment extends BaseFragment {
    @BindView(R.id.genres_list)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Genre> genres = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://download.cdn.yandex.net/")
                .build();
        ArtistsService artistsService = retrofit.create(ArtistsService.class);
        Observable<List<Artist>> artistsObservable = artistsService.listArtists();

        artistsObservable
                .subscribeOn(Schedulers.io())
                .flatMapIterable(artists -> artists)
                .flatMap(new Func1<Artist, Observable<Genre>>() {
                    @Override
                    public Observable<Genre> call(Artist artist) {
                        List<Genre> artistGenres = new ArrayList<>();
                        List<Artist> forGenre = new ArrayList<>();
                        forGenre.add(artist);

                        for (String genre : artist.getGenres()) {
                            artistGenres.add(new Genre(genre, forGenre));
                        }
                        return Observable.from(artistGenres);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        genre -> {
                            for (int i = 0; i < genres.size(); i++) {
                                if (genre.getName().equals(genres.get(i).getName())) {
                                    genres.get(i).addArtist(genre.getArtists().get(0));
                                    return;
                                }
                            }
                            genres.add(genre);
                        },
                        e -> Log.e("RX ERROR", "not good in download"),
                        () -> {
                            recyclerView.requestLayout();
                            Log.e("RX ERROR", "goood");
                        }
                );

        recyclerView.setAdapter(new GenreAdapter(genres));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}