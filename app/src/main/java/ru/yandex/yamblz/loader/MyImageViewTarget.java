package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by SerG3z on 02.08.16.
 */

public class MyImageViewTarget implements ImageTarget {
    private final WeakReference<ImageView> imageViewWeakReference;

    public MyImageViewTarget(ImageView imageView) {
        imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
