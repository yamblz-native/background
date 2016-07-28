package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.yandex.yamblz.model.Artist;
import ru.yandex.yamblz.model.ArtistFetcher;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class CollageLoaderImpl implements CollageLoader {
    public static final int NUMBER_OF_THREADS = 4;

    public void doo(List<String> urls) {

        urls = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            urls.add(String.valueOf(i));
        }
        Observable<String> urlObs = Observable.from(urls);

        AtomicInteger counter = new AtomicInteger();
        urlObs
                .groupBy(s -> counter.getAndIncrement() % NUMBER_OF_THREADS)
                .flatMap(g -> g.observeOn(Schedulers.newThread()))
                //.forEach(v -> Log.e("Test", "" + Thread.currentThread().getName()))
                .map(this::downloadImage);
    }

    public Bitmap downloadImage(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(response.body().byteStream());
    }

    public Observer<Bitmap> collageMaker = new Observer<Bitmap>() {
        private Bitmap mCollageBitmap;
        private int numberOfRows;    // Строки это
        private int numberOfColumns; // А это столбцы

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Bitmap bitmap) {
            
        }
    };


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