package ru.yandex.yamblz.ui.fragments;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.yandex.yamblz.data.Artist;
import ru.yandex.yamblz.data.ArtistsApi;
import ru.yandex.yamblz.data.Genre;
import ru.yandex.yamblz.ui.presenters.Presenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


class ArtistsLoadingPresenter extends Presenter<ContentFragment> {

    @NonNull private final CompositeSubscription subs = new CompositeSubscription();
    @NonNull private final ArtistsApi artistsApi;

    ArtistsLoadingPresenter(@NonNull ArtistsApi artistsApi) {
        this.artistsApi = artistsApi;
    }

    void loadArtists() {
        subs.add(artistsApi.getArtists()
                .subscribeOn(Schedulers.io())
                .map(this::extractGenres)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showContent,
                        Throwable::printStackTrace));
    }

    private void showContent(List<Genre> list) {
        ContentFragment view = view();
        if (view != null) view.showContent(list);
    }

    private List<Genre> extractGenres(List<Artist> artistsList) {
        ConcurrentHashMap<String, List<Artist>> genres = new ConcurrentHashMap<>();
        List<Genre> genreList = new ArrayList<>();

        for (Artist artist : artistsList) {
            for (String genreName : artist.genres) {
                if (genres.containsKey(genreName)) {
                    genres.get(genreName).add(artist);
                } else {
                    if (genreName != null && !genreName.equals("")) {
                        genres.put(genreName, new ArrayList<Artist>() {{
                            add(artist);
                        }});

                    }
                }
            }
        }

        for (Map.Entry<String, List<Artist>> entry : genres.entrySet()) {
            genreList.add(new Genre(entry.getKey(), entry.getValue()));
        }

        return genreList;

    }
}
