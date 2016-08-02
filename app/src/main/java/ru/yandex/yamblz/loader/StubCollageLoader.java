package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

import static solid.collectors.ToSolidMap.toSolidMap;

public class StubCollageLoader implements CollageLoader {
    private CollageStrategy collageStrategy;
    private static final Scheduler SHED_4 = Schedulers.from(Executors.newFixedThreadPool(4));

    public StubCollageLoader(CollageStrategy collageStrategy) {
        this.collageStrategy = collageStrategy;
    }

    private Bitmap getBitmapFromURL(String url) {
        Log.w("CollageLoader", "getting bitmap");
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
        } catch (IOException e) {
            Log.w("CollageLoader", "got trouble about " + url);
            e.printStackTrace();
        }
        Log.w("CollageLoader", "got bitmap");
        return bitmap;
    }

    private void createObservable(List<String> urls, ImageTarget target) {
        Observable.from(urls)
                .take(4)
                .flatMap(it -> Observable.fromCallable(() -> getBitmapFromURL(it))
                        .subscribeOn(SHED_4))
                .toList()
                .observeOn(Schedulers.computation())
                .map(bitmapList -> collageStrategy.create(bitmapList))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(target::onLoadBitmap, Actions.empty());
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView, collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, new MyimageTarget(imageView), collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        Log.w("StubLoader", "load");
        this.collageStrategy = collageStrategy;
        createObservable(urls, imageTarget);
    }

}
