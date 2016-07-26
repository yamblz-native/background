package ru.yandex.yamblz.ui.fragments.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Artist;
import ru.yandex.yamblz.data.InfoObservable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GenresFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Subscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new Adapter());

        subscription = InfoObservable.getObservable()
                .map(list -> {
                    Set<String> genres = new HashSet<>();
                    for (Artist artist : list) {
                        genres.addAll(artist.genres);
                    }

                    List<Pair<List<Artist>, String>> adapterContent = new ArrayList<>(genres.size());

                    for (String genre : genres) {
                        List<Artist> artists = new ArrayList<>();
                        for (Artist artist : list) {
                            if (artist.genres.contains(genre)) {
                                artists.add(artist);
                            }
                        }
                        adapterContent.add(new Pair<>(artists, genre));
                    }

                    return adapterContent;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapterContent -> {
                    ((Adapter) recyclerView.getAdapter()).setArtistByGenres(adapterContent);
                });

        return view;
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }
}
