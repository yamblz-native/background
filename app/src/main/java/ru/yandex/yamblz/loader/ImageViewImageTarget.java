package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Aleksandra on 27/07/16.
 */
public class ImageViewImageTarget implements ImageTarget {
    private WeakReference<ImageView> reference;

    public ImageViewImageTarget(WeakReference<ImageView> reference) {
        this.reference = reference;
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        final ImageView iv = reference.get();
        if (iv != null) {
            iv.setImageBitmap(bitmap);
            iv.invalidate();
        }
    }
}
