package ru.yandex.yamblz.loader;

import android.widget.ImageView;

import java.util.List;

public abstract class CollageLoader implements AsyncLoader{

    public abstract void loadCollage(List<String> urls, ImageView imageView);

    public abstract void loadCollage(List<String> urls, ImageTarget imageTarget);

    public abstract void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy);

    public abstract void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy);

}
