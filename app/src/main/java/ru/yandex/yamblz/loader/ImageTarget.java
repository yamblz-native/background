package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;

/**
 * Callback for image
 */
public interface ImageTarget {

    void onLoadBitmap(Bitmap bitmap);
}
