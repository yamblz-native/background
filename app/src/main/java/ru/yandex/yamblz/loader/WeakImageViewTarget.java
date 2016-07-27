package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class WeakImageViewTarget implements ImageTarget {
    private WeakReference<ImageView> reference;

    @SuppressWarnings("WeakerAccess")
    public WeakImageViewTarget(ImageView reference) {
        this.reference = new WeakReference<>(reference);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        if (reference.get() != null) {
            reference.get().setImageBitmap(bitmap);
        }
    }
}
