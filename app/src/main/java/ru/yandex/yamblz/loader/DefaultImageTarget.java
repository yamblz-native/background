package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by shmakova on 30.07.16.
 */

public class DefaultImageTarget implements ImageTarget {
    private WeakReference<ImageView> weakReferenceImageView;

    public DefaultImageTarget(ImageView imageView) {
        this.weakReferenceImageView = new WeakReference<>(imageView);
    }


    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        if (weakReferenceImageView != null) {
            ImageView imageView = weakReferenceImageView.get();
            imageView.setImageBitmap(bitmap);
        }
    }
}
