package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;

import java.util.List;

public interface CollageStrategy {

    Bitmap create(List<Bitmap> bitmaps);
}
