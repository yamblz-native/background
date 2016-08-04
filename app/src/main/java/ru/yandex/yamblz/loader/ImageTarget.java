package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;

import rx.Subscription;

public interface ImageTarget {

    void onLoadBitmap(Bitmap bitmap);

    void setTag(Subscription subscription);
}
