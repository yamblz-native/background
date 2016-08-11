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
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.DefaultCollageLoader;
import ru.yandex.yamblz.model.Artist;
import ru.yandex.yamblz.model.ArtistsService;
import ru.yandex.yamblz.model.Genre;
import ru.yandex.yamblz.ui.adapters.GenreAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ContentFragment extends BaseFragment {
    @BindView(R.id.genres_list)
    RecyclerView recyclerView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CollageLoaderManager.init(new DefaultCollageLoader(compositeSubscription));
        CriticalSectionsManager.init(null); // add implementation
    }

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
                //http://download.cdn.yandex.net/mobilization-2016/artists.json
                .baseUrl("http://download.cdn.yandex.net/")
                .build();
        ArtistsService artistsService = retrofit.create(ArtistsService.class);
        Observable<List<Artist>> artistsObservable = artistsService.listArtists();

        artistsObservable
                .subscribeOn(Schedulers.io())
                .flatMapIterable(artists -> artists)
                .flatMap(genresWithArtist)
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
                        e -> Log.e("RX artists->genres ", "not good in download" + e),
                        () -> {
                            recyclerView.requestLayout();
                            Log.e("RX artists->genres ", "goood");
                        }
                );
        recyclerView.setAdapter(new GenreAdapter(genres));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    private Func1<Artist, Observable<Genre>> genresWithArtist = artist -> {
        List<Genre> artistGenres = new ArrayList<>();
        List<Artist> forGenre = new ArrayList<>();
        forGenre.add(artist);

        for (String genre : artist.getGenres()) {
            artistGenres.add(new Genre(genre, forGenre));
        }
        return Observable.from(artistGenres);
    };
}