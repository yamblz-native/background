package ru.yandex.yamblz.ui.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.StubCriticalSectionsHandler;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageOneOrFour;
import ru.yandex.yamblz.loader.JsonLoad;
import ru.yandex.yamblz.loader.StubCollageLoader;
import ru.yandex.yamblz.model.Singer;
import ru.yandex.yamblz.ui.adapters.FirstRecyclerAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

import solid.stream.Stream;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static solid.collectors.ToSolidMap.toSolidMap;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.genre_list)
    RecyclerView rv;

    private FirstRecyclerAdapter adapter;
    private JsonLoad jsonLoad;

    @TargetApi(Build.VERSION_CODES.M)
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);
        CriticalSectionsHandler handler = CriticalSectionsManager.getHandler();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int number = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_DRAGGING) {
                    handler.startSection(number++);
                }
                if (newState == SCROLL_STATE_IDLE) {
                    handler.stopSections();
                }

            }
        });
        CollageLoader collageLoader = new StubCollageLoader(new CollageOneOrFour());
        adapter = new FirstRecyclerAdapter(null, collageLoader);
        rv.setAdapter(adapter);
        jsonLoad = new JsonLoad();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        createObservable();
    }

    private void createObservable() {

        Observable.fromCallable(() -> jsonLoad.loadSingers())
                .subscribeOn(Schedulers.io())
                .map(this::getGenres)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displaySingers, Actions.empty());
    }

    private void displaySingers(Map<String, Stream<Singer>> genres) {
        Log.w("fragment", "display");
        adapter.setSingers(genres);
        rv.setAdapter(adapter);
    }

    private Map<String, Stream<Singer>> getGenres(List<Singer> singers) {
        Stream<String> genres = Stream.stream(singers).flatMap(Singer::getGenres);
        return genres.collect(toSolidMap(it -> it, it -> Stream.stream(singers).
                filter(value -> value.getGenres().contains(it)))).asMap();
    }


}
