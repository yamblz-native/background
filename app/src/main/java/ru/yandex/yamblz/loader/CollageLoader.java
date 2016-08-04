package ru.yandex.yamblz.loader;

import android.widget.ImageView;

import java.util.List;

import rx.Subscription;

public interface CollageLoader {

    void loadCollage(List<String> urls, ImageView imageView);

    void loadCollage(List<String> urls, ImageTarget imageTarget);

    void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy);

    Subscription loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy);

}
