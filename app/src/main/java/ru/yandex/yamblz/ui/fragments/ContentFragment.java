package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Artist;
import ru.yandex.yamblz.data.Cover;
import ru.yandex.yamblz.retrofit.ApiServices;
import ru.yandex.yamblz.ui.adapters.FrameItemDecoration;
import ru.yandex.yamblz.ui.adapters.GenresRecyclerAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ContentFragment extends BaseFragment {

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @BindView(R.id.genres)
    RecyclerView genresList;

    GenresRecyclerAdapter adapter;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new GenresRecyclerAdapter();
        genresList.setItemAnimator(new DefaultItemAnimator());
        genresList.setLayoutManager(new LinearLayoutManager(getContext()));
        genresList.addItemDecoration(new FrameItemDecoration());
        genresList.setAdapter(adapter);
        downloadAndMapArtists();
    }

    private void downloadAndMapArtists() {
        Map<String, ArrayList<Cover>> genres = new ConcurrentHashMap<>();
        ApiServices apiServices = new ApiServices();

        compositeSubscription.add(apiServices
                .getArtists()
                .flatMapIterable( artists -> artists )
                .flatMap(getGenreArtistPair()) // создаем пару жанр-артист
                .groupBy(groupByGenre())
                .flatMap(getGenreCoverList()) // получаем пару жанр - список обложек
                .reduce( new HashMap<String, List<Cover>>(), collectAllPairsToMap())
                .retryWhen(errors -> showReloadSnackbar(errors))
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribeOn( Schedulers.newThread() )
                .subscribe(artists -> {
                    adapter.setItems(artists);
                })
        );
    }

    @NonNull
    private Func2<Map<String, List<Cover>>, Pair<String, List<Cover>>, Map<String, List<Cover>>> collectAllPairsToMap() {
        return new Func2<Map<String, List<Cover>>, Pair<String, List<Cover>>, Map<String, List<Cover>>>() {
            @Override
            public Map<String, List<Cover>> call(Map<String, List<Cover>> stringListHashMap, Pair<String, List<Cover>> stringListPair) {
                stringListHashMap.put(stringListPair.first, stringListPair.second);
                return stringListHashMap; // собираем всё вместе в HashMap
            }
        };
    }

    private Observable<Object> showReloadSnackbar(Observable<? extends Throwable> errors) {
        return errors.flatMap( error ->
                Observable.create(
                        subscriber -> Snackbar.make( genresList, R.string.no_data, Snackbar.LENGTH_INDEFINITE )
                                                .setAction( R.string.reload, v -> { subscriber.onNext(null); subscriber.onCompleted(); })
                                                .setActionTextColor( ContextCompat.getColor( getContext(), R.color.colorAccent ) )
                                                .show()));
    }

    @NonNull
    private Func1<GroupedObservable<String, Pair<String, Artist>>, Observable<? extends Pair<String, List<Cover>>>> getGenreCoverList() {
        return spgo -> spgo
                .map(p-> ((Artist) p.second).getCover())
                .toList()
                .map(la-> new Pair<String, List<Cover>>(spgo.getKey(), la));
    }

    @NonNull
    private Func1<Pair<String, Artist>, String> groupByGenre() {
        return new Func1<Pair<String, Artist>, String>() {
            @Override
            public String call(Pair<String, Artist> stringArtistPair) {
                return stringArtistPair.first; // группируем по жанрам
            }
        };
    }

    @NonNull
    private Func1<Artist, Observable<Pair<String, Artist>>> getGenreArtistPair() {
        return new Func1<Artist, Observable<Pair<String, Artist>>>() {
            @Override
            public Observable<Pair<String, Artist>> call(Artist artist) {
                return Observable.from(artist.getGenres()).map(s -> new Pair(s, artist));
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
