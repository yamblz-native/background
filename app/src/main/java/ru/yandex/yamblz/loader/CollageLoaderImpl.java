package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;
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

    @Override
    public Subscription loadCollage(List<String> urls, ImageView imageView) {
        return loadCollage(urls, new ImageViewImageTarget(imageView));
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageTarget imageTarget) {
        return loadCollage(urls, imageTarget, new DefaultCollageStrategy(DEFAULT_BITMAP_SIZE));
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageView imageView,
                                    CollageStrategy collageStrategy) {
        return loadCollage(urls, new ImageViewImageTarget(imageView), collageStrategy);
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

    private static class ImageViewImageTarget implements ImageTarget {

        WeakReference<ImageView> reference;

        public ImageViewImageTarget(ImageView reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        public void onLoadBitmap(Bitmap bitmap) {
            if (reference.get() != null) {
                reference.get().setImageBitmap(bitmap);
            }
        }
    }

}
