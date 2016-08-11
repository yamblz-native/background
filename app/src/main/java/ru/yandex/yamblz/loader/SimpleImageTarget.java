package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class SimpleImageTarget implements ImageTarget {
    private final WeakReference<ImageView> refImageView;

    public SimpleImageTarget(ImageView imageView) {
        refImageView = new WeakReference<>(imageView);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        ImageView imageView = refImageView.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
