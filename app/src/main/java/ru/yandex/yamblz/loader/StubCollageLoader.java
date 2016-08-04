package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StubCollageLoader implements CollageLoader {
    private LruCache<List<String>, Bitmap> cache;

    public StubCollageLoader() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        final int cacheSize = maxMemory / 8;
        cache = new LruCache<>(cacheSize);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, new MyImageViewTarget(imageView), new MyCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, new MyCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, new MyImageViewTarget(imageView), collageStrategy);

    }


    @Override
    public Subscription loadCollage(List<String> urls, ImageTarget imageTarget,
                                    CollageStrategy collageStrategy) {
        return Observable
                .from(urls)
                .take(100)
                .flatMap(s -> {
                    if (cache.get(urls) == null) {
                        return Observable.fromCallable(() -> BitmapFactory.decodeStream(new URL(s).openStream()));
                    } else
                        return Observable.empty();
                })
                .toList()
                .map(bitmaps -> {
                    if (cache.get(urls) == null) {
                        Bitmap bitmap = collageStrategy.create(bitmaps);
                        cache.put(urls, bitmap);
                        return bitmap;
                    } else {
                        return cache.get(urls);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((imageTarget::onLoadBitmap));
    }
}
