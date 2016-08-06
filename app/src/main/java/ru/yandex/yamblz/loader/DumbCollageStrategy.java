package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by grin3s on 06.08.16.
 */

public class DumbCollageStrategy implements CollageStrategy {
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        return bitmaps.get(0);
    }
}
