package ru.yandex.yamblz.loader;

import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class DefaultCollageLoader implements CollageLoader {

    @Override
    public void loadCollage(int[] ids, ImageView imageView) {
        loadCollage(ids, imageView, null, null);
    }

    @Override
    public void loadCollage(int[] ids, ImageTarget imageTarget) {
        loadCollage(ids, null, imageTarget, null);

    }

    @Override
    public void loadCollage(int[] ids, ImageView imageView, CollageStrategy collageStrategy) {
        loadCollage(ids, imageView, null, collageStrategy);

    }

    @Override
    public void loadCollage(int[] ids, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        loadCollage(ids, null, imageTarget, collageStrategy);
    }


    private void loadCollage(int[] ids, ImageView iv, ImageTarget it, CollageStrategy strategy) {
        if (strategy == null) {
            strategy = new DefaultCollageStrategy();
        }

        WeakReference<ImageView> refImageView = new WeakReference<>(iv);
        WeakReference<ImageTarget> refImageTarget = new WeakReference<>(it);

        asyncLoadCollage(ids, refImageView, refImageTarget, strategy);
    }


    // TODO *async*
    private void asyncLoadCollage(int[] ids,
                                  WeakReference<ImageView> refImageView,
                                  WeakReference<ImageTarget> refImageTarget,
                                  CollageStrategy strategy) {

        ImageView imageView = refImageView.get();
        if (imageView == null) return;

        imageView.setImageResource(ids[0]);
    }
}
