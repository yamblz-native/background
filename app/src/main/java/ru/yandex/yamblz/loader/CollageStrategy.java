package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;

import java.util.List;

public interface CollageStrategy {
    //занимается склейкой коллажа
    Bitmap create(List<Bitmap> bitmaps);
}
