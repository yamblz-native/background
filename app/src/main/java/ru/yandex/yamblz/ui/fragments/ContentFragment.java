package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.api.ApiManager;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.SimpleCollageStrategy;
import ru.yandex.yamblz.model.Artist;
import ru.yandex.yamblz.model.Genre;
import ru.yandex.yamblz.ui.adapters.GenreListAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GenreListAdapter adapter = new GenreListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ApiManager apiManager = new ApiManager();
        apiManager.requestArtists()
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .flatMap(this::toGenres)
                .toMultimap(Genre::getName, genre -> genre.getArtist().getSmallCover())
                .flatMap(map -> Observable.from(map.entrySet()))
                .map(entry -> new GenreListAdapter.GenreCovers(entry.getKey(), entry.getValue()))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::setData);
    }

    private Observable<Genre> toGenres(Artist artist) {
        return Observable.from(artist.getGenres())
                .map(genre -> new Genre(genre, artist));
    }
}
