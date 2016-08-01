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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageOneOrFour;
import ru.yandex.yamblz.loader.JsonLoad;
import ru.yandex.yamblz.loader.StubCollageLoader;
import ru.yandex.yamblz.model.Singer;
import ru.yandex.yamblz.ui.adapters.FirstRecyclerAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import rx.Subscription;
import solid.collections.SolidMap;
import solid.stream.Stream;

import static solid.collectors.ToSolidMap.toSolidMap;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.genre_list)
    RecyclerView rv;

    private FirstRecyclerAdapter adapter;
    private JsonLoad jsonLoad;
    private Map<String, Stream<Singer>> _genres;
    private CollageLoader collageLoader;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);

        _genres = new HashMap<>();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        collageLoader = new StubCollageLoader(new CollageOneOrFour());
        adapter = new FirstRecyclerAdapter(_genres, collageLoader);
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
        Observable<List<Singer>> singerObservable = Observable.fromCallable(() -> jsonLoad.loadSingers());
        singerObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<List<Singer>>() {
                            @Override
                            public void onCompleted() {
                                Log.w("obs", "complete");
                                displaySingers();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(List<Singer> singers) {
                                Log.w("obs", "next");

                                Stream<String> genres = Stream.stream(singers).flatMap(Singer::getGenres);
                                _genres = genres.collect(toSolidMap(it -> it, it -> Stream.stream(singers).
                                        filter(value -> value.getGenres().contains(it)))).asMap();
                            }
                        });
    }

    private void displaySingers() {
        Log.w("fragment", "display");
        adapter.setSingers(_genres);
        rv.setAdapter(adapter);
    }

}
