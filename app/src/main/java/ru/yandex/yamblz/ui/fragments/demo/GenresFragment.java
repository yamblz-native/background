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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.InfoObservable;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import solid.collections.SolidSet;
import solid.collectors.ToSolidSet;

import static solid.collectors.ToList.toList;
import static solid.stream.Stream.stream;

public class GenresFragment extends Fragment {

    private static final int CRITICAL_SECTION_ID = 0;

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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    CriticalSectionsManager.getHandler().stopSection(CRITICAL_SECTION_ID);
                } else {
                    CriticalSectionsManager.getHandler().startSection(CRITICAL_SECTION_ID);
                }
            }
        });

        subscription = InfoObservable.getObservable()
                .map(list -> {
                    SolidSet<String> genres = stream(list)
                            .flatMap(artist -> artist.genres)
                            .collect(ToSolidSet.toSolidSet());

                    return genres.map(genre -> new Pair<>(
                            stream(list).filter(artist -> artist.genres.contains(genre))
                                    .collect(toList()),
                            genre)).collect(toList());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapterContent -> {
                    ((Adapter) recyclerView.getAdapter()).setArtistByGenres(adapterContent);
                }, e -> {
                    Log.d(this.getClass().getSimpleName(), "can't download", e);
                    Toast.makeText(getActivity(),
                            getString(R.string.download_error), Toast.LENGTH_SHORT).show();
                });

        return view;
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }
}
