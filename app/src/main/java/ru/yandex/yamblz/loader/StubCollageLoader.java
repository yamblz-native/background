package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class StubCollageLoader implements CollageLoader {


    private ExecutorService executorService = newFixedThreadPool(10);
    private CollageStrategy defaultCollageStrategy = new DefaultCollageStrategy();

    @Override
    public Subscription loadCollage(List<String> urls, WeakReference<ImageView> imageViewWeakReference) {
        return getCollage(urls, defaultCollageStrategy)
                .subscribe(b -> postBitmapToView(b, imageViewWeakReference));
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageTarget imageTarget) {
        return getCollage(urls, defaultCollageStrategy)
                .subscribe(imageTarget::onLoadBitmap);
    }

    @Override
    public Subscription loadCollage(List<String> urls, WeakReference<ImageView> imageViewWeakReference,
                            CollageStrategy collageStrategy) {
        return getCollage(urls, collageStrategy)
                .subscribe(b -> postBitmapToView(b, imageViewWeakReference));
    }

    @Override
    public Subscription loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        return getCollage(urls, collageStrategy)
                .subscribe(imageTarget::onLoadBitmap);
    }

    private Observable<Bitmap> getCollage(List<String> urls, CollageStrategy strategy) {
        return Observable.from(urls)
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(Schedulers.from(executorService))
                .map(this::downloadBitmap)
                .toList()
                .map(strategy::create);
    }

    private Bitmap downloadBitmap(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Bitmap d = BitmapFactory.decodeStream(is);
            is.close();
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void postBitmapToView(Bitmap b, WeakReference<ImageView> imageViewWeakReference) {
        ImageView imageView = imageViewWeakReference.get();
        if (imageView != null) {
            imageView.post(() -> imageView.setImageBitmap(b));
        }
    }

}
