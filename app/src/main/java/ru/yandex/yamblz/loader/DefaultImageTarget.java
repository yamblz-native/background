package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import rx.Subscription;

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
        ImageView imageView = weakReferenceImageView.get();

        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void setTag(Subscription subscription) {
        ImageView imageView = weakReferenceImageView.get();

        if (imageView != null) {
            imageView.setTag(subscription);
        }
    }
}
