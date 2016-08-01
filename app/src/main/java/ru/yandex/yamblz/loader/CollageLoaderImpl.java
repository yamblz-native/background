package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@SuppressWarnings("WeakerAccess")
public class CollageLoaderImpl implements CollageLoader {

    private static final int DEFAULT_BITMAP_SIZE = 1000;
    private static final int MAX_INPUT_IMAGE_N = 4;
    private static final CollageStrategy defaultCollageStrategy =//
            new DefaultCollageStrategy(DEFAULT_BITMAP_SIZE);

    private static Observable<Bitmap> buildBitmapObservable(String url) {
        return Observable.fromCallable(() -> BitmapFactory.decodeStream(
                new URL(url)
                        .openConnection()
                        .getInputStream()));
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageView imageView) {
        return loadCollage(urls, new WeakImageViewTarget(imageView));
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageTarget imageTarget) {
        return loadCollage(urls, imageTarget, defaultCollageStrategy);
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageView imageView,
                                    CollageStrategy collageStrategy) {
        return loadCollage(urls, new WeakImageViewTarget(imageView), collageStrategy);
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageTarget imageTarget,
                                    CollageStrategy collageStrategy) {
        return Observable.from(urls)
                .filter(url -> url != null)
                .take(MAX_INPUT_IMAGE_N)
                .flatMap(CollageLoaderImpl::buildBitmapObservable)
                .toList()
                .map(collageStrategy::create)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageTarget::onLoadBitmap);
    }

}
