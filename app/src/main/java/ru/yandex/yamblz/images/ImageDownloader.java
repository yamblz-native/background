package ru.yandex.yamblz.images;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import ru.yandex.yamblz.performance.AnyThread;

/**
 * Interface which allows downloading image by the given url
 */
public interface ImageDownloader {

    /**
     * Downloads image by the given url
     * @param url the url
     * @return the image
     */
    @AnyThread
    @Nullable Bitmap downloadBitmap(String url);

}
