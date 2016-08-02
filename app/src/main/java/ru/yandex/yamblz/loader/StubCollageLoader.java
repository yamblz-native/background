package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;


public class StubCollageLoader implements CollageLoader {

    private static final String TAG = "StubCollageLoader";

    private CollageStrategy defaultCollageStrategy = new DefaultCollageStrategy();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public Observable<Bitmap> loadCollage(List<String> urls) {
        return getCollage(urls, defaultCollageStrategy);
    }

    @Override
    public Observable<Bitmap> loadCollage(List<String> urls, CollageStrategy collageStrategy) {
        return getCollage(urls, collageStrategy);
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
}
