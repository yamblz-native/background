package ru.yandex.yamblz.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import rx.Observable;
import timber.log.Timber;

public class Utils {
    private Utils() {

    }

    public static Observable<Bitmap> loadBitmapAsync(String urlString) {
        return Observable.fromCallable(() -> {
            Timber.d("Loading image on thread " + Thread.currentThread().getName());
            InputStream in = null;
            try {
                in = new java.net.URL(urlString).openStream();
                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeConnection(in);
            }
            return null;
        });
    }


    private static void closeConnection(InputStream in) {
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
