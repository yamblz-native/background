package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;


public class StubCollageLoader implements CollageLoader {

    private static final String TAG = "StubCollageLoader";

    private CollageStrategy defaultCollageStrategy = new DefaultCollageStrategy();
    private HashMap<Object, Subscription> subscriptionsMap = new HashMap<>();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void loadCollage(List<String> urls, WeakReference<ImageView> imageViewReference) {
        loadCollage(urls, imageViewReference, defaultCollageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, defaultCollageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, WeakReference<ImageView> imageViewReference, CollageStrategy collageStrategy) {
        ImageView imageView = imageViewReference.get();
        if (imageView != null) {
            unsubscribe(imageView);
            Subscription sub = getCollage(urls, collageStrategy)
                    .subscribe(bitmap -> {
                        mainHandler.post(() -> {
                            imageView.setImageBitmap(bitmap);
                        });
                    });
            subscriptionsMap.put(imageView, sub);
        }
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        unsubscribe(imageTarget);
        Subscription sub = getCollage(urls, collageStrategy)
                .subscribe(imageTarget::onLoadBitmap);
        subscriptionsMap.put(imageTarget, sub);
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

    private void closeSilently(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void unsubscribe(Object view) {
        if (subscriptionsMap.containsKey(view)) {
            Subscription subscription = subscriptionsMap.get(view);
            if (subscription != null) {
                subscription.unsubscribe();
            }
        }
    }
}
