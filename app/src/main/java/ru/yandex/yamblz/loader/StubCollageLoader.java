package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

import static solid.collectors.ToSolidMap.toSolidMap;

public class StubCollageLoader implements CollageLoader {
    private CollageStrategy collageStrategy;
    private Map<ImageTarget, Subscription> subscriptions;
    private static final Scheduler SHED_4 = Schedulers.from(Executors.newFixedThreadPool(4));

    public StubCollageLoader(CollageStrategy collageStrategy) {
        this.collageStrategy = collageStrategy;
        subscriptions = new HashMap<>();
    }

    private Bitmap getBitmapFromURL(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
        } catch (IOException e) {
            Log.w("CollageLoader", "got trouble about " + url);
            e.printStackTrace();
        }
        return bitmap;
    }

    private Subscription createObservable(List<String> urls, ImageTarget target) {
        return Observable.from(urls)
                .take(4)
                .flatMap(it -> Observable.fromCallable(() -> getBitmapFromURL(it))
                        .subscribeOn(SHED_4))
                .toList()
                .observeOn(Schedulers.computation())
                .map(bitmapList -> collageStrategy.create(bitmapList))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(target::onLoadBitmap);
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageView imageView) {
        return loadCollage(urls, imageView, collageStrategy);
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageTarget imageTarget) {
        return loadCollage(urls, imageTarget, collageStrategy);
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        return loadCollage(urls, new MyimageTarget(imageView), collageStrategy);
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        Log.w("Loader", "observer");
        this.collageStrategy = collageStrategy;
        if (subscriptions.containsKey(imageTarget)) {
            subscriptions.get(imageTarget).unsubscribe();
            subscriptions.remove(imageTarget);
        }
        Subscription newSubscription = createObservable(urls, imageTarget);
        subscriptions.put(imageTarget, newSubscription);
        return newSubscription;
    }

}
