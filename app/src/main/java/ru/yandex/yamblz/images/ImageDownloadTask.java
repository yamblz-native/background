package ru.yandex.yamblz.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by grin3s on 06.08.16.
 */

public class ImageDownloadTask implements Callable<Bitmap> {
    String url;

    public ImageDownloadTask(String url) {
        this.url = url;
    }

    @Override
    public Bitmap call() throws Exception {
        try {
            URL url = new URL(this.url);
            InputStream inputStream = new BufferedInputStream(url.openStream());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
