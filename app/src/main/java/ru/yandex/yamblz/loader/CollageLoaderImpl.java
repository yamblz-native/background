package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.List;

import ru.yandex.yamblz.net.NetUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CollageLoaderImpl implements CollageLoader {
    public static final int MAX_IMAGES_PER_COLLAGE = 16;
    private static CollageStrategy collageStrategy = new CollageStrategySquareImpl();
    private LruCache<List<String>, Bitmap> collageCache = new LruCache<>(10);

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
        imageView.setTag(Observable.concat(
                Observable.fromCallable(() -> collageCache.get(urls))
                        .filter(cache -> cache != null),
                downloadAndCreateCollage(collageStrategy, urls))
                .takeFirst(bitmap -> true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageView::setImageBitmap));
    }

    @NonNull
    private Observable<Bitmap> downloadAndCreateCollage(CollageStrategy collageStrategy, List<String> downloadUrls) {
        return Observable.from(downloadUrls)
                .map(NetUtils::getBitmapFromURL)
                .limit(MAX_IMAGES_PER_COLLAGE)
                .toList()
                .observeOn(Schedulers.computation())
                .map(collageStrategy::create)
                .doOnNext(collage -> collageCache.put(downloadUrls, collage));
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {

    }

}
