package ru.yandex.yamblz.loader;

import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import ru.yandex.yamblz.model.Artist;
import ru.yandex.yamblz.model.ArtistFetcher;
import rx.Observable;
import rx.schedulers.Schedulers;


public class CollageLoaderImpl implements CollageLoader {
    public static final int NUMBER_OF_THREADS = 4;

    public void doo() {
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            arr.add(String.valueOf(i));
        }
        Observable<String> urlObs = Observable.from(arr);

        AtomicInteger counter = new AtomicInteger();

        urlObs
                .groupBy(s -> counter.getAndIncrement() % NUMBER_OF_THREADS)
                .flatMap(g -> g.observeOn(Schedulers.newThread()))
                .forEach(v -> Log.e("", "" + Thread.currentThread().getName()));
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {

    }

    public void doSomething() throws IOException {
        List<Artist> inputArtists = new ArtistFetcher().getArtistsFromJson();

        Map<String, Set<Artist>> outputGenres = new HashMap<>();

        for (Artist currentArtist : inputArtists) {
            for (String currentGenre : currentArtist.getGenresSet()) {
                if (outputGenres.containsKey(currentGenre)) {
                    outputGenres.get(currentGenre).add(currentArtist);
                } else {
                    Set<Artist> newArtistSet = new HashSet<>();
                    newArtistSet.add(currentArtist);
                    outputGenres.put(currentGenre, newArtistSet);
                }
            }
        }
    }
}