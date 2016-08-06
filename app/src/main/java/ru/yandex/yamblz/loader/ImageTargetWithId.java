package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;

public abstract class ImageTargetWithId implements ImageTarget {
    @Override
    public void onLoadBitmap(Bitmap bitmap) {

    }

    public int getId() {
        return hashCode();
    }
}
