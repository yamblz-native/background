package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DefaultCollageLoader implements CollageLoader {
    private static final int MAX_COLLAGE_SIZE = 4;


    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, new DefaultImageTarget(imageView));
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        StripCollageStrategy stripCollageStrategy = new StripCollageStrategy(MAX_COLLAGE_SIZE,
                StripCollageStrategy.HORIZONTAL_STRIPES);
        loadCollage(urls, imageTarget, stripCollageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, new DefaultImageTarget(imageView), collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {

        Subscription subscription = Observable.from(urls)
                .take(MAX_COLLAGE_SIZE)
                .flatMap(this::loadBitmap)
                .toList()
                .map(collageStrategy::create)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageTarget::onLoadBitmap,
                        throwable -> Timber.d(throwable.getMessage()),
                        () -> Timber.d("Completed"));

        imageTarget.setTag(subscription);
    }

    private Observable<Bitmap> loadBitmap(String urlString) {
        return Observable.create(subscriber -> {
                    HttpURLConnection httpURLConnection = null;

                    try {
                        httpURLConnection = (HttpURLConnection) new URL(urlString).openConnection();
                        subscriber.onNext(BitmapFactory.decodeStream(
                                httpURLConnection.getInputStream()));
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    } finally {
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }
                }
        );
    }


}
