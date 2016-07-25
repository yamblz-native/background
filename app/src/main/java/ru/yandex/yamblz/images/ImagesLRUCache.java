package ru.yandex.yamblz.images;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.LruCache;

/**
 * Simple {@link LruCache} wrapper for url2images
 */
public class ImagesLRUCache implements Cache<String, Bitmap> {

    private LruCache<String, Bitmap> mCache;

    /**
     *
     * @param maxSize in bytes
     */
    public ImagesLRUCache(int maxSize) {
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    @Nullable
    public Bitmap get(String key) {
        return mCache.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return mCache.get(key) != null;
    }

    @Override
    @Nullable
    public Bitmap remove(String key) {
        return mCache.remove(key);
    }

    @Override
    public boolean put(String key, Bitmap value) {
        mCache.put(key, value);
        return true;
    }
}
