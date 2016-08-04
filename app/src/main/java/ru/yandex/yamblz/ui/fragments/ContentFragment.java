package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Artist;
import ru.yandex.yamblz.data.Cover;
import ru.yandex.yamblz.retrofit.ApiServices;
import ru.yandex.yamblz.ui.adapters.FrameItemDecoration;
import ru.yandex.yamblz.ui.adapters.GenresRecyclerAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);;
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
                .doOnNext( artist -> {
                    for (String genre : artist.getGenres()) {
                        if ( genres.get(genre) == null || !genre.contains(genre) )
                            genres.put( genre, new ArrayList<>() );
                        genres.get( genre ).add( artist.getCover() );
                    }
                } )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribeOn( Schedulers.newThread() )
                .toList()
                .subscribe(artists -> {
                    adapter.setItems(genres);
                })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
