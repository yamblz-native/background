package ru.yandex.yamblz.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.yandex.yamblz.performance.AnyThread;

/**
 * Simple implementation of {@link ImageDownloader}. Uses {@link OkHttpClient} for loading
 */
public class UrlImageDownloader implements ImageDownloader {

    private OkHttpClient mOkHttpClient;

    public UrlImageDownloader(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    @Override
    @AnyThread
    @Nullable
    public Bitmap downloadBitmap(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            return BitmapFactory.decodeStream(response.body().byteStream());
        } catch (IOException e) {
            return null;
        }
    }
}
