package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.haha.perflib.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class CollageLoaderImpl implements CollageLoader {
    private static final int NUMBER_OF_THREADS = 4;
    private CollageStrategy mDefaultCollageStrategy = new CollageStrategyImpl(); // Какой такой Dagger?

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        List<Bitmap> bitmapList = new ArrayList<>();
        makeCollage(urls, bitmapList, () -> imageView.setImageBitmap(mDefaultCollageStrategy.create(bitmapList)));
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        List<Bitmap> bitmapList = new ArrayList<>();
        makeCollage(urls, bitmapList, () -> imageTarget.onLoadBitmap(mDefaultCollageStrategy.create(bitmapList)));
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        List<Bitmap> bitmapList = new ArrayList<>();
        makeCollage(urls, bitmapList, () -> imageView.setImageBitmap(collageStrategy.create(bitmapList)));
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        List<Bitmap> bitmapList = new ArrayList<>();
        makeCollage(urls, bitmapList, () -> imageTarget.onLoadBitmap(collageStrategy.create(bitmapList)));
    }

    // Как-то костыльно без rx everywhere, но не переписывать же интерфейсы?
    private void makeCollage(List<String> urls, List<Bitmap> bitmapList, @NonNull Action0 onCompleted) {
        collageObservable(urls)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmapList::add, throwable -> {
                }, onCompleted);
    }

    private Observable<Bitmap> collageObservable(List<String> urls) {
        AtomicInteger counter = new AtomicInteger();
        return Observable.from(urls)
                .groupBy(s -> counter.getAndIncrement() % NUMBER_OF_THREADS)
                .flatMap(g -> g.observeOn(Schedulers.newThread()))
                .map(this::downloadImage);
    }

    private Bitmap downloadImage(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            return BitmapFactory.decodeStream(response.body().byteStream());
        } else {
            return null;
        }
    }
}