package ru.yandex.yamblz.ui.presenters;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.yandex.yamblz.App;
import ru.yandex.yamblz.data.Genre;
import ru.yandex.yamblz.data.Performer;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.net.IPerformer;
import ru.yandex.yamblz.ui.fragments.GenresFragment;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dalexiv on 8/8/16.
 */

public class GenresPresenter extends Presenter<GenresFragment> {
    private static final String TAG = GenresPresenter.class.getSimpleName();
    @Inject
    IPerformer iPerformer;

    private Subscription loadGenres;
    private List<Genre> genresCache;

    @Override
    public void bindView(@NonNull GenresFragment view) {
        super.bindView(view);
        App.get(view.getActivity()).applicationComponent().inject(this);

        loadGenres = Observable.concat(
                Observable.just(genresCache)
                        .filter(cache -> cache != null),
                loadGenres())
                .takeFirst(genreList -> true)
                .flatMap(Observable::from)
                .compose(applySchedulers())
                .subscribe(genreSubscriber());

    }

    private Subscriber<Genre> genreSubscriber() {
        return new Subscriber<Genre>() {
            @Override
            public void onCompleted() {
                GenresFragment fragment = view();
                if (fragment != null) {
                    fragment.setRefreshing(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "loadGenres:: onError", e);
                GenresFragment fragment = view();
                if (fragment != null) {
                    fragment.notifyUser("Something just happened");
                }
            }

            @Override
            public void onNext(Genre genre) {
                GenresFragment fragment = view();
                if (fragment != null)
                    fragment.addGenre(genre);
            }
        };
    }

    private Observable<List<Genre>> loadGenres() {
        return iPerformer.getPerformers()
                .flatMap(Observable::from)
                .flatMap(this::propagatePerformerToGenres)
                .groupBy(performer -> performer.getGenres()[0])
                .flatMap(groupedObservables -> Observable.zip(
                        Observable.just(groupedObservables.getKey()),
                        groupedObservables.map(performer -> performer.getCover().getSmall()).toList(),
                        Genre::new))
                .toList()
                .doOnNext(genreList -> genresCache = genreList); // Save to cache
    }

    @NonNull
    private Observable<? extends Performer> propagatePerformerToGenres(Performer performer) {
        List<Performer> mappedPerformers = new ArrayList<>(performer.getGenres().length);
        for (String s : performer.getGenres()) {
            Performer perf = new Performer(performer);
            perf.setGenres(new String[]{s});
            mappedPerformers.add(perf);
        }
        return Observable.from(mappedPerformers);
    }

    public void doSwipeToRefresh() {
        // Just abort

        // Is it okay? Should I even care about NPE? How it could be done better?
        GenresFragment view = view();
        if (view != null)
            view.setRefreshing(false);
    }

    public void retrieveGenreSmallImage(Genre genre, ImageView toPost) {
        CriticalSectionsManager.getHandler().postLowPriorityTask(() -> CollageLoaderManager.getLoader().loadCollage(genre.getUrls(), toPost));
    }

    public void startScrolling() {
        CriticalSectionsManager.getHandler().startSection(420);
    }

    public void stopScrolling() {
        CriticalSectionsManager.getHandler().stopSection(420);
    }

    private <T> Observable.Transformer<T, T> applySchedulers() {
        return tObservable -> tObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void unbindView(@NonNull GenresFragment view) {
        super.unbindView(view);
        loadGenres.unsubscribe();
    }
}

