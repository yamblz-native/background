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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static solid.collectors.ToSolidMap.toSolidMap;

public class StubCollageLoader implements CollageLoader {
    private CollageStrategy collageStrategy;
    private static final Scheduler SHED = Schedulers.from(Executors.newFixedThreadPool(4));

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
        List<Bitmap> collage = new ArrayList<>();
        Observable.from(urls)
                .subscribeOn(SHED)
                .take(4)
                .map(this::getBitmapFromURL)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        Log.w("obsCollage", "complete");
                        target.onLoadBitmap(collageStrategy.create(collage));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("obsCollage", "error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Log.w("obsCollage", "next");
                        collage.add(bitmap);
                    }
                });
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
