package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executors;

import ru.yandex.yamblz.api.ApiManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StubCollageLoader implements CollageLoader {

    private LruCache<List<String>, Bitmap> collageCache = new LruCache<>(20);
    ApiManager apiManager = new ApiManager();
    private CollageStrategy defaultCollageStrategy;

    public StubCollageLoader(@NonNull CollageStrategy defaultCollageStrategy) {
        this.defaultCollageStrategy = defaultCollageStrategy;
    }

    public StubCollageLoader() {
        this(new SimpleCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView, defaultCollageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, defaultCollageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        final WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(imageView);
        loadCollage(urls, bitmap -> {
            ImageView weekImageView = imageViewWeakReference.get();
            if (weekImageView != null)
                weekImageView.setImageBitmap(bitmap);
        }, collageStrategy);

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        Bitmap cachedBitmap = collageCache.get(urls);
        if (cachedBitmap == null)
            Observable.from(urls)
                    .take(4)
                    .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(4)))
                    .map(this::downloadImage)
                    .observeOn(Schedulers.io())
                    .toList()
                    .map(collageStrategy::create)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(imageTarget::onLoadBitmap);
        else
            imageTarget.onLoadBitmap(cachedBitmap);
    }

    @WorkerThread
    private Bitmap downloadImage(String url) {
        return apiManager.downloadImageSync(url);
    }

    private static class CollageData {
        ImageTarget imageTarget;
        CollageStrategy collageStrategy;
        Bitmap collage;
    }
}
