package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import ru.yandex.yamblz.performance.AnyThread;

public interface ImageDownloader {

    @AnyThread
    @Nullable Bitmap downloadBitmap(String url);

}
