package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by vorona on 01.08.16.
 */

public class MyimageTarget implements ImageTarget {
    private WeakReference<ImageView> image;

    MyimageTarget(ImageView img) {
        image = new WeakReference<>(img);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        if (image == null) return;
        image.get().setImageBitmap(bitmap);
    }
}
