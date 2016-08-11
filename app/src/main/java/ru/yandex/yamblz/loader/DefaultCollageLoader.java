package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DefaultCollageLoader implements CollageLoader {
    private CollageStrategy defaultStrategy = new SquareCollageStrategy();
    private CompositeSubscription compositeSubscription;
    private HashMap<Object, Subscription> subscriptionsMap = new HashMap<>();


    // Начитался https://habrahabr.ru/post/265997/
    public DefaultCollageLoader(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView, defaultStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, defaultStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, imageView, bitmap -> imageView.setImageBitmap((Bitmap) bitmap), collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, imageTarget, bitmap -> imageTarget.onLoadBitmap((Bitmap) bitmap), collageStrategy);
    }

    //Работает, но такое ощущение, что можно лучше) хочу критики
    private void loadCollage(List<String> urls, Object o, Action1 setBitmap,
                             CollageStrategy collageStrategy) {
        if (subscriptionsMap.containsKey(o)) {
            compositeSubscription.remove(subscriptionsMap.get(o));
            subscriptionsMap.remove(o);
        }
        Subscription loading = Observable.from(urls)
                .observeOn(Schedulers.io())
                .map(this::getBitmapFromURL)
                .toList()
                .doOnNext(t ->
                        Log.e("THREAD", " " + Thread.currentThread().toString()))
                .map(collageStrategy::create)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(setBitmap,
                        e -> Log.e("E:DefaultCollageLoader", "Collage downloading failed: " + e),
                        () -> Log.d("D:DefaultCollageLoader", "Collage downloading completed")
                );
        subscriptionsMap.put(o, loading);
        compositeSubscription.add(loading);
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
            //Что-то мне не совсем это нравится, прокомментируешь?
            try {
                connection.disconnect();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
