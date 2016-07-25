package ru.yandex.yamblz.loader;


import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class DefaultImageTarget implements ImageTarget {
    private WeakReference<ImageView> imageView;

    public DefaultImageTarget(ImageView imageView){
        this.imageView = new WeakReference<>(imageView);
    }
    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        if(imageView!=null){
            imageView.get().setImageBitmap(bitmap);
        }
    }
}
