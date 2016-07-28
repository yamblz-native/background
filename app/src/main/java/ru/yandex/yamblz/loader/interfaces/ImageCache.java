package ru.yandex.yamblz.loader.interfaces;

import android.graphics.Bitmap;

/**
 * Created by platon on 28.07.2016.
 */
public interface ImageCache<I>
{
    void put(String key, I image);
    I get(String key);
}
