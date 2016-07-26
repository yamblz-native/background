package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@SuppressWarnings("WeakerAccess")
public class CollageLoaderImpl implements CollageLoader {

    private static final int DEFAULT_BITMAP_SIZE = 1000;

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView::setImageBitmap);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, new DefaultCollageStrategy(DEFAULT_BITMAP_SIZE));
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, imageView::setImageBitmap, collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        Observable.from(urls)
                .flatMap(CollageLoaderImpl::buildBitmapObservable)
                .toList()
                .map(collageStrategy::create)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageTarget::onLoadBitmap);
    }

    private static Observable<Bitmap> buildBitmapObservable(String url) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(BitmapFactory.decodeStream(
                        new URL(url)
                                .openConnection()
                                .getInputStream()));
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

}
