package ru.yandex.yamblz.loader;

import android.widget.ImageView;

import java.util.List;

public interface CollageLoader {

    void loadCollage(List<String> urls, ImageView imageView,String key);

    void loadCollage(List<String> urls, ImageTarget imageTarget,String key);

    void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy,String key);

    void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy,String key);

    void cancel(String key);

    void cancelAll();



}
