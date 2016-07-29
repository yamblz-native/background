package ru.yandex.yamblz.loader;

import android.widget.ImageView;

public interface CollageLoader {

    void loadCollage(int[] ids, ImageView imageView);

    void loadCollage(int[] ids, ImageTarget imageTarget);

    void loadCollage(int[] ids, ImageView imageView, CollageStrategy collageStrategy);

    void loadCollage(int[] ids, ImageTarget imageTarget, CollageStrategy collageStrategy);

}
