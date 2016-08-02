package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;


public class StubCollageLoader implements CollageLoader {

    private static final String TAG = "StubCollageLoader";

    private CollageStrategy defaultCollageStrategy = new DefaultCollageStrategy();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public Subscription loadCollage(List<String> urls, WeakReference<ImageView> imageViewWeakReference) {
        Log.d(TAG, "loadCollage: urls count to load " + urls.size());
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
                .observeOn(Schedulers.io())
                .map(this::downloadBitmap)
                .toList()
                .map(strategy::create);
    }

    private Bitmap downloadBitmap(String url) {
        InputStream is = null;
        Bitmap result = null;
        try {
            is = (InputStream) new URL(url).getContent();
            result = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
        }
        return result;
    }


    private void postBitmapToView(Bitmap b, WeakReference<ImageView> imageViewWeakReference) {
        ImageView imageView = imageViewWeakReference.get();
        if (imageView != null) {
            mainHandler.post(() -> imageView.setImageBitmap(b));
        }
    }

    private void closeSilently(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }
}
