package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
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
import rx.schedulers.Schedulers;


public class CollageLoaderImpl implements CollageLoader {
    public static final int NUMBER_OF_THREADS = 4;

    public void doo(List<String> urls) {

        //urls = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            //urls.add(String.valueOf(i));
        }

        List<Artist> test = null;
        try {
            test = new ArtistFetcher().getArtistsFromJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
        urls = new ArrayList<>();
        for (int i = 0; i < 42; i++) {
            urls.add(test.get(i).getUrlOfSmallCover());
        }

        Observable<String> urlObs = Observable.from(urls);

        AtomicInteger counter = new AtomicInteger();
        urlObs
                .groupBy(s -> counter.getAndIncrement() % NUMBER_OF_THREADS)
                .flatMap(g -> g.observeOn(Schedulers.newThread()))
                //.forEach(v -> Log.e("Test", "" + Thread.currentThread().getName()))
                .map(this::downloadImage)
                .subscribe(collageMaker);
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
        private static final int NUMBER_OF_COLUMNS = 3;
        private static final int IMAGE_SIZE = 300; // Все входящие картинки считаем квадратными, ибо нефиг
        private static final int COLLAGE_WIDTH = IMAGE_SIZE * NUMBER_OF_COLUMNS;

        private List<Bitmap> mBitmapList = new ArrayList<>();
        private int mNumberOfRows;    // Строки это

        @Override
        public void onCompleted() {
            // Битмап ARGB_8888 размером 1500x1500 занимает 5 мегабайт, а вмещает в себя 5x5 = 25 изображений
            // Значит столбцов будет 5 штук, а строк - сколько получится
            if (mBitmapList.size() >= 3) {
                mNumberOfRows = mBitmapList.size() / NUMBER_OF_COLUMNS; // 10 / 3 = 3, одной картинки не будет, задо будет красивенько
            } else {
                mNumberOfRows = 1;
            }

            Bitmap collage = Bitmap.createBitmap(COLLAGE_WIDTH, IMAGE_SIZE * mNumberOfRows, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(collage);
            int top = 0, left = 0;
            for (Bitmap currentBitmap : mBitmapList) {
                if (left >= COLLAGE_WIDTH) {
                    top += 300;
                    left = 0;
                }
                canvas.drawBitmap(currentBitmap, new Rect(0, 0, 300, 300), new Rect(left, top, left + 300, top + 300), null);
                left += 300;
            }
            canvas.save();
            collage.describeContents();
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Bitmap bitmap) {
            mBitmapList.add(bitmap);
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

    public Map<String, Set<Artist>> doSomething() throws IOException {
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

        return outputGenres;
    }
}