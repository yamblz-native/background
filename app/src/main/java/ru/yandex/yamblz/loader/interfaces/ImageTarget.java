package ru.yandex.yamblz.loader.interfaces;

import android.graphics.Bitmap;

public interface ImageTarget {

    void onLoadBitmap(Bitmap bitmap);
    void clear();
}
