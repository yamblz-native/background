package ru.yandex.yamblz.loader.interfaces;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageTarget {

    void onLoadBitmap(Bitmap bitmap);
    ImageView getImageView();
    void clear();
}
