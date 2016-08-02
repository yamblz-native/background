package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.Observable;
import rx.Subscription;

public interface CollageLoader {

    Observable<Bitmap> loadCollage(List<String> urls);

    Observable<Bitmap> loadCollage(List<String> urls, CollageStrategy collageStrategy);

}
