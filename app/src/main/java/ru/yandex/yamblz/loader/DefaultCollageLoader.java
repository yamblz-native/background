package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DefaultCollageLoader implements CollageLoader {
    private CollageStrategy defaultStrategy = new SquareCollageStrategy();
    private CompositeSubscription compositeSubscription;
    private Map<Object, Subscription> subscriptionsMap = new WeakHashMap<>();
    private LruCache<List<String>, Bitmap> bitmapCache;


    // Начитался https://habrahabr.ru/post/265997/
    public DefaultCollageLoader(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;

        int cacheSize = (int) (Runtime.getRuntime().maxMemory());

        bitmapCache = new LruCache<List<String>, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(List<String> key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, new SimpleImageTarget(imageView), defaultStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, defaultStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, new SimpleImageTarget(imageView), collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        if (subscriptionsMap.containsKey(imageTarget)) {
            compositeSubscription.remove(subscriptionsMap.get(imageTarget));
            subscriptionsMap.remove(imageTarget);
        }

        Subscription loading = Observable
                .concat(collageFromCache(urls), collageFromNetwork(urls, collageStrategy))
                .first(bitmap -> bitmap != null)
                .doOnNext(bitmap -> bitmapCache.put(urls, bitmap))
                .doOnNext(t ->
                        Log.e("THREAD", " " + Thread.currentThread().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        imageTarget::onLoadBitmap,
                        e -> Log.e("E:DefaultCollageLoader", "Collage downloading failed: " + e),
                        () -> Log.d("D:DefaultCollageLoader", "Collage downloading completed")
                );
        subscriptionsMap.put(imageTarget, loading);
        compositeSubscription.add(loading);
    }

    private Observable<Bitmap> collageFromCache(List<String> urls) {
        return Observable.create(subscriber -> {
            subscriber.onNext(bitmapCache.get(urls));
            subscriber.onCompleted();
        });
    }

    private Observable<Bitmap> collageFromNetwork(List<String> urls, CollageStrategy collageStrategy) {
        return Observable.from(urls)
                .subscribeOn(Schedulers.io())
                .map(this::getBitmapFromURL)
                .toList()
                .map(collageStrategy::create);
    }

    private Bitmap getBitmapFromURL(String src) {
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(src).openConnection();
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e("E:DefaultCollageLoader", "Image downloading failed: " + src);
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}